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

import android.content.Context
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.transition.ChangeBounds
import androidx.transition.ChangeTransform
import androidx.transition.Slide
import androidx.transition.TransitionSet
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.cheesemotion.common.Cheeses

class CheeseDetailFragment : androidx.fragment.app.Fragment() {

    interface FabHost {
        val fab: FloatingActionButton
    }

    companion object {
        const val ENTER_TRANSITION_DURATION = 225L
        const val RETURN_TRANSITION_DURATION = 150L

        const val IMAGE_TRANSITION_NAME = "cheese_image"

        private const val ARG_CHEESE_ID = "cheese_id"

        fun newInstance(cheeseId: Int) = CheeseDetailFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_CHEESE_ID, cheeseId)
            }
        }
    }

    private var fabHost: FabHost? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The transition to be used for non-shared elements when this fragment is coming up.
        enterTransition = TransitionSet().apply {
            addTransition(Slide(Gravity.BOTTOM).apply {
                duration = ENTER_TRANSITION_DURATION
                interpolator = LinearOutSlowInInterpolator()
            })
        }
        // The transition to be used for non-shared elements when this fragment is leaving.
        returnTransition = TransitionSet().apply {
            addTransition(Slide(Gravity.BOTTOM).apply {
                duration = RETURN_TRANSITION_DURATION
                interpolator = FastOutLinearInInterpolator()
            })
        }
        // The transition to be used for shared elements when this fragment is coming up.
        sharedElementEnterTransition = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            duration = ENTER_TRANSITION_DURATION
            interpolator = FastOutSlowInInterpolator()
            addTransition(ChangeTransform())
            addTransition(ChangeBounds())
        }
        // The transition to be used for shared elements when this fragment is leaving.
        sharedElementReturnTransition = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            duration = RETURN_TRANSITION_DURATION
            interpolator = FastOutSlowInInterpolator()
            addTransition(ChangeTransform())
            addTransition(ChangeBounds())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // The FAB is actually in the activity. This shows the hidden FAB with animation.
        fabHost?.run { fab.show() }
        return inflater.inflate(R.layout.cheese_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Postpone the enter transition until the image is loaded.
        postponeEnterTransition()
        val image: ImageView = view.findViewById(R.id.image)
        val name: TextView = view.findViewById(R.id.name)
        ViewCompat.setTransitionName(image, IMAGE_TRANSITION_NAME)
        // Bind
        val cheese = Cheeses.CHEESES[arguments?.getInt(ARG_CHEESE_ID) ?: 0]
        val resourceId = Cheeses.getDrawableForCheese(cheese)
        Glide.with(this)
                .load(resourceId)
                // We want to handle ImageView with Transition, so disable scaleType transformation
                // in Glide.
                .apply(RequestOptions.noTransformation())
                .listener(onEnd {
                    // Start the postponed transition when Glide finishes loading the image.
                    startPostponedEnterTransition()
                })
                .into(image)
        name.text = cheese
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // This hides the FAB with animation.
        fabHost?.run { fab.hide() }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fabHost = requireActivity() as FabHost
    }

    override fun onDetach() {
        fabHost = null
        super.onDetach()
    }

}
