package com.noor.qwenai

import android.app.Activity
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private var webView: WebView? = null
    private var uploadCallback: ValueCallback<Array<Uri>>? = null
    private val fileRequest = 4102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val saved = getSharedPreferences("qwen", MODE_PRIVATE).getString("url", null)
        if (!saved.isNullOrBlank() && isAllowedUrl(saved)) showWebView(saved) else showConnectScreen()
    }

    private fun isAllowedUrl(value: String): Boolean = try {
        val uri = Uri.parse(value)
        val host = uri.host?.lowercase() ?: return false
        uri.scheme == "https" &&
            (host.endsWith(".trycloudflare.com") || host == "trycloudflare.com") &&
            !uri.getQueryParameter("k").isNullOrBlank()
    } catch (_: Exception) { false }

    private fun showConnectScreen() {
        webView = null
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 48, 28, 24)
            setBackgroundColor(Color.rgb(11, 11, 26))
        }
        val title = TextView(this).apply {
            text = "Qwen AI"; textSize = 28f; setTextColor(Color.WHITE)
        }
        val note = TextView(this).apply {
            text = "Kaggle Notebook চালিয়ে পাওয়া সম্পূর্ণ HTTPS trycloudflare লিংকটি এখানে পেস্ট করুন। লিংকে ?k=... access key থাকতে হবে।"
            textSize = 15f; setTextColor(Color.LTGRAY); setPadding(0, 18, 0, 18)
        }
        val url = EditText(this).apply {
            hint = "https://....trycloudflare.com/?k=..."
            setSingleLine(false); setTextColor(Color.WHITE); setHintTextColor(Color.GRAY)
            setPadding(16, 14, 16, 14); setBackgroundColor(Color.rgb(35, 35, 74))
        }
        val open = Button(this).apply {
            text = "Connect to Qwen AI"
            setOnClickListener {
                val value = url.text.toString().trim()
                if (!isAllowedUrl(value)) url.error = "সঠিক HTTPS trycloudflare লিংক ও ?k= access key দিন"
                else {
                    getSharedPreferences("qwen", MODE_PRIVATE).edit().putString("url", value).apply()
                    showWebView(value)
                }
            }
        }
        getSharedPreferences("qwen", MODE_PRIVATE).getString("url", null)?.let { url.setText(it) }
        root.addView(title); root.addView(note)
        root.addView(url, LinearLayout.LayoutParams(-1, -2)); root.addView(open)
        setContentView(root)
    }

    private fun showWebView(url: String) {
        val wv = WebView(this); webView = wv
        wv.layoutParams = ViewGroup.LayoutParams(-1, -1)
        wv.settings.javaScriptEnabled = true
        wv.settings.domStorageEnabled = true
        wv.settings.allowFileAccess = false
        wv.settings.allowContentAccess = true
        CookieManager.getInstance().setAcceptCookie(true)
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                val host = uri.host?.lowercase() ?: return true
                return !(uri.scheme == "https" &&
                    (host.endsWith(".trycloudflare.com") || host == "trycloudflare.com"))
            }
        }
        wv.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(view: WebView, callback: ValueCallback<Array<Uri>>,
                                           params: FileChooserParams): Boolean {
                uploadCallback?.onReceiveValue(null); uploadCallback = callback
                return try { startActivityForResult(params.createIntent(), fileRequest); true }
                catch (_: Exception) { uploadCallback = null; false }
            }
        }
        setContentView(wv); wv.loadUrl(url)
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileRequest) {
            val result = if (resultCode == Activity.RESULT_OK)
                WebChromeClient.FileChooserParams.parseResult(resultCode, data) else null
            uploadCallback?.onReceiveValue(result); uploadCallback = null
        }
    }

    override fun onDestroy() {
        uploadCallback?.onReceiveValue(null); uploadCallback = null
        webView?.destroy(); webView = null
        super.onDestroy()
    }

    @Deprecated("Deprecated in Android API")
    override fun onBackPressed() {
        val wv = webView
        if (wv != null && wv.canGoBack()) wv.goBack() else showConnectScreen()
    }
}
