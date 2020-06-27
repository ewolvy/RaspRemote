package com.mooo.ewolvy.raspremote.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.mooo.ewolvy.broadcastdiscovery.BroadcastDiscoveryActivity
import com.mooo.ewolvy.broadcastdiscovery.FetchDataErrorStatus
import com.mooo.ewolvy.raspremote.R
import com.mooo.ewolvy.raspremote.database.Device
import kotlinx.android.synthetic.main.activity_edit_item.*
import org.json.JSONObject

class EditItemActivity : AppCompatActivity() {

    companion object {
        const val EDIT_FOR_NEW = 1
        const val EDIT_FOR_EDIT = 2
        const val EDIT_PURPOSE = "PURPOSE"
        const val EXTRA_DEVICE = "EXTRA_DEVICE"

        const val REQUEST_CODE_BCD = 3
//        const val REQUEST_CODE_FC = 4
        const val MY_TIMEOUT = 5000L
        const val BCD_SERVER_PORT = 19103
        const val SERVICE_REQUESTED = "BROADCAST_RASPREMOTE"
        const val EDIT_TAG = "EDIT_TAG"
        const val MAX_PORT = 65535
        const val MIN_PORT = 1
    }

    private val purpose: Int by lazy {getPurpose(intent.extras)}
    private val device: Device by lazy {getDevice(intent.extras)}
    private var certificateUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        setButtonListeners()
        if (purpose == EDIT_FOR_NEW) {
            fab_edit.show()
        }else{
            fab_edit.hide()
            populateFields()
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
            else -> return super.onOptionsItemSelected(item!!)
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
//            edit_certificate_text.text.isEmpty() -> Snackbar.make(
//                edit_root_layout,
//                R.string.edit_error_empty_certificate,
//                Snackbar.LENGTH_LONG).show()
            // If there is no error, offer the user to save changes and exit
            else -> {
                val builder = AlertDialog.Builder(this@EditItemActivity)
                with(builder) {
                    setTitle(R.string.dialog_confirm_title)
                    setMessage(R.string.dialog_save_message)
                    setPositiveButton(R.string.dialog_yes) { _, _ ->
                        val returnDevice = Device(device._id,
                            edit_name.text.toString(),
                            edit_type.selectedItemPosition,
                            edit_server.text.toString(),
                            edit_port.text.toString().toInt(),
                            edit_username.text.toString(),
                            edit_password.text.toString(),
                            edit_alias.text.toString(),
                            certificateUri ?: device.certificateFile,
                            device.position,  // will manage the correct order on the calling activity when creating / updating the device on the database
                            device.currentState)
                        val replyIntent = Intent()
                        replyIntent.putExtra(EXTRA_DEVICE, returnDevice)
                        setResult(Activity.RESULT_OK, replyIntent)
                        finish()
                    }
                    setNeutralButton(R.string.dialog_no) { _, _ ->
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
        builder.setPositiveButton(R.string.dialog_yes) { _, _ ->
            val replyIntent = Intent()
            setResult(Activity.RESULT_CANCELED, replyIntent)
            finish()
        }
        builder.setNeutralButton(R.string.dialog_no) { _, _ ->
            return@setNeutralButton
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun getPurpose(extras: Bundle?): Int{
        return extras?.getInt(EDIT_PURPOSE) ?: EDIT_FOR_NEW
    }

    private fun getDevice(extras: Bundle?): Device{
        return extras?.getParcelable(EXTRA_DEVICE) ?: Device()
    }

    private fun populateFields(){
        edit_name.setText(device.name)
        edit_type.setSelection(device.type)
        edit_server.setText(device.server)
        edit_port.setText(device.port.toString())
        edit_username.setText(device.username)
        edit_password.setText(device.password)
        edit_alias.setText(device.alias)
//        device.certificateFile.path?.let {
//            edit_certificate_text.text = it.substring(it.lastIndexOf("/") + 1)
//        }
    }

    private fun setButtonListeners(){
//        edit_certificate_button.setOnClickListener{
//            startFileChooser()
//        }

        fab_edit.setOnClickListener {
            startBroadcastDiscoveryActivity()
        }
    }

//    private fun startFileChooser(){
//        val intent = Intent()
//            .setType("*/*")
//            .setAction(Intent.ACTION_OPEN_DOCUMENT)
//            .addCategory(Intent.CATEGORY_OPENABLE)
//
//        startActivityForResult(Intent.createChooser(intent, "Select a file"),
//            REQUEST_CODE_FC
//        )
//    }

    private fun startBroadcastDiscoveryActivity(){
        val intent = Intent(this@EditItemActivity, BroadcastDiscoveryActivity::class.java)
        val extras = Bundle()
        extras.putString(BroadcastDiscoveryActivity.EXTRA_SERVICE,
            SERVICE_REQUESTED
        )
        extras.putInt(BroadcastDiscoveryActivity.EXTRA_PORT,
            BCD_SERVER_PORT
        )
        extras.putLong(BroadcastDiscoveryActivity.EXTRA_TIMEOUT,
            MY_TIMEOUT
        )
        extras.putLong(BroadcastDiscoveryActivity.EXTRA_RESEND_TIME,
            MY_TIMEOUT
        )

        intent.putExtra(BroadcastDiscoveryActivity.BROADCAST_EXTRAS, extras)

        startActivityForResult(intent,
            REQUEST_CODE_BCD
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            REQUEST_CODE_BCD ->
                if (resultCode == RESULT_OK) {
                    val jsonData = JSONObject(data?.getStringExtra(BroadcastDiscoveryActivity.EXTRA_SERVER)!!)
                    edit_name.setText(jsonData.getString("Name"))
                    edit_type.setSelection(jsonData.getInt("Type"))
                    edit_server.setText(jsonData.getString("Address"))
                    edit_port.setText(jsonData.getString("Port"))
                    edit_alias.setText(jsonData.getString("Alias"))
                } else if (resultCode == RESULT_CANCELED) {
                    val error = data?.getSerializableExtra(BroadcastDiscoveryActivity.EXTRA_ERROR_CODE) as FetchDataErrorStatus? ?: FetchDataErrorStatus.UNKNOWN_ERROR
                    Snackbar.make(
                        edit_root_layout,
                        error.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
//            REQUEST_CODE_FC ->
//                if (resultCode == Activity.RESULT_OK) {
//                    certificateUri = data?.data
//                    certificateUri?.path?.let{
//                        edit_certificate_text.text = it.substring(it.lastIndexOf("/") + 1)
//                    }
//                }
            else -> Log.d(EDIT_TAG, "Unexpected activity returned some result!!!")
        }
    }
}
