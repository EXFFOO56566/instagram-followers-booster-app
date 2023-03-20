package com.lxquyen.instabooster.ui.explore

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.lxquyen.instabooster.databinding.ActivityWebviewBinding
import com.lxquyen.instabooster.ui.base.BaseActivity
import com.ohayo.core.ui.extensions.setWindowStatusNav

/**
 * Created by Furuichi on 08/07/2022
 */
class WebViewActivity : BaseActivity<ActivityWebviewBinding>(ActivityWebviewBinding::inflate) {

    private val extraUrl: String
        get() = intent.getStringExtra(EXTRA_URL) ?: throw NullPointerException("Oops...`EXTRA_URL` is null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowStatusNav(Color.TRANSPARENT, flagLight = true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        viewBinding.btnBack.setOnClickListener {
            finish()
        }

        viewBinding.webView.apply {
            loadUrl(extraUrl)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }
    }

    companion object {
        private const val EXTRA_URL = "EXTRA_URL"

        fun start(context: Context?, url: String) {
            val intent = Intent(context, WebViewActivity::class.java)
                .apply {
                    putExtra(EXTRA_URL, url)
                }
            context?.startActivity(intent)
        }
    }
}