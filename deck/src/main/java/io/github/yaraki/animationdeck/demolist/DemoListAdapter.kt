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

package io.github.yaraki.animationdeck.demolist

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.yaraki.animationdeck.R
import io.github.yaraki.animationdeck.demo.DEMOS
import io.github.yaraki.animationdeck.demo.Demo

class DemoListAdapter(private val listener: OnDemoSelectedListener)
    : androidx.recyclerview.widget.RecyclerView.Adapter<DemoListAdapter.ViewHolder>() {

    private val onClickListener = View.OnClickListener {
        val demo = it.getTag(R.id.tag_demo) as Demo
        listener.onDemoSelected(demo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context), parent, onClickListener)
    }

    override fun getItemCount() = DEMOS.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(DEMOS[position])
    }

    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup, listener: View.OnClickListener)
        : androidx.recyclerview.widget.RecyclerView.ViewHolder(inflater.inflate(R.layout.demo_list_item, parent, false)) {

        val title = itemView.findViewById<TextView>(R.id.title)

        init {
            itemView.setOnClickListener(listener)
        }

        fun bind(demo: Demo) {
            itemView.setTag(R.id.tag_demo, demo)
            title.setText(demo.title)
        }

    }

}
