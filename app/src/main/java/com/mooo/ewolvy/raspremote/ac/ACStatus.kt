package com.mooo.ewolvy.raspremote.ac

import android.util.Log

abstract class ACStatus (){

    protected var mTemp: Int = 27
    protected var mFan: Int = 0
    protected var mMode: Int = 0
    protected var mActiveFan: Boolean = true
    protected var mActiveTemp: Boolean = true
    protected var mActiveSwing: Boolean = false

    protected val mTag = "ACStatus"

    protected abstract val mMaxTemp: Int
    protected abstract val mMinTemp: Int

    constructor (status: String): this(){
        try {
            if (status.isNotBlank()) {
                val statusArray = status.split(";")
                mMode = statusArray[0].toInt()
                mFan = statusArray[1].toInt()
                mTemp = statusArray[2].toInt()
                mActiveSwing = statusArray[3].toBoolean()
            }
        } catch (exception: Exception) {
            mTemp = 27
            mFan = 0
            mMode = 0
            mActiveFan = true
            mActiveTemp = true
            mActiveSwing = false
            Log.d(mTag, "The saved status is invalid: ${exception.message}")
        }
    }

    companion object{
        const val MODE_AUTO = 0
        const val MODE_COOL = 1
        const val MODE_DRY = 2
        const val MODE_HEAT = 3
        const val MODE_FAN = 4

        val MODE_NAMES = mapOf(
            MODE_AUTO to "AUTO_",
            MODE_COOL to "COOL_",
            MODE_DRY to "DRY_",
            MODE_HEAT to "HEAT_",
            MODE_FAN to "FAN_"
        )

        const val FAN_AUTO = 0
        const val FAN_1 = 1
        const val FAN_2 = 2
        const val FAN_3 = 3
        const val FAN_QUIET = 4

        val FAN_NAMES = mapOf(
            FAN_AUTO to "AUTO_",
            FAN_1 to "LOW_",
            FAN_2 to "MED_",
            FAN_3 to "HIGH_",
            FAN_QUIET to "QUIET_"
        )
    }

    override fun toString(): String {
        return "$mMode;$mFan;$mTemp;$mActiveSwing"
    }

    abstract fun isValidState(): Boolean

    open fun setNextMode(){
        mMode++
        if (mMode > MODE_FAN) mMode = MODE_AUTO
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

    open fun setNextFan(){
        if (isFanActive()) {
            mFan++
            if (mFan > FAN_3) mFan = FAN_AUTO
        }
    }

    fun plusTemp(){
        if (mActiveTemp) mTemp =
            if (mTemp == mMaxTemp) mMaxTemp else mTemp + 1
    }

    fun minusTemp(){
        if (mActiveTemp) mTemp =
            if (mTemp == mMinTemp) mMinTemp else mTemp - 1
    }

    fun isFanActive(): Boolean { return mActiveFan }

    fun isTempActive(): Boolean { return mActiveTemp }

    fun isSwingActive(): Boolean { return mActiveSwing }

    fun getTemp(): Int { return mTemp }

    fun getFan(): Int { return mFan }

    fun getMode(): Int { return mMode }

    open fun getCode(): String {
        return "${MODE_NAMES[mMode]}${FAN_NAMES[mFan]}$mTemp"
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