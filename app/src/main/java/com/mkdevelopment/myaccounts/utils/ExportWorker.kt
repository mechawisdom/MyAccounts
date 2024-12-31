package com.mkdevelopment.myaccounts.utils

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mkdevelopment.myaccounts.R
import com.mkdevelopment.myaccounts.viewmodel.AccountDataViewModel
import com.mkdevelopment.myaccounts.viewmodel.CategoryDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ExportWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    private val accountDataViewModel: AccountDataViewModel by lazy {
        AccountDataViewModel(context.applicationContext as Application)
    }
    private val categoryDataViewModel: CategoryDataViewModel by lazy {
        CategoryDataViewModel(context.applicationContext as Application)
    }

    override suspend fun doWork(): Result {
        return try {
            val exportResult = exportData()
            if (exportResult) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (exception: Exception) {
            showToast(
                applicationContext.getString(
                    R.string.an_unknown_error_occurred
                ),
                Toast.LENGTH_SHORT
            )
            Log.e(TAG, exception.toString())
            Result.failure()
        }
    }

    private suspend fun exportData(): Boolean {
        return withContext(Dispatchers.IO) {
            val accounts = accountDataViewModel.getAllAccounts()
            val categories = categoryDataViewModel.getAllCategories()
            val gson = GsonBuilder().setPrettyPrinting().create()

            val jsonObject = JsonObject().apply {
                add("MyAccounts", JsonObject().apply {
                    add("accounts", JsonArray().apply {
                        accounts.forEach { account ->
                            add(JsonObject().apply {
                                addProperty("id", account.id)
                                addProperty("categoryId", account.categoryId)
                                addProperty("title", account.title)
                                addProperty("name", account.name)
                                addProperty("surname", account.surname)
                                addProperty("gender", account.gender)
                                addProperty("birthday", account.birthday)
                                addProperty("username", account.username)
                                addProperty("password", account.password)
                                addProperty("email", account.email)
                                addProperty("phone", account.phone)
                                addProperty("recoveryEmail", account.recoveryEmail)
                                addProperty("recoveryPhone", account.recoveryPhone)
                                addProperty("securityQuestion", account.securityQuestion)
                                addProperty(
                                    "securityQuestionAnswer",
                                    account.securityQuestionAnswer
                                )
                                addProperty("address", account.address)
                                addProperty("other", account.other)
                                addProperty("addedTime", account.addedTime)
                                addProperty("updatedTime", account.updatedTime)
                                addProperty("iconPosition", account.iconPosition)
                            })
                        }
                    })

                    add("categories", JsonArray().apply {
                        categories.forEach { category ->
                            add(JsonObject().apply {
                                addProperty("id", category.id)
                                addProperty("title", category.title)
                            })
                        }
                    })
                })
            }

            val exportData = gson.toJson(jsonObject)
            saveToFile(exportData)
        }
    }

    private fun saveToFile(data: String): Boolean {
        val fileName = "MyAccounts_${System.currentTimeMillis()}.json"
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        return try {
            if (file.exists()) {
                showToast(
                    applicationContext.getString(
                        R.string.file_already_exist
                    ),
                    Toast.LENGTH_SHORT
                )
                return false
            }

            val writer = FileWriter(file)
            writer.use {
                it.write(data)
            }
            showToast(
                applicationContext.getString(R.string.data_exported_successfully) + "\n" + file.absolutePath,
                Toast.LENGTH_LONG
            )
            true
        } catch (exception: IOException) {
            Log.e(TAG, exception.toString())
            false
        }
    }


    private fun showToast(message: String, toastLength: Int) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, message, toastLength).show()
        }
    }

    companion object {
        private const val TAG = "ExportWorker"
    }
}
