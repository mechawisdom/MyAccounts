package com.mkdevelopment.myaccounts.utils

data class DataModel(
    val MyAccounts: MyAccounts
)

data class MyAccounts(
    val accounts: List<Account>,
    val categories: List<Category>
)

data class Account(
    val id: Int,
    val categoryId: Int,
    val title: String,
    val name: String,
    val surname: String,
    val gender: String,
    val birthday: String,
    val username: String,
    val password: String,
    val email: String,
    val phone: String,
    val recoveryEmail: String,
    val recoveryPhone: String,
    val securityQuestion: String,
    val securityQuestionAnswer: String,
    val address: String,
    val other: String,
    val addedTime: String,
    val updatedTime: String,
    val iconPosition: Int
)

data class Category(
    val id: Int,
    val title: String
)
