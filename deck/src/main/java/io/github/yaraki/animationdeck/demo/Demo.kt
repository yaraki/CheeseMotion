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

package io.github.yaraki.animationdeck.demo

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import io.github.yaraki.animationdeck.R
import io.github.yaraki.animationdeck.demo.badlayout.BadLayoutFragment
import io.github.yaraki.animationdeck.demo.expand.ExpandFragment
import io.github.yaraki.animationdeck.demo.fade.FadeOutFragment
import io.github.yaraki.animationdeck.demo.fade.ToggleFadeFragment
import io.github.yaraki.animationdeck.demo.objectanimator.ObjectAnimatorFragment
import io.github.yaraki.animationdeck.demo.viewpropertyanimator.ViewPropertyAnimatorFragment

data class Demo(
        @StringRes val title: Int,
        val factory: DemoFactory
)

interface DemoFactory {
    fun create(): Fragment
}

val DEMOS = listOf(
        Demo(R.string.demo_bad_layout, BadLayoutFragment.FACTORY),
        Demo(R.string.demo_view_property_animator, ViewPropertyAnimatorFragment.FACTORY),
        Demo(R.string.demo_object_animator, ObjectAnimatorFragment.FACTORY),
        Demo(R.string.demo_fade_out, FadeOutFragment.FACTORY),
        Demo(R.string.demo_toggle_fade, ToggleFadeFragment.FACTORY),
        Demo(R.string.demo_expand, ExpandFragment.FACTORY)
)
