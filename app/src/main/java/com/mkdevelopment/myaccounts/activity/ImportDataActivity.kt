package com.mkdevelopment.myaccounts.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mkdevelopment.myaccounts.R
import com.mkdevelopment.myaccounts.common.Constant.REQUEST_CODE_JSON_FILE
import com.mkdevelopment.myaccounts.databinding.ActivityImportDataBinding
import com.mkdevelopment.myaccounts.utils.ImportWorker
import com.mkdevelopment.myaccounts.utils.ShakeAnimatorHelper

class ImportDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImportDataBinding
    private var getUri: Uri? = null


    @SuppressLint("IntentReset", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SDK_INT < Build.VERSION_CODES.O) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.app_level_n)
        }

        binding = ActivityImportDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener { finish() }

        binding.fileName.tag = "unselected"
        binding.fileName.setOnClickListener {
            binding.errorText.isVisible = false
            enableAllFocusable(binding.startButton)
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            startActivityForResult(
                Intent.createChooser(intent, getString(R.string.select_json_file)),
                REQUEST_CODE_JSON_FILE
            )
            binding.nestedScrollView.post {
                binding.nestedScrollView.fullScroll(View.FOCUS_UP)
            }
        }

        binding.startButton.setOnClickListener {
            if (binding.fileName.tag.equals("unselected")) {
                ShakeAnimatorHelper(applicationContext, binding.fileName, 500).startAnimation()
            } else {
                disableAllFocusable(binding.startButton)
                val inputData = Data.Builder()
                    .putString("uri", getUri.toString())
                    .build()

                val importRequest = OneTimeWorkRequestBuilder<ImportWorker>()
                    .setInputData(inputData)
                    .build()

                val workManager = WorkManager.getInstance(applicationContext)
                workManager.enqueueUniqueWork(
                    "import_worker",
                    ExistingWorkPolicy.REPLACE,
                    importRequest
                )
                workManager.getWorkInfoByIdLiveData(importRequest.id)
                    .observe(this) { workInfo ->
                        if (workInfo != null)
                            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                                binding.errorText.isVisible = false
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.import_successful),
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else if (workInfo.state == WorkInfo.State.FAILED) {
                                val errorMessage =
                                    workInfo.outputData.getString("error_message").toString()
                                binding.errorText.isVisible = true
                                binding.errorText.text =
                                    getString(R.string.import_error) + "\n" + errorMessage
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.import_error),
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("IMPORT_WORKER", errorMessage)
                                enableAllFocusable(binding.startButton)

                                binding.nestedScrollView.post {
                                    binding.nestedScrollView.fullScroll(View.FOCUS_DOWN)
                                }
                            }
                    }
            }
        }
    }

    private fun disableAllFocusable(getView: View) {
        getView.isEnabled = false
        getView.isFocusable = false
        getView.isClickable = false
    }

    private fun enableAllFocusable(getView: View) {
        getView.isEnabled = true
        getView.isFocusable = true
        getView.isClickable = true
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_JSON_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                getUri = uri
                binding.fileName.tag = "selected"
                binding.fileName.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(applicationContext, R.drawable.ic_ui_check_mark),
                    null
                )
                val displayName = getFileName(uri)
                binding.fileName.text = displayName
                /*
                Log.d(TAG, displayName.toString())
                jsonString?.let { json ->
                    Log.d(TAG, json)
                }
                */
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        var displayName: String? = null
        cursor?.use {
            if (it.moveToFirst()) {
                displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return displayName
    }

    @Suppress("DEPRECATION")
    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.from_left,
            R.anim.to_right
        )
    }
}