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

package io.github.yaraki.animationdeck.demo.expand

import android.os.Bundle
import android.support.transition.ChangeBounds
import android.support.transition.Fade
import android.support.transition.TransitionManager
import android.support.transition.TransitionSet
import android.support.v4.app.Fragment
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import io.github.yaraki.animationdeck.R
import io.github.yaraki.animationdeck.demo.DemoFactory

class ExpandFragment : Fragment() {

    companion object {

        val FACTORY = object : DemoFactory {
            override fun create() = ExpandFragment()
        }

        private val INTERPOLATOR_OUT = FastOutLinearInInterpolator()
        private val INTERPOLATOR_COMMON = FastOutSlowInInterpolator()
        private val INTERPOLATOR_IN = LinearOutSlowInInterpolator()

        val TRANSITION = TransitionSet().apply {
            duration = 150
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(Fade(Fade.IN).apply {
                interpolator = INTERPOLATOR_IN
            })
            addTransition(Fade(Fade.OUT).apply {
                interpolator = INTERPOLATOR_OUT
            })
            addTransition(ChangeBounds().apply {
                interpolator = INTERPOLATOR_COMMON
            })
        }
    }

    private var defaultHeight = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        defaultHeight = resources.getDimensionPixelSize(R.dimen.box_size)
        return inflater.inflate(R.layout.demo_expand, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val expand: FrameLayout = view.findViewById(R.id.expand)
        val scrim: View = view.findViewById(R.id.scrim)
        expand.setOnClickListener {
            TransitionManager.beginDelayedTransition(expand, TRANSITION)
            val params = expand.layoutParams
            if (params.height == FrameLayout.LayoutParams.WRAP_CONTENT) {
                params.height = defaultHeight
                scrim.visibility = View.VISIBLE
            } else {
                params.height = FrameLayout.LayoutParams.WRAP_CONTENT
                scrim.visibility = View.INVISIBLE
            }
            expand.layoutParams = params
        }
    }

}
