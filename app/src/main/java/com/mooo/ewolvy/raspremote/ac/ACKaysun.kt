package com.mooo.ewolvy.raspremote.ac

import android.util.Log

class ACKaysun(status: String) : ACStatus(status) {
    override val mMaxTemp: Int = 30
    override val mMinTemp: Int = 17

    init {
        if (isValidState()) {
            mActiveFan = when (mMode) {
                MODE_AUTO, MODE_DRY -> false
                else -> true
            }
            mActiveTemp = when (mMode) {
                MODE_FAN -> false
                else -> true
            }
        } else {
            Log.d(mTag, "The saved status is invalid on ACKaysun")
            mTemp = 27
            mFan = 0
            mMode = 0
            mActiveFan = true
            mActiveTemp = true
        }
    }

    override fun isValidState(): Boolean {
        if (mTemp > mMaxTemp || mTemp < mMinTemp) return false
        if (mMode > MODE_FAN || mMode < MODE_AUTO) return false
        if (mFan > FAN_3 || mFan < FAN_AUTO) return false
        return true
    }

    override fun getPowerOn(): String {
        return getCode()
    }
}