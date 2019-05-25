package com.mooo.ewolvy.raspremote.plug

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mooo.ewolvy.raspremote.R
import kotlinx.android.synthetic.main.activity_plug.*

class PlugActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plug)

        setupListeners()
    }

    private fun setupListeners(){
        button_plug_1_on.setOnClickListener { doActionForButton(it.id) }
        button_plug_1_off.setOnClickListener { doActionForButton(it.id) }
        button_plug_2_on.setOnClickListener { doActionForButton(it.id) }
        button_plug_2_off.setOnClickListener { doActionForButton(it.id) }
        button_plug_3_on.setOnClickListener { doActionForButton(it.id) }
        button_plug_3_off.setOnClickListener { doActionForButton(it.id) }
        button_plug_4_on.setOnClickListener { doActionForButton(it.id) }
        button_plug_4_off.setOnClickListener { doActionForButton(it.id) }
        button_plug_all_on.setOnClickListener { doActionForButton(it.id) }
        button_plug_all_off.setOnClickListener { doActionForButton(it.id) }
    }

    private fun doActionForButton (buttonId: Int){
        when (buttonId){
            R.id.button_plug_1_on ->  TODO("Yet to be done")
            R.id.button_plug_1_off -> TODO("Yet to be done")
        }
    }
}
