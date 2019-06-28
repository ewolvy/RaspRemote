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

        const val FAN_AUTO = 0
        const val FAN_1 = 1
        const val FAN_2 = 2
        const val FAN_3 = 3
    }

    override fun toString(): String {
        return "$mTemp;$mFan;$mMode"
    }

    open fun setNextMode(){
        mMode++
        if (mMode > MODE_FAN) mMode = MODE_AUTO
    }

    open fun setNextFan(){
        mFan++
        if (mFan > FAN_3) mFan = FAN_AUTO
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
}