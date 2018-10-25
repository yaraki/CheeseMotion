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

package io.github.yaraki.animationdeck.demo.badlayout

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import io.github.yaraki.animationdeck.R
import io.github.yaraki.animationdeck.demo.DemoFactory

class BadLayoutFragment : Fragment() {

    companion object {
        val FACTORY = object : DemoFactory {
            override fun create() = BadLayoutFragment()
        }
    }

    private lateinit var box: View
    private var animator: ValueAnimator? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.demo_box, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        box = view.findViewById(R.id.box)
    }

    override fun onResume() {
        super.onResume()
        startAnimation()
    }

    override fun onPause() {
        stopAnimation()
        super.onPause()
    }

    private fun startAnimation() {
        val initialLeftMargin = (box.layoutParams as FrameLayout.LayoutParams).leftMargin
        animator = ValueAnimator.ofInt(0, 500).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener {
                // ######################################################################
                // # Caution: This is a bad example. You should never do this.          #
                // # Modifying LayoutParams invokes layout. It is bad for performance.  #
                // # Use translationX/Y instead.                                        #
                // ######################################################################
                val lp = box.layoutParams as FrameLayout.LayoutParams
                lp.setMargins(initialLeftMargin + (it.animatedValue as Int), lp.topMargin,
                        lp.rightMargin, lp.bottomMargin)
                box.layoutParams = lp
            }
            start()
        }
    }

    private fun stopAnimation() {
        animator?.end()
        animator = null
    }

}
