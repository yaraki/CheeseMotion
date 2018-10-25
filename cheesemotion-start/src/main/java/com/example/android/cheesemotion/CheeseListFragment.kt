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

package com.example.android.cheesemotion

import android.os.Bundle
import androidx.transition.Explode
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnPreDraw

class CheeseListFragment : androidx.fragment.app.Fragment() {

    companion object {
        const val EXIT_TRANSITION_DURATION = 150L
        const val REENTER_TRANSITION_DURATION = 225L
        fun newInstance() = CheeseListFragment()
    }

    interface Listener {
        fun onCheeseSelected(cheeseId: Int, image: ImageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Configure the exit transition (Explode, Slide or Fade).
        // TODO: Configure the reenter transition (Explode, Slide or Fade).
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cheese_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO: Postpone the reenter transition.
        view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.list).run {
            setHasFixedSize(true)
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
            adapter = CheeseListAdapter { cheeseId, image ->
                activity?.let {
                    if (!it.isFinishing) {
                        (it as Listener).onCheeseSelected(cheeseId, image)
                    }
                }
            }
            // Invoke the postponed transition when the RecyclerView is drawn for the first time.
            doOnPreDraw {
                // TODO: Start the postponed reenter transition.
            }
        }
    }

}
