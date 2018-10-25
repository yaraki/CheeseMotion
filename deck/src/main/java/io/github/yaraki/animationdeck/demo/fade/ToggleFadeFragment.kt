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

package io.github.yaraki.animationdeck.demo.fade

import android.os.Bundle
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import io.github.yaraki.animationdeck.R
import io.github.yaraki.animationdeck.demo.DemoFactory

class ToggleFadeFragment : androidx.fragment.app.Fragment() {

    companion object {
        val FACTORY = object : DemoFactory {
            override fun create() = ToggleFadeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.demo_box_button, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val container: ViewGroup = view.findViewById(R.id.container)
        val box: View = view.findViewById(R.id.box)
        val button: Button = view.findViewById(R.id.animate)
        val fade = Fade().apply { duration = 1000 }
        button.setOnClickListener {
            TransitionManager.beginDelayedTransition(container, fade)
            if (box.visibility == View.VISIBLE) {
                box.visibility = View.INVISIBLE
            } else {
                box.visibility = View.VISIBLE
            }
        }
    }

}
