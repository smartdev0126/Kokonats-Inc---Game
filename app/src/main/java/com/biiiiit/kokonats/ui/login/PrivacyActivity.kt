package com.biiiiit.kokonats.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebView
import android.webkit.WebViewClient
import com.biiiiit.kokonats.databinding.ActivityPrivacyBinding
import com.biiiiit.lib_base.base.BaseActivity
import com.biiiiit.lib_base.base.EmptyViewModel

class PrivacyActivity: BaseActivity<ActivityPrivacyBinding, EmptyViewModel>() {
    override fun createBinding(layoutInflater: LayoutInflater): ActivityPrivacyBinding =
        ActivityPrivacyBinding.inflate(layoutInflater)

    override fun getVMClazz(): Class<EmptyViewModel> = EmptyViewModel::class.java
    override val showFloat: Boolean = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = getIntent()
        val mytitle = intent.getStringExtra("title")
        val mywebviewurl = intent.getStringExtra("webviewurl")

        binding.ivBack.setOnClickListener { finish() }
        binding.privacyTitle.setText(mytitle)

        binding.privacyWebview.settings.setJavaScriptEnabled(true)
        if (mywebviewurl != null) {
            binding.privacyWebview.loadUrl(mywebviewurl)
        }
    }
}