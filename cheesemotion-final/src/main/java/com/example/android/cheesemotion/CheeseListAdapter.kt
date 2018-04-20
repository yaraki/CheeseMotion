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

import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewGroupCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.cheesemotion.common.Cheeses

class CheeseListAdapter(
        private val onCheeseSelected: (Int, ImageView) -> Unit
) : RecyclerView.Adapter<CheeseListAdapter.CheeseViewHolder>() {

    private val onClickListener = View.OnClickListener {
        // Ungroup the clicked item so the shared element transition can remove the ImageView.
        ViewGroupCompat.setTransitionGroup(it as ViewGroup, false)
        // Pass the ImageView used as the shared element.
        onCheeseSelected(it.getTag(R.id.cheese_id) as Int, it.findViewById(R.id.image))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            CheeseViewHolder(parent, onClickListener)

    override fun getItemCount() = Cheeses.CHEESES.size

    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) {
        holder.bind(position, Cheeses.CHEESES[position])
    }


    class CheeseViewHolder(parent: ViewGroup, onClickListener: View.OnClickListener)
        : RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.cheese_list_item, parent, false)) {

        private val image = itemView.findViewById<ImageView>(R.id.image)
        private val name = itemView.findViewById<TextView>(R.id.name)

        init {
            itemView.setOnClickListener(onClickListener)
            // Treat the whole item as a transition group. This is specifically important when
            // you are using Slide or Explode as an exit fragment transition.
            ViewGroupCompat.setTransitionGroup(itemView as ViewGroup, true)
        }

        fun bind(id: Int, cheese: String) {
            itemView.setTag(R.id.cheese_id, id)
            val resourceId = Cheeses.getDrawableForCheese(cheese)
            // We want to handle ImageView with Transition, so disable scaleType transformation
            // in Glide.
            Glide.with(image).load(resourceId).apply(RequestOptions.noTransformation()).into(image)
            // Each of the possible candidates of shared element transition has to have a unique
            // transition name.
            ViewCompat.setTransitionName(image, "image-$id")
            name.text = cheese
        }

    }

}
