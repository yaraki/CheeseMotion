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

package io.github.yaraki.animationdeck.ui.deck

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.github.yaraki.animationdeck.deck.Page
import io.github.yaraki.animationdeck.deck.standardDeck
import io.github.yaraki.animationdeck.demo.fade.FadeOutFragment
import io.github.yaraki.animationdeck.demo.fade.ToggleFadeFragment
import io.github.yaraki.animationdeck.demo.objectanimator.ObjectAnimatorFragment
import io.github.yaraki.animationdeck.widget.PageView
import kotlin.concurrent.thread

class DeckViewModel : ViewModel() {

    private val deck = standardDeck {
        title = "Android Animation"
        subtitle = "droid girls #11 / Apr 20 / @yuichi_araki"
        body = """
            今日話すこと

            ・アニメーションの前提知識
            ・アニメーションの基本機能
            ・Transition API
              ・Fragment Transition
              ・Shared Element Fragment Transition
        """
        chapter {
            title = "アニメーションの前に"
            body = """
                Android の View の表示位置を決める要素

                大きくわけて
                - レイアウト
                - 変形 (Transformation)
            """
            section {
                title = "レイアウト"
                body = """
                    指定する
                    - ViewGroup.LayoutParams / android:layout_なんとか
                    計算する
                    - ViewGroup.onMeasure: 子 View の大きさを測る
                    - ViewGroup.onLayout: 子 View を配置する
                    取得する
                    - View.getLeft/Top/Right/Bottom()
                """
            }
            section {
                title = "大切なこと"
                body = """
                    レイアウトは遅い (60 FPS に間に合わない)

                    アニメーション中にレイアウトを起こしてはいけない
                """
            }
            section {
                title = "レイアウトを起こす動作"
                body = """
                    - View.requestLayout()
                    - View.setLayoutParams()
                    - View.setPadding()
                    - View の大きさが変わる操作一般
                    など多数
                """
            }
            section {
                title = "offsetLeftAndRight/TopAndBottom"
                body = """
                    ViewCompat.offsetLeftAndRight(View, int)
                    ViewCompat.offsetTopAndBottom(View, int)

                    レイアウトを起こさずに、レイアウト位置をずらす
                """
            }
            section {
                title = "変形 (Transformation)"
                body = """
                    レイアウトの位置を基準にして変形する

                    - 平行移動: View.setTranslationX/Y(float)
                    - 回転: View.setRotation(), setPivotX/Y()
                    - 拡大縮小: View.setScaleX/Y()

                    取得する
                    - View.getX/Y()
                """
            }
            section {
                title = "大切なこと"
                body = """
                    アニメーション中にレイアウトを起こしては行けない

                    View を動かしたいときは offset〜 または 変形を使う
                """
            }
        }
        chapter {
            title = "様々なアニメーション"
            body = """
                - Animation
                - Animator
                - ViewPropertyAnimator
                - DynamicAnimation
                - LayoutTransition
                - Transition
                  - Shared Element Transition
            """
            section {
                title = "基本的なアニメーション"
                body = """
                    - Animation
                    - Animator
                    - ViewPropertyAnimator

                    duration: 持続時間
                    interpolator
                    リスナー
                """
            }
            section {
                title = "Animation"
                body = """
                    android.view.animation.Animation
                    忘れよう

                    ViewSwitcher, TextSwitcher
                    Activity.overridePendingTransition(),
                    Fragment.setCustomAnimations()
                """
            }
            section {
                title = "Animator"
                body = """
                    android.animation.Animator

                    ValueAnimator: 値をアニメーションする
                    ObjectAnimator: 値をアニメーションし、対象にセットする
                    AnimatorSet: 複数の Animator をまとめる
                    TimeAnimator: 時間経過
                """
            }
            section {
                title = "ViewPropertyAnimator"
                body = """
                    View.animate()
                    - alpha(), translationX/Y(), scaleX/Y(), rotation()

                    Animator ではない

                    簡単なアニメーションには便利だが、応用度が低い
                    ViewPropertyAnimator でできることは
                    ObjectAnimator でもできる
                """
            }
            section {
                title = "DynamicAnimation"
                body = """
                    android.support.animation.DynamicAnimation

                    物理法則に基づいたアニメーション

                    - FlingAnimation
                    - SpringAnimation
                """
            }
            section {
                title = "Transition"
                body = """
                    画面の状態の差を何でもアニメーション

                    Animator に基づいた高度 API
                """
            }
            section {
                title = "LayoutTransition"
                body = """
                    android.animation.LayoutTransition
                    android:animateLayoutChanges="true"

                    addView(), removeView(), setVisibility()
                    などをアニメーション

                    Transition ではない
                    LayoutTransition でできることは Transition でもできる
                """
            }
            section {
                title = "アニメーション機能まとめ"
                body = """
                    ユーザーのドラッグ・スワイプに従ってアニメーション
                     → DynamicAnimation

                    画面遷移をアニメーション
                     → Transition
                        カスタムの Transition が必要？
                        → ObjectAnimator
                """
            }
        }
        chapter {
            title = "ObjectAnimator"
            body = """
                値をアニメーションし、対象に値をセットする

                値: Int, Float, etc
                対象: 主に View
                セットする: android.util.Property#set(), get()
            """
            code {
                body = """
                    val box = view.findViewById<View>(R.id.box)

                    val animator = ObjectAnimator
                      .ofFloat(box, View.TRANSLATION_X, 0f, 100f)
                    animator.start()
                """
                demo = ObjectAnimatorFragment::class.java
            }
        }
        chapter {
            title = "Transition の基本"
            body = """
                画面の状態の変化をアニメーション

                - 普通の Transition
                - Fragment Transition
                  - Shared Element Transition
                - Activity Transition
                  - Shared Element Transition
            """
            code {
                body = """
                    val box: View = findViewById(...)
                    val container: ViewGroup = findViewById(...)

                    TransitionManager
                      .beginDelayedTransition(container, Fade())
                    box.visibility = View.INVISIBLE
                """
                demo = FadeOutFragment::class.java
            }
            section {
                title = "Transition の仕組み"
                body = """
                    ある ViewGroup 内の View それぞれに対して

                    1. 変化前の状態を記録する
                    2. 変化後の状態を記録する
                    3. 2つの状態の間をつなぐ Animator を生成する

                    → Animator をまとめて実行
                """
            }
            code {
                body = """
                    TM.beginDelayedTransition(container, Fade())
                    if (box.visibility == View.VISIBLE) {
                      box.visibility = View.INVISIBLE
                    } else {
                      box.visibility = View.VISIBLE
                    }
                """
                demo = ToggleFadeFragment::class.java
            }
        }
        chapter {
            title = "様々な Transition"
            body = """
                ChangeBounds, ChangeTransform
                Fade, Slide, Explode
                ChangeClipBounds
                ChangeScroll
                ChangeImageTransform
                TransitionSet
                AutoTransition
            """
            section {
                title = "ChangeBounds, ChangeTransform"
                body = """
                    ChangeBounds
                    View のレイアウト位置・大きさ

                    ChangeTransform
                    View の変形 (Transformation) 位置
                """
            }
            section {
                title = "Fade, Slide, Explode"
                body = """
                    View の存在 / Visibility の変更

                    Fade: フェードイン・フェードアウト
                    Slide: スライドイン・スライドアウト
                    Explode: 中心点と画面外の移動
                """
            }
            section {
                title = "その他"
                body = """
                    ChangeClipBounds
                    clipBounds

                    ChangeScroll
                    スクロール位置

                    ChangeImageTransform
                    ImageView の scaleType
                """
            }
            section {
                title = "TransitionSet, AutoTransition"
                body = """
                    TransitionSet
                    複数の Transition をまとめて、同時または順番に実行

                    AutoTransition
                    Fade(OUT) → ChangeBounds → Fade(IN)
                """
            }
            code {
                body = """
                    val transition = TransitionSet().apply {
                      duration = 300
                      ordering = TransitionSet.ORDERING_TOGETHER
                      addTransition(Explode().apply {
                        interpolator = FastOutLinearInInterpolator()
                      })
                      addTransition(ChangeBounds()).apply {
                        interpolator = FastOutSlowInInterpolator()
                      }
                    }
                """
            }
        }
        chapter {
            title = "その他の Transition 機能"
            body = """
                - Transition Group
                - Transition Name
                - Target add/exclude
                - PathMotion
                - Propagation
                - Epicenter
                - Match Order
            """
            section {
                title = "Transition Group"
                body = """
                    ViewGroupCompat.setTransitionGroup(container, true)

                    指定した ViewGroup を、ひとかたまりにする

                    中の View 一つ一つに Transition をかけるのではなく
                    全体でまとめてかける
                """
            }
            section {
                title = "Transition Name"
                body = """
                    ViewCompat.setTransitionName(view, "abc")

                    View に Transition 用の名前をつける
                    使いみちは後述
                """
            }
            section {
                title = "addTarget/excludeTarget"
                body = """
                    Transition#addTarget()/excludeTarget()

                    Transition の対象を
                    - View のインスタンス
                    - View の ID
                    - View のクラス
                    - View の Transition Name
                    でフィルタリング
                """
            }
            section {
                title = "PathMotion"
                body = """
                    Transition#setPathMotion()

                    二次元的な動きを Path に従ってアニメーション
                    ChangeBounds や ChangeTransform 用
                """
            }
            section {
                title = "Propagation"
                body = """
                    Transition#setPropagation()

                    その Transition の対象が複数あるとき
                    アニメーションの開始を少しずつずらす
                    Explode や Slide ではデフォルトで有効
                """
            }
            section {
                title = "Epicenter"
                body = """
                    Transition#setEpicenterCallback()

                    Transition の中心点を指定する
                    基本的にはタッチした場所
                    Explode や Slide ではデフォルトで有効
                """
            }
            section {
                title = "Match Order"
                body = """
                    変化前と変化後の View のマッチングをどう行うか

                    - View のインスタンス
                    - View の ID
                    - View の Transition name
                    - View のアイテム ID (ListView 用)
                """
            }
        }
        chapter {
            title = "カスタム Transition"
            body = """
                Transition を継承して実装する

                実装するメソッド
                - captureStartValues()
                - captureEndValues()
                - createAnimator()
            """
            section {
                title = "captureStart/EndValues()"
                body = """
                    この Transition に必要な値を View から抜き出して
                    TransitionValues に入れる

                    captureStartValues: 遷移前の状態を記録する
                    captureEndValues: 遷移後の状態を記録する

                    beginDelayedTransition で指定された ViewGroup 以下の
                    すべての View それぞれに対して呼び出される
                """
            }
            section {
                title = "createAnimator()"
                body = """
                    TransitionValues に入れておいた値をもとに
                    Animator を作って返す

                    すべての View それぞれに対して呼び出される
                    関係のない View については null を返せばいい
                """
            }
        }
        chapter {
            title = "Fragment Transition"
            body = """
                Fragment の遷移で Transition を使う

                - Fragment Transition
                  共通しない要素に対して
                  主に Fade, Slide, Explode など
                - Shared Element Fragment Transition
                  前後の Fragment 間で共通の要素に対して
                  主に ChangeBounds, ChangeTransform など
            """
            section {
                title = "Fragment Transition"
                body = """
                    Fragment#setExitTransition()
                    Fragment#setEnterTransition()
                    Fragment#setReturnTransition()
                    Fragment#setReenterTransition()

                    元 Fragment [exit] -------> 先 Fragment [enter]
                    元 Fragment [reenter] <---- 先 Fragment [return]
                """
            }
            code {
                body = """
                    class ListFragment : Fragment() {
                      override fun onCreate(state: Bundle?) {
                        super.onCreate(state)
                        exitTransition = Slide(GravityCompat.START)
                      }
                    }

                    class DetailFragment : Fragment() {
                      override fun onCreate(state: Bundle?) {
                        super.onCreate(state)
                        enterTransition = Fade()
                      }
                    }
                """
            }
            section {
                title = "Shared Element Fragment Transition"
                body = """
                    Fragment#setSharedElementEnterTransition()
                    Fragment#setSharedElementReturnTransition()

                    遷移先の Fragment で指定
                """
            }
            code {
                body = """
                    class DetailFragment : Fragment() {
                      override fun onCreate(state: Bundle?) {
                        super.onCreate(state)
                        sharedElementEnterTransition = TransitionSet()
                          .apply {
                            addTransition(ChangeBounds())
                            addTransition(ChangeTransform())
                          }
                      }
                    }
                """
            }
            section {
                title = "共通要素の指定"
                body = """
                    遷移元 遷移先 両方の View に Transition name が必要
                    画面内でユニークでなければならない
                """
            }
            code {
                body = """
                    val view: View = ...
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, detailFragment)
                        .addToBackStack(null)
                        // 遷移元の View, 遷移先の Transition name
                        .addSharedElement(view, "target")
                         // ※
                        .setReorderingAllowed(true)
                        .commit()
                """
            }
            section {
                title = "Transition の延期"
                body = """
                    遷移元・遷移先 両方の Fragment

                    onViewCreated で
                      延期: postponeEnterTransition()
                    データ読み込み・レイアウトが終わったら
                      再開: startPostponedEnterTransition()

                    setReorderingAllowed(true) が必要
                """
            }
            section {
                title = "Fragment Transition まとめ"
                body = """
                    まずは Shared Element を使わない Transition から
                """
            }
        }
        chapter {
            title = "ハンズオン"
            body = """
                Fragment Transition のサンプル・コードラボ
                github.com/yaraki/CheeseMotion

                - cheesemotion-start: 課題 - TODO を埋めていく
                - cheesemotion-final: 完成形
                - common: データ置き場
                - deck: このスライド
            """
            section {
                title = "普通の Transition"
                body = """
                    [易] LinearLayout の最初の項目を GONE にして
                    　　 beginDelayedTransition
                    [易] beginDelayedTransition の第 2 引数を変えてみる
                    [中] ある ViewGroup から別の ViewGroup に View を動かす
                    [難] Expandable な FrameLayout
                    　　 (layout_height 固定値 ←→ wrap_content 切り替え)
                """
            }
            section {
                title = "参考"
                body = """
                    記事
                    - https://chris.banes.me/2018/02/18/fragmented-transitions/

                    アプリ
                    - Tivi: github.com/chrisbanes/tivi
                    - Topeka github.com/googlesamples/android-topeka
                    - Plaid: github.com/nickbutcher/plaid
                """
            }
        }
        chapter {
            title = "おわり"
        }
    }

    private val index = MutableLiveData<Int>().apply { value = 0 }
    val currentPage: LiveData<Page> = Transformations.map(index) { deck.pages[it] }

    fun moveToNext() {
        index.value?.let { i ->
            if (i < deck.pages.size - 1) {
                index.value = i + 1
            }
        }
    }

    fun moveToPrevious() {
        index.value?.let { i ->
            if (i > 0) {
                index.value = i - 1
            }
        }
    }

    @RequiresApi(19)
    fun generatePdf(view: PageView, fragmentManager: FragmentManager?) {
        thread {
            val document = PdfDocument()
            deck.pages.forEachIndexed { i, page ->
                view.post {
                    view.setPage(page, fragmentManager)
                }
                SystemClock.sleep(50) // Wait for the layout
                document.startPage(PdfDocument.PageInfo.Builder(deck.size.x, deck.size.y, i)
                        .create()).let {
                    view.draw(it.canvas) // Should this be in the UI thread?
                    document.finishPage(it)
                }
            }
            view.context.openFileOutput("deck.pdf", Context.MODE_PRIVATE).use {
                document.writeTo(it)
            }
            document.close()
            index.postValue(index.value)
        }
    }

}
