package com.mooo.ewolvy.raspremote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.mooo.ewolvy.broadcastdiscovery.BroadcastDiscoveryActivity
import com.mooo.ewolvy.broadcastdiscovery.FetchDataErrorStatus
import kotlinx.android.synthetic.main.activity_edit_item.*
import org.json.JSONObject

const val REQUEST_CODE_BCD = 1
const val MY_TIMEOUT = 5000L
const val SERVER_PORT = 19103
const val SERVICE_REQUESTED = "BROADCAST_RASPREMOTE"
const val EDIT_TAG = "EDIT_TAG"

class EditItemActivity : AppCompatActivity() {

    companion object {
        const val EDIT_FOR_NEW = 0
        const val EDIT_FOR_EDIT = 1
        const val EDIT_PURPOSE = "PURPOSE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        val purpose = getPurpose(intent.extras)

        if (purpose == EDIT_FOR_NEW) {
            fab_edit.show()
            setButtonListeners()
        }else{
            fab_edit.hide()
        }
    }

    private fun getPurpose(extras: Bundle?): Int{
        return extras?.getInt(EDIT_PURPOSE) ?: EDIT_FOR_NEW
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
                    val jsonData = JSONObject(data?.getStringExtra(BroadcastDiscoveryActivity.EXTRA_SERVER))
                    edit_name.setText(jsonData.getString("Name"))
                    edit_type.setSelection(jsonData.getInt("Type"))
                    edit_server.setText(jsonData.getString("Address"))
                    edit_port.setText(jsonData.getString("Port"))
                    edit_alias.setText(jsonData.getString("Alias"))
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
