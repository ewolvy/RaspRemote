package com.mooo.ewolvy.raspremote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.mooo.ewolvy.broadcastdiscovery.BroadcastDiscoveryActivity
import com.mooo.ewolvy.broadcastdiscovery.FetchDataErrorStatus
import com.mooo.ewolvy.raspremote.database.Device
import kotlinx.android.synthetic.main.activity_edit_item.*
import org.json.JSONObject

const val REQUEST_CODE_BCD = 3
const val MY_TIMEOUT = 5000L
const val SERVER_PORT = 19103
const val SERVICE_REQUESTED = "BROADCAST_RASPREMOTE"
const val EDIT_TAG = "EDIT_TAG"
const val MAX_PORT = 65535
const val MIN_PORT = 1

class EditItemActivity : AppCompatActivity() {

    companion object {
        const val EDIT_FOR_NEW = 1
        const val EDIT_FOR_EDIT = 2
        const val EDIT_PURPOSE = "PURPOSE"
        const val EXTRA_DEVICE = "EXTRA_DEVICE"
    }

    private val purpose: Int by lazy {getPurpose(intent.extras)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        if (purpose == EDIT_FOR_NEW) {
            fab_edit.show()
            setButtonListeners()
        }else{
            fab_edit.hide()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_edit_save -> saveAndExit()
            R.id.menu_edit_cancel -> cancelAndExit()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun saveAndExit(){
        when {
            edit_name.text.isEmpty() -> Snackbar.make(
                edit_root_layout,
                R.string.edit_error_empty_name,
                Snackbar.LENGTH_LONG).show()
            edit_server.text.isEmpty() -> Snackbar.make(
                edit_root_layout,
                R.string.edit_error_empty_server,
                Snackbar.LENGTH_LONG).show()
            edit_port.text.toString().toInt() !in MIN_PORT..MAX_PORT -> Snackbar.make(
                edit_root_layout,
                R.string.edit_error_invalid_port,
                Snackbar.LENGTH_LONG).show()
            edit_username.text.isEmpty() -> Snackbar.make(
                edit_root_layout,
                R.string.edit_error_empty_username,
                Snackbar.LENGTH_LONG).show()
            edit_password.text.isEmpty() -> Snackbar.make(
                edit_root_layout,
                R.string.edit_error_empty_password,
                Snackbar.LENGTH_LONG).show()
            edit_alias.text.isEmpty() -> Snackbar.make(
                edit_root_layout,
                R.string.edit_error_empty_alias,
                Snackbar.LENGTH_LONG).show()
/******************* NOT IMPLEMENTED YET - TODO: Remove when can select certificate file
            edit_certificate_text.text.isEmpty() -> Snackbar.make(
                edit_root_layout,
                R.string.edit_error_empty_certificate,
                Snackbar.LENGTH_LONG).show()
***************************************************************************************/
            // If there is no error, offer the user to save changes and exit
            else -> {
                val builder = AlertDialog.Builder(this@EditItemActivity)
                with(builder) {
                    setTitle(R.string.dialog_confirm_title)
                    setMessage(R.string.dialog_save_message)
                    setPositiveButton(R.string.dialog_yes) {_, _ ->
                        val device = Device(0,
                            edit_name.text.toString(),
                            edit_type.selectedItemPosition,
                            edit_server.text.toString(),
                            edit_port.text.toString().toInt(),
                            edit_username.text.toString(),
                            edit_password.text.toString(),
                            edit_alias.text.toString(),
                            "/cert.pem", // TODO: Using fake certificate until can get a file
                            0,  // will manage the correct order on the calling activity when creating / updating the device on the database
                            "")
                        val replyIntent = Intent()
                        replyIntent.putExtra(EXTRA_DEVICE, device)
                        setResult(Activity.RESULT_OK, replyIntent)
                        finish()
                    }
                    setNeutralButton(R.string.dialog_no) {_, _ ->
                        return@setNeutralButton
                    }
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }

    }

    private fun cancelAndExit(){
        val builder = AlertDialog.Builder(this@EditItemActivity)
        builder.setTitle(R.string.dialog_confirm_title)
        builder.setMessage(R.string.dialog_discard_message)
        builder.setPositiveButton(R.string.dialog_yes) {_, _ ->
            val replyIntent = Intent()
            setResult(Activity.RESULT_CANCELED, replyIntent)
            finish()
        }
        builder.setNeutralButton(R.string.dialog_no) {_, _ ->
            return@setNeutralButton
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
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
