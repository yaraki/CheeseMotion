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
import android.support.transition.Explode
import android.support.v4.app.Fragment
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnPreDraw

class CheeseListFragment : Fragment() {

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
        // The transition to be used when this fragment is leaving.
        exitTransition = Explode().apply {
            mode = Explode.MODE_OUT
            duration = EXIT_TRANSITION_DURATION
            interpolator = FastOutLinearInInterpolator()
        }
        // The transition to be used when this fragment is brought back.
        reenterTransition = Explode().apply {
            mode = Explode.MODE_IN
            duration = REENTER_TRANSITION_DURATION
            interpolator = LinearOutSlowInInterpolator()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cheese_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Postpone the reenter transition until the grid items are laid out.
        postponeEnterTransition()
        view.findViewById<RecyclerView>(R.id.list).run {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter = CheeseListAdapter { cheeseId, image ->
                activity?.let {
                    if (!it.isFinishing) {
                        (it as Listener).onCheeseSelected(cheeseId, image)
                    }
                }
            }
            // Invoke the postponed transition when the RecyclerView is drawn for the first time.
            doOnPreDraw {
                // Note that this starts the reenter transition despite the method name.
                startPostponedEnterTransition()
            }
        }
    }

}
