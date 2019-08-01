package com.mooo.ewolvy.raspremote.ac

import android.util.Log

class ACGeneral(status: String) : ACStatus(status) {
    override val mMaxTemp: Int = 30
    override val mMinTemp: Int = 18

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
            Log.d(mTag, "The saved status is invalid on ACGeneral")
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
        if (mFan > FAN_QUIET || mFan < FAN_AUTO) return false
        return true
    }

    override fun setNextFan() {
        if (isFanActive()) {
            mFan++
            if (mFan > FAN_QUIET) mFan = FAN_AUTO
        }
    }
}