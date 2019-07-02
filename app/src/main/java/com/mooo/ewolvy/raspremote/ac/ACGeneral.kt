package com.mooo.ewolvy.raspremote.ac

class ACGeneral(status: String) : ACStatus(status) {

    override fun setNextFan() {
        mFan++
        if (mFan > FAN_QUIET) mFan = FAN_AUTO
    }
}