package com.mooo.ewolvy.raspremote.ac

abstract class ACStatus (){

    protected var mTemp: Int = 27
    protected var mFan: Int = 0
    protected var mMode: Int = 0
    protected var mActiveFan: Boolean = true
    protected var mActiveTemp: Boolean = true

    constructor (status: String): this(){
        try {
            if (status.isNotBlank()) {
                val statusArray = status.split(";")
                mTemp = statusArray[0].toInt()
                mFan = statusArray[1].toInt()
                mMode = statusArray[2].toInt()
            }
        } catch (exception: Exception) {
            mTemp = 27
            mFan = 0
            mMode = 0
            mActiveFan = true
            mActiveTemp = true
        }
    }

    companion object{
        const val MODE_AUTO = 0
        const val MODE_COOL = 1
        const val MODE_DRY = 2
        const val MODE_HEAT = 3
        const val MODE_FAN = 4

        val MODE_NAMES = mapOf(
            0 to "AUTO_",
            1 to "COOL_",
            2 to "DRY_",
            3 to "HEAT_",
            4 to "FAN_"
        )

        const val FAN_AUTO = 0
        const val FAN_1 = 1
        const val FAN_2 = 2
        const val FAN_3 = 3
        const val FAN_QUIET = 4

        val FAN_NAMES = mapOf(
            0 to "AUTO_",
            1 to "LOW_",
            2 to "MED_",
            3 to "HIGH_",
            4 to "QUIET_"
        )
    }

    override fun toString(): String {
        return "$mTemp;$mFan;$mMode"
    }

    open fun setNextMode(){
        mMode++
        if (mMode > MODE_FAN) mMode = MODE_AUTO
    }

    open fun setNextFan(){
        if (isFanActive()) {
            mFan++
            if (mFan > FAN_3) mFan = FAN_AUTO
        }
    }

    open fun plusTemp(){
        mTemp++
    }

    open fun minusTemp(){
        mTemp--
    }

    fun isFanActive(): Boolean { return mActiveFan }

    fun isTempActive(): Boolean { return mActiveTemp }

    fun getTemp(): Int { return mTemp }

    fun getFan(): Int { return mFan }

    fun getMode(): Int { return mMode }

    fun getCode(): String {
        return "$MODE_NAMES[$mMode]$FAN_NAMES[$mFan]$mTemp"
    }

    open fun getPowerOn(): String {
        return "POWER_ON"
    }

    open fun getPowerOff(): String {
        return "POWER_OFF"
    }

    open fun getSwing(): String {
        return "SWING"
    }
}