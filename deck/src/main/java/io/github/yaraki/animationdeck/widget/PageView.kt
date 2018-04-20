/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.yaraki.animationdeck.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewCompat
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import io.github.yaraki.animationdeck.deck.*

class PageView @JvmOverloads constructor(context: Context,
                                         attrs: AttributeSet? = null,
                                         defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "PageView"
    }

    var size = Size(1920, 1080)
        set(value) {
            field = value
            requestLayout()
        }

    private var _page: Page? = null

    fun setPage(page: Page?, fragmentManager: FragmentManager?) {
        _page = page
        updatePage(fragmentManager)
    }

    var animate = true

    private val textSizes = HashMap<TextView, Int>()

    data class Connection(val startId: Int, val startSide: Int, val endId: Int, val endSide: Int)

    private val connectionMargins = HashMap<Connection, Int>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) {
            applyScales(width.toFloat() / size.x)
            super.onMeasure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(width * size.y / size.x, MeasureSpec.EXACTLY))
        } else if (heightMode == MeasureSpec.AT_MOST || heightMeasureSpec == MeasureSpec.EXACTLY) {
            applyScales(height.toFloat() / size.y)
            super.onMeasure(
                    MeasureSpec.makeMeasureSpec(height * size.x / size.y, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
        } else {
            Log.w(TAG, "Illegal dimension")
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private fun clearScales() {
        textSizes.clear()
        connectionMargins.clear()
    }

    private fun applyScales(magnification: Float) {
        textSizes.forEach { (textView, textSize) ->
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * magnification)
        }
        val constraintSet = ConstraintSet().apply { clone(this@PageView) }
        var changed = false
        connectionMargins.forEach { connection, size ->
            constraintSet.connect(
                    connection.startId, connection.startSide,
                    connection.endId, connection.endSide,
                    (size * magnification).toInt())
            changed = true
        }
        if (changed) {
            constraintSet.applyTo(this)
        }
    }

    private fun <ViewType : View> findOrAdd(tag: String, create: () -> ViewType): ViewType {
        return findViewWithTag(tag) ?: create().apply {
            this.tag = tag
            id = ViewCompat.generateViewId()
            addView(this)
        }
    }

    private fun updatePage(fragmentManager: FragmentManager?) {
        clearScales()
        val currentPage = _page
        if (currentPage == null) {
            removeAllViews()
            return
        }
        val constraintSet = ConstraintSet().apply { clone(this@PageView) }
        for (i in (childCount - 1) downTo 0) {
            val id = getChildAt(i).tag as String?
            if (currentPage.elements.none { it.id == id }) {
                removeViewAt(i)
            }
        }
        val context = context
        currentPage.elements.forEach { element ->
            val view: View? = when (element) {
                is Text -> {
                    val textView = findOrAdd(element.id) {
                        AppCompatTextView(context).apply {
                            setLineSpacing(0f, 1.2f)
                        }
                    }
                    textView.text = element.text
                    textView.setTextColor(element.textColor)
                    textSizes[textView] = element.textSize
                    textView
                }
                is Code -> {
                    val textView = findOrAdd(element.id) {
                        AppCompatTextView(context).apply {
                            typeface = Typeface.MONOSPACE
                        }
                    }
                    textView.text = element.code
                    textView.setTextColor(element.textColor)
                    textSizes[textView] = element.textSize
                    textView
                }
                is FragmentEmbed -> {
                    val frame = findOrAdd(element.id) {
                        FrameLayout(context).apply {
                            setBackgroundColor(Color.WHITE)
                        }
                    }
                    fragmentManager?.run {
                        beginTransaction()
                                .replace(frame.id, element.fragmentClass.newInstance())
                                .commitAllowingStateLoss()
                    }
                    frame
                }
                is BackgroundColor -> {
                    val view = findOrAdd(element.id) { View(context) }
                    view.setBackgroundColor(element.color)
                    view
                }
                else -> {
                    null
                }
            }
            if (view != null) {
                val position = element.position
                ViewCompat.setElevation(view, position.elevation)
                constraintSet.clear(view.id)
                when (position) {
                    is Position.Absolute -> {
                        connectionMargins[Connection(
                                view.id, ConstraintSet.START,
                                ConstraintSet.PARENT_ID, ConstraintSet.START)] = position.x
                        connectionMargins[Connection(
                                view.id, ConstraintSet.TOP,
                                ConstraintSet.PARENT_ID, ConstraintSet.TOP)] = position.y
                        constraintSet.constrainWidth(view.id, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        constraintSet.constrainHeight(view.id, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                    }
                    is Position.Margin -> {
                        connectionMargins[Connection(
                                view.id, ConstraintSet.START,
                                ConstraintSet.PARENT_ID, ConstraintSet.START)] = position.start
                        connectionMargins[Connection(
                                view.id, ConstraintSet.TOP,
                                ConstraintSet.PARENT_ID, ConstraintSet.TOP)] = position.top
                        connectionMargins[Connection(
                                view.id, ConstraintSet.END,
                                ConstraintSet.PARENT_ID, ConstraintSet.END)] = position.end
                        connectionMargins[Connection(
                                view.id, ConstraintSet.BOTTOM,
                                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)] = position.bottom
                        constraintSet.constrainWidth(view.id, ConstraintLayout.LayoutParams.MATCH_PARENT)
                        constraintSet.constrainHeight(view.id, ConstraintLayout.LayoutParams.MATCH_PARENT)
                    }
                    is Position.PageNumber -> {
                        connectionMargins[Connection(
                                view.id, ConstraintSet.END,
                                ConstraintSet.PARENT_ID, ConstraintSet.END)] = position.x
                        connectionMargins[Connection(
                                view.id, ConstraintSet.BOTTOM,
                                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)] = position.y
                        constraintSet.constrainWidth(view.id, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        constraintSet.constrainHeight(view.id, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                    }
                    is Position.Center -> {
                        constraintSet.connect(
                                view.id, ConstraintSet.START,
                                ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
                        constraintSet.connect(
                                view.id, ConstraintSet.END,
                                ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
                        connectionMargins[Connection(
                                view.id, ConstraintSet.TOP,
                                ConstraintSet.PARENT_ID, ConstraintSet.TOP)] = position.y
                        constraintSet.constrainWidth(view.id, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        constraintSet.constrainHeight(view.id, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                    }
                    is Position.CenterBelow -> {
                        constraintSet.connect(
                                view.id, ConstraintSet.START,
                                ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
                        constraintSet.connect(
                                view.id, ConstraintSet.END,
                                ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
                        val target: View? = findViewWithTag(position.id)
                        connectionMargins[Connection(
                                view.id, ConstraintSet.TOP,
                                target?.id
                                        ?: ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)] = position.margin
                        constraintSet.constrainWidth(view.id, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        constraintSet.constrainHeight(view.id, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                    }
                    is Position.Background -> {
                        constraintSet.connect(
                                view.id, ConstraintSet.START,
                                ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
                        constraintSet.connect(
                                view.id, ConstraintSet.END,
                                ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
                        constraintSet.connect(
                                view.id, ConstraintSet.TOP,
                                ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
                        if (position.marginBottom > 0) {
                            connectionMargins[Connection(
                                    view.id, ConstraintSet.BOTTOM,
                                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)] =
                                    position.marginBottom
                        } else {
                            constraintSet.connect(
                                    view.id, ConstraintSet.BOTTOM,
                                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
                        }
                        constraintSet.constrainWidth(view.id, ConstraintLayout.LayoutParams.MATCH_PARENT)
                        constraintSet.constrainHeight(view.id, ConstraintLayout.LayoutParams.MATCH_PARENT)
                    }
                }
            }
        }
        constraintSet.applyTo(this)
    }

}
