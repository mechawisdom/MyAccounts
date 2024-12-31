package com.mkdevelopment.myaccounts.activity

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mkdevelopment.myaccounts.R
import com.mkdevelopment.myaccounts.adapter.LegalItemAdapter
import com.mkdevelopment.myaccounts.databinding.ActivityDisclaimerNoticeBinding
import com.mkdevelopment.myaccounts.utils.DisclaimerItem
import com.mkdevelopment.myaccounts.utils.ShakeAnimatorHelper
import com.mkdevelopment.myaccounts.utils.SharedPreferencesHelper

class DisclaimerNoticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDisclaimerNoticeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SDK_INT < Build.VERSION_CODES.O) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.app_level_n)
        }
        binding = ActivityDisclaimerNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val legalItemList: MutableList<DisclaimerItem> = ArrayList()

        val disclaimers = resources.getStringArray(R.array.disclaimers)
        for ((index, disclaimer) in disclaimers.withIndex()) {
            legalItemList.add(
                DisclaimerItem(
                    index + 1,
                    disclaimer
                )
            )
        }
        val legalItemAdapter = LegalItemAdapter(legalItemList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this@DisclaimerNoticeActivity)
        binding.recyclerView.adapter = legalItemAdapter


        binding.startButton.setOnClickListener {
            if (binding.checkbox.isChecked) {
                SharedPreferencesHelper(applicationContext).setDisclaimerAccepted(true)
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else {
                ShakeAnimatorHelper(applicationContext, binding.checkbox, 500).startAnimation()
            }
        }
    }
}