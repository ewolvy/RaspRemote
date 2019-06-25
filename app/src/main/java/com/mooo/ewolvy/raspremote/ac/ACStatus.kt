package com.mooo.ewolvy.raspremote.ac

abstract class ACStatus (){

    var temp: Int = 27
    var fan: Int = 0
    var mode: Int = 0

    constructor (status: String): this(){
        if (status.isNotBlank()) {
            val statusArray = status.split(";")
            temp = statusArray[0].toInt()
            fan = statusArray[1].toInt()
            mode = statusArray[2].toInt()
        }
    }

    companion object{
        const val MODE_AUTO = 0
        const val MODE_COOL = 1
        const val MODE_DRY = 2
        const val MODE_HEAT = 3
        const val MODE_FAN = 4

        const val FAN_AUTO = 0
        const val FAN_1 = 1
        const val FAN_2 = 2
        const val FAN_3 = 3
    }

    override fun toString(): String {
        return "$temp;$fan;$mode"
    }

    fun setNextMode(){
        mode++
        if (mode > MODE_FAN) mode = MODE_AUTO
    }

    fun setNextFan(){
        fan++
        if (fan > FAN_3) fan = FAN_AUTO
    }

    fun plusTemp(){
        temp++
    }

    fun minusTemp(){
        temp--
    }
}