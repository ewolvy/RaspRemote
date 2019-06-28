package com.mooo.ewolvy.raspremote.ac

class ACGeneral(status: String) : ACStatus(status) {

    companion object{
        const val FAN_QUIET = 4
    }

    override fun setNextFan() {
        mFan++
        if (mFan > FAN_QUIET) mFan = FAN_AUTO
    }
}