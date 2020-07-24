package ru.alexalekhin.todomanager.presentation.misc

import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView


class CustomRecyclerViewAnimator : DefaultItemAnimator(), ViewPropertyAnimatorListener {
    override fun onAnimationStart(view: View?) {}

    override fun onAnimationCancel(view: View?) {}

    override fun onAnimationEnd(view: View?) {
        view?.visibility = View.GONE
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
//        if (holder != null)
//            holder.itemView.translationX = ((-holder.itemView.rootView?.width!!).toFloat())
        //TODO: choose animation

//        holder?.itemView?.let {
//            ViewCompat.animate(it)
//                .alpha(1.0f)
//                .setDuration(addDuration * TIME_SCALE)
//                .setInterpolator(LinearInterpolator())
//                .setStartDelay(0)
//                .start()
//        }
        return true
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        //TODO: choose animation
        holder?.itemView?.let {
            ViewCompat.animate(it)
                .alpha(REMOVE_ALPHA)
                .scaleX(REMOVE_SCALE)
                .scaleY(REMOVE_SCALE)
                .setDuration(removeDuration * TIME_SCALE)
                .setInterpolator(LinearInterpolator())
                .setListener(this)
                .setStartDelay(0)
                .start()
        }
        return true
    }

    companion object {
        private const val REMOVE_ALPHA = 0.0f
        private const val ADD_TRANSLATION_X = 0.0f
        private const val REMOVE_SCALE = 0.0f
        private const val TIME_SCALE = 5
    }
}
