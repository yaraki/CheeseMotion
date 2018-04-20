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

package io.github.yaraki.animationdeck

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.support.transition.*
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import io.github.yaraki.animationdeck.transition.ChangeColor
import io.github.yaraki.animationdeck.ui.deck.DeckViewModel
import io.github.yaraki.animationdeck.widget.PageView

class DeckActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PAGE = "page"

        private val INTERPOLATOR_OUT = FastOutLinearInInterpolator()
        private val INTERPOLATOR_COMMON = FastOutSlowInInterpolator()
        private val INTERPOLATOR_IN = LinearOutSlowInInterpolator()

        private val TRANSITION = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(ChangeColor().apply {
                duration = 600
                interpolator = INTERPOLATOR_COMMON
            })
            addTransition(TransitionSet().apply {
                ordering = TransitionSet.ORDERING_SEQUENTIAL
                addTransition(Fade(Fade.OUT).apply {
                    duration = 200
                    interpolator = INTERPOLATOR_OUT
                })
                addTransition(ChangeBounds().apply {
                    duration = 200
                    interpolator = INTERPOLATOR_COMMON
                    setPathMotion(ArcMotion())
                })
                addTransition(Fade(Fade.IN).apply {
                    duration = 200
                    interpolator = INTERPOLATOR_IN
                })
            })
        }
    }

    private lateinit var viewModel: DeckViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deck_activity)

        viewModel = ViewModelProviders.of(this).get(DeckViewModel::class.java)
        val pageView = findViewById<PageView>(R.id.page)
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 19) {
            pageView.setOnLongClickListener {
                Toast.makeText(this, "Generating a PDF", Toast.LENGTH_SHORT).show()
                viewModel.generatePdf(findViewById(R.id.page), null)
                true
            }
        }
        viewModel.currentPage.observe(this, Observer {
            TransitionManager.beginDelayedTransition(pageView, TRANSITION)
            pageView.setPage(it, supportFragmentManager)
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (event?.keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_J -> {
                viewModel.moveToNext()
                true
            }
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_K -> {
                viewModel.moveToPrevious()
                true
            }
            else -> {
                super.onKeyDown(keyCode, event)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

}
