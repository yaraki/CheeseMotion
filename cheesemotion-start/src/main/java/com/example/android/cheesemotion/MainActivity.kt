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
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView

class MainActivity : AppCompatActivity(),
        CheeseListFragment.Listener,
        CheeseDetailFragment.FabHost {

    private lateinit var _fab: FloatingActionButton
    override val fab: FloatingActionButton
        get() = _fab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.toolbar))
        _fab = findViewById(R.id.fab)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, CheeseListFragment.newInstance())
                    .commitNow()
        }
    }

    override fun onCheeseSelected(cheeseId: Int, image: ImageView) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, CheeseDetailFragment.newInstance(cheeseId))
                .addToBackStack(null)
                // TODO: Specify the shared element (image).
                // TODO: Allow reordering for postponing the transition.
                .commit()
    }

}
