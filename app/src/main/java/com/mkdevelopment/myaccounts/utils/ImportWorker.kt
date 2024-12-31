package com.mkdevelopment.myaccounts.utils

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.mkdevelopment.myaccounts.activity.ImportDataActivity
import com.mkdevelopment.myaccounts.database.AccountEntity
import com.mkdevelopment.myaccounts.database.CategoryEntity
import com.mkdevelopment.myaccounts.viewmodel.AccountDataViewModel
import com.mkdevelopment.myaccounts.viewmodel.CategoryDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImportWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    private val accountDataViewModel: AccountDataViewModel by lazy {
        AccountDataViewModel(context.applicationContext as Application)
    }
    private val categoryDataViewModel: CategoryDataViewModel by lazy {
        CategoryDataViewModel(context.applicationContext as Application)
    }


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val uri: Uri = inputData.getString("uri")?.toUri() ?: return@withContext Result.failure()
            val inputStream = applicationContext.contentResolver.openInputStream(uri)
            val jsonString = inputStream?.bufferedReader().use { it?.readText() }

            val data = Gson().fromJson(jsonString, DataModel::class.java)

            for (category in data.MyAccounts.categories) {
                val categoryEntity = CategoryEntity(category.id, category.title)
                categoryDataViewModel.insertData(categoryEntity)
            }

            for (account in data.MyAccounts.accounts) {
                val accountEntity = AccountEntity(
                    account.id,
                    account.categoryId,
                    account.title,
                    account.name,
                    account.surname,
                    account.gender,
                    account.birthday,
                    account.username,
                    account.password,
                    account.email,
                    account.phone,
                    account.recoveryEmail,
                    account.recoveryPhone,
                    account.securityQuestion,
                    account.securityQuestionAnswer,
                    account.address,
                    account.other,
                    account.addedTime,
                    account.updatedTime,
                    account.iconPosition
                )
                accountDataViewModel.insertData(accountEntity)
            }

            Result.success()
        } catch (e: Exception) {
            val outputData = Data.Builder()
                .putString("error_message",  e.toString())
                .build()

            Result.failure(outputData)
        }
    }
}
