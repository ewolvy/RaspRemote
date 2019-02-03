package com.mooo.ewolvy.raspremote

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import com.mooo.ewolvy.broadcastdiscovery.BroadcastDiscoveryActivity
import com.mooo.ewolvy.broadcastdiscovery.FetchDataErrorStatus
import kotlinx.android.synthetic.main.activity_edit_item.*
import org.json.JSONObject

const val REQUEST_CODE_BCD = 1
const val MY_TIMEOUT = 5000L
const val SERVER_PORT = 19103
const val SERVICE_REQUESTED = "BROADCAST_REALREMOTE"
const val EDIT_TAG = "EDIT_TAG"

class EditItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        setButtonListeners()
    }

    private fun setButtonListeners(){
        // TODO: Set choose file button listener

        fab_edit.setOnClickListener {
            startBroadcastDiscoveryActivity()
        }
    }

    private fun startBroadcastDiscoveryActivity(){
        val intent = Intent(this@EditItemActivity, BroadcastDiscoveryActivity::class.java)
        val extras = Bundle()
        extras.putString(BroadcastDiscoveryActivity.EXTRA_SERVICE, SERVICE_REQUESTED)
        extras.putInt(BroadcastDiscoveryActivity.EXTRA_PORT, SERVER_PORT)
        extras.putLong(BroadcastDiscoveryActivity.EXTRA_TIMEOUT, MY_TIMEOUT)
        extras.putLong(BroadcastDiscoveryActivity.EXTRA_RESEND_TIME, MY_TIMEOUT)

        intent.putExtra(BroadcastDiscoveryActivity.BROADCAST_EXTRAS, extras)

        startActivityForResult(intent, REQUEST_CODE_BCD)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            REQUEST_CODE_BCD ->
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val response = data?.getStringExtra(BroadcastDiscoveryActivity.EXTRA_SERVER)
                    //TODO: Get the data from the intent and show it on the EditItemActivity
                    Snackbar.make(
                        edit_root_layout, // Parent view
                        JSONObject(response).getJSONObject("META").getString("Description"), // Message to show
                        Snackbar.LENGTH_LONG // How long to display the message.
                    ).show()
                } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                    val error = data?.getSerializableExtra(BroadcastDiscoveryActivity.EXTRA_ERROR_CODE) as FetchDataErrorStatus? ?: FetchDataErrorStatus.UNKNOWN_ERROR
                    Snackbar.make(
                        edit_root_layout,
                        error.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            else -> Log.d(EDIT_TAG, "Unexpected activity returned some result!!!")
        }
    }
}
