package com.southampton.comp6239.base

import android.util.Log
import android.view.View
import com.southampton.comp6239.PassswordResetFragment

abstract class OnClickEvent : View.OnClickListener {

    var lastTime: Long = 0

    abstract fun singleClick(v: View?)

    override fun onClick(v: View?) {
        if (onDoubClick()) {
            singleClick(v);
            return;
        }
    }

    open fun onDoubClick(): Boolean {
        var flag = false
        val time = System.currentTimeMillis() - lastTime
        if (time > 500) {
            lastTime = System.currentTimeMillis()
            flag = true
        }
        return flag
    }
}