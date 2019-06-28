package com.mooo.ewolvy.raspremote.ac

class ACKaysun(status: String) : ACStatus(status) {
    companion object {
        const val MAX_TEMP: Int = 30
        const val MIN_TEMP: Int = 17
    }

    init {
        if (mTemp > MAX_TEMP) mTemp = MAX_TEMP
        if (mTemp < MIN_TEMP) mTemp = MIN_TEMP

        mActiveFan = when (mMode){
            MODE_AUTO, MODE_DRY -> false
            else -> true
        }
        mActiveTemp = when (mMode){
            MODE_FAN -> false
            else -> true
        }
    }

    override fun setNextMode() {
        super.setNextMode()
        when (mMode){
            MODE_AUTO -> {
                mActiveFan = false
                mActiveTemp = true
            }
            MODE_COOL -> mActiveFan = true
            MODE_DRY -> mActiveFan = false
            MODE_HEAT -> mActiveFan = true
            MODE_FAN -> mActiveTemp = false
        }
    }

    override fun setNextFan() {
        if (mActiveFan) super.setNextFan()
    }

    override fun plusTemp(){
        if (mActiveTemp) mTemp =
            if (mTemp == MAX_TEMP) MAX_TEMP else mTemp + 1
    }

    override fun minusTemp() {
        if (mActiveTemp) mTemp =
            if (mTemp == MIN_TEMP) MIN_TEMP else mTemp - 1
    }
}