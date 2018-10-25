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

package io.github.yaraki.animationdeck.transition

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import androidx.transition.Transition
import androidx.transition.TransitionValues
import android.view.ViewGroup


/**
 * A sample implementation of support [Transition].
 */
class ChangeColor : Transition() {

    companion object {

        /** Key to store a color value in TransitionValues object  */
        private const val PROPNAME_BACKGROUND = "deck:change_color:background"

        private val PROPERTIES = arrayOf(PROPNAME_BACKGROUND)

    }

    override fun getTransitionProperties(): Array<String>? {
        return PROPERTIES
    }

    /**
     * Convenience method: Add the background Drawable property value
     * to the TransitionsValues.value Map for a target.
     */
    private fun captureValues(values: TransitionValues) {
        // Capture the property values of views for later use
        val background = values.view.background
        if (background is ColorDrawable) {
            values.values[PROPNAME_BACKGROUND] = background.color
        }
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    // Create an animation for each target that is in both the starting and ending Scene. For each
    // pair of targets, if their background property value is a color (rather than a graphic),
    // create a ValueAnimator based on an ArgbEvaluator that interpolates between the starting and
    // ending color. Also create an update listener that sets the View background color for each
    // animation frame
    override fun createAnimator(sceneRoot: ViewGroup,
                                startValues: TransitionValues?,
                                endValues: TransitionValues?): Animator? {
        // This transition can only be applied to views that are on both starting and ending scenes.
        if (null == startValues || null == endValues
                || !startValues.values.containsKey(PROPNAME_BACKGROUND)
                || !endValues.values.containsKey(PROPNAME_BACKGROUND)) {
            return null
        }
        // Store a convenient reference to the target. Both the starting and ending layout have the
        // same target.
        val view = endValues.view
        // Store the object containing the background property for both the starting and ending
        // layouts.
        val startBackground = startValues.values[PROPNAME_BACKGROUND] as Int
        val endBackground = endValues.values[PROPNAME_BACKGROUND] as Int
        // If the background color for the target in the starting and ending layouts is
        // different, create an animation.
        if (startBackground != endBackground) {
            // Create a new Animator object to apply to the targets as the transitions framework
            // changes from the starting to the ending layout. Use the class ValueAnimator,
            // which provides a timing pulse to change property values provided to it. The
            // animation runs on the UI thread. The Evaluator controls what type of
            // interpolation is done. In this case, an ArgbEvaluator interpolates between two
            // #argb values, which are specified as the 2nd and 3rd input arguments.
            val animator = ValueAnimator.ofObject(ArgbEvaluator(),
                    startBackground, endBackground)
            // Add an update listener to the Animator object.
            animator.addUpdateListener { animation ->
                val value = animation.animatedValue
                // Each time the ValueAnimator produces a new frame in the animation, change
                // the background color of the target. Ensure that the value isn't null.
                if (null != value) {
                    view.setBackgroundColor(value as Int)
                }
            }
            // Return the Animator object to the transitions framework. As the framework changes
            // between the starting and ending layouts, it applies the animation you've created.
            return animator
        }
        // For non-ColorDrawable backgrounds, we just return null, and no animation will take place.
        return null
    }

}
