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

package io.github.yaraki.animationdeck.deck

import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.v4.app.Fragment

// Models

data class Size(val x: Int, val y: Int)

sealed class Position {
    abstract val elevation: Float

    data class Absolute(val x: Int, val y: Int) : Position() {
        override val elevation = 8f
    }

    data class Margin(val start: Int, val top: Int, val end: Int, val bottom: Int) : Position() {
        override val elevation = 8f
    }

    data class PageNumber(val x: Int, val y: Int) : Position() {
        override val elevation = 2f
    }

    data class Center(val y: Int) : Position() {
        override val elevation = 8f
    }

    data class CenterBelow(val id: String, val margin: Int) : Position() {
        override val elevation = 8f
    }

    data class Background(val marginBottom: Int = 0) : Position() {
        override val elevation = 4f
    }
}

data class Deck(val size: Size, val pages: List<Page>)

interface Element {
    val id: String
    val position: Position
}

data class Text(
        override val id: String,
        override val position: Position,
        val text: String,
        val textSize: Int,
        @ColorInt val textColor: Int
) : Element

data class Code(
        override val id: String,
        override val position: Position,
        val code: String,
        val textSize: Int,
        @ColorInt val textColor: Int
) : Element

data class FragmentEmbed(
        override val id: String,
        override val position: Position,
        val fragmentClass: Class<out Fragment>
) : Element

data class BackgroundColor(
        override val id: String,
        override val position: Position,
        @ColorInt val color: Int
) : Element

data class Page(val elements: List<Element>)

// Builders & DSL

@DslMarker
annotation class DeckMarker

@DeckMarker
class StandardDeckBuilder {
    var title = "(No title)"
    var subtitle = ""
    var body = ""
    private val pages = mutableListOf<Page>()
    private val colors = listOf(
            Color.rgb(0x21, 0x96, 0xF3),
            Color.rgb(0xF4, 0x43, 0x36),
            Color.rgb(0x00, 0x96, 0x88),
            Color.rgb(0xFF, 0x57, 0x22),
            Color.rgb(0xE9, 0x1E, 0x63),
            Color.rgb(0x9C, 0x27, 0xB0),
            Color.rgb(0xEF, 0x6C, 0x00),
            Color.rgb(0x67, 0x3A, 0xB7),
            Color.rgb(0x60, 0x7D, 0x8B),
            Color.rgb(0x03, 0x9B, 0xE5),
            Color.rgb(0x00, 0x83, 0x8F),
            Color.rgb(0x43, 0xA0, 0x47),
            Color.rgb(0x75, 0x75, 0x75),
            Color.rgb(0x82, 0x77, 0x17),
            Color.rgb(0x79, 0x55, 0x48),
            Color.rgb(0x68, 0x9F, 0x38))
    private var colorIndex = 0

    fun build(): Deck {
        if (body.isNotEmpty()) {
            pages.add(0, Page(listOf(
                    BackgroundColor("bg", Position.Background(848), colors[0]),
                    Text("deck-title", Position.Absolute(256, 40), title, 100, Color.WHITE),
                    Text("deck-body", Position.Absolute(256, 300), body.trimIndent(), 60, Color.BLACK))))
        }
        pages.add(0, Page(listOf(
                BackgroundColor("bg", Position.Background(), colors[0]),
                Text("deck-title", Position.Center(400), title, 100, Color.WHITE),
                Text("deck-subtitle", Position.Center(550), subtitle, 60, Color.WHITE))))
        return Deck(Size(1920, 1200), pages.mapIndexed { i, page ->
            Page(page.elements + listOf(
                    Text("page", Position.PageNumber(64, 32), "${i+1}/${pages.size}", 40, Color.GRAY)))
        })
    }

    fun chapter(content: ChapterBuilder.() -> Unit) {
        val builder = ChapterBuilder()
        builder.backgroundColor = colors[++colorIndex % colors.size]
        builder.content()
        pages.addAll(builder.build())
    }
}

@DeckMarker
class ChapterBuilder {
    var title = "(No title)"
    var body = ""
    var backgroundColor = Color.rgb(0xf4, 0x43, 0x36)
    private val pages = mutableListOf<Page>()

    fun build(): List<Page> {
        if (body.isNotEmpty()) {
            pages.add(0, Page(listOf(
                    BackgroundColor("bg", Position.Background(848), backgroundColor),
                    Text("chapter-title", Position.Absolute(256, 40), title, 100, Color.WHITE),
                    Text("chapter-body", Position.Absolute(256, 300), body.trimIndent(), 60, Color.BLACK))))
        }
        pages.add(0, Page(listOf(
                BackgroundColor("bg", Position.Background(), backgroundColor),
                Text("chapter-title", Position.Center(400), title, 100, Color.WHITE))))
        return pages
    }

    fun section(content: SectionBuilder.() -> Unit) {
        val builder = SectionBuilder()
        builder.backgroundColor = backgroundColor
        builder.content()
        pages.add(builder.build())
    }

    fun code(content: CodeBuilder.() -> Unit) {
        val builder = CodeBuilder()
        builder.backgroundColor = backgroundColor
        builder.content()
        pages.add(builder.build())
    }
}

@DeckMarker
class SectionBuilder {
    var title = "(No title)"
    var body = ""
    var backgroundColor = Color.rgb(0xf4, 0x43, 0x36)

    fun build(): Page {
        return Page(listOf(
                BackgroundColor("bg", Position.Background(848), backgroundColor),
                Text("$title-title", Position.Absolute(256, 40), title, 100, Color.WHITE),
                Text("$title-body", Position.Absolute(256, 300), body.trimIndent(), 60, Color.BLACK)))
    }
}

@DeckMarker
class CodeBuilder {
    var body = ""
    var backgroundColor = Color.rgb(0xf4, 0x43, 0x36)
    var demo: Class<out Fragment>? = null
    fun build(): Page {
        val elements = mutableListOf(
                BackgroundColor("bg", Position.Background(1024), backgroundColor),
                Code("code", Position.Absolute(128, 128), body.trimIndent(), 56, Color.BLACK))
        demo?.let {
            elements.add(FragmentEmbed("demo", Position.CenterBelow("code", 60), it))
        }
        return Page(elements)
    }
}

fun standardDeck(content: StandardDeckBuilder.() -> Unit): Deck {
    val builder = StandardDeckBuilder()
    builder.content()
    return builder.build()
}
