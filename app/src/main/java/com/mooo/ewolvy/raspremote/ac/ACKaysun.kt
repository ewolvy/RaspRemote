package com.mooo.ewolvy.raspremote.ac

class ACKaysun(status: String) : ACStatus(status) {
    companion object {
        const val MAX_TEMP: Int = 30
        const val MIN_TEMP: Int = 17
    }

    init {
        if (temp > MAX_TEMP) temp = MAX_TEMP
        if (temp < MIN_TEMP) temp = MIN_TEMP

        activeFan = when (mode){
            MODE_AUTO, MODE_DRY -> false
            else -> true
        }
        activeTemp = when (mode){
            MODE_FAN -> false
            else -> true
        }
    }

    override fun setNextMode() {
        super.setNextMode()
        when (mode){
            MODE_AUTO -> {
                activeFan = false
                activeTemp = true
            }
            MODE_COOL -> activeFan = true
            MODE_DRY -> activeFan = false
            MODE_HEAT -> activeFan = true
            MODE_FAN -> activeTemp = false
        }
    }

    override fun setNextFan() {
        if (activeFan) super.setNextFan()
    }

    override fun plusTemp(){
        if (activeTemp) temp =
            if (temp == MAX_TEMP) MAX_TEMP else temp + 1
    }

    override fun minusTemp() {
        if (activeTemp) temp =
            if (temp == MIN_TEMP) MIN_TEMP else temp - 1
    }
}