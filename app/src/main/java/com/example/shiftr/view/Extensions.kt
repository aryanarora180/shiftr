package com.example.shiftr.view

import android.view.View
import android.widget.EdgeEffect
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView
import com.example.shiftr.data.SingleLiveEvent
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(text: String) = Snackbar.make(
    this,
    text,
    Snackbar.LENGTH_LONG
).show()

fun View.showSnackbar(text: SingleLiveEvent<String>) {
    text.getContentIfNotHandled()?.let {
        Snackbar.make(
            this,
            it,
            Snackbar.LENGTH_LONG
        ).show()
    }
}

val RecyclerView.ViewHolder.translationY: SpringAnimation
    get() = SpringAnimation(itemView, SpringAnimation.TRANSLATION_Y)
        .setSpring(
            SpringForce()
                .setFinalPosition(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_LOW)
        )

class SpringyRecycler {
    companion object {
        inline fun <reified T : RecyclerView.ViewHolder> springEdgeEffectFactory() =
            object : RecyclerView.EdgeEffectFactory() {
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    return object : EdgeEffect(view.context) {
                        override fun onPull(deltaDistance: Float) {
                            super.onPull(deltaDistance)
                            handlePull(deltaDistance)
                        }

                        override fun onPull(deltaDistance: Float, displacement: Float) {
                            super.onPull(deltaDistance, displacement)
                            handlePull(deltaDistance)
                        }

                        private fun handlePull(deltaDistance: Float) {
                            val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                            val translationYDelta =
                                sign * view.width * deltaDistance * 0.2f
                            view.forEachVisibleHolder { holder: T ->
                                holder.translationY.cancel()
                                holder.itemView.translationY += translationYDelta
                            }
                        }

                        override fun onRelease() {
                            super.onRelease()
                            view.forEachVisibleHolder { holder: T ->
                                holder.translationY.start()
                            }
                        }

                        override fun onAbsorb(velocity: Int) {
                            super.onAbsorb(velocity)
                            val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                            val translationVelocity = sign * velocity * 0.5f
                            view.forEachVisibleHolder { holder: T ->
                                holder.translationY
                                    .setStartVelocity(translationVelocity)
                                    .start()
                            }
                        }
                    }
                }
            }
    }
}

inline fun <reified T : RecyclerView.ViewHolder> RecyclerView.forEachVisibleHolder(
    action: (T) -> Unit
) {
    for (i in 0 until childCount) {
        action(getChildViewHolder(getChildAt(i)) as T)
    }
}