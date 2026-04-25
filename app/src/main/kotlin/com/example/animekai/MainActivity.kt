package com.example.animekai

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.ProgressBar
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLayout: LinearLayout
    private lateinit var root: FrameLayout
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    private val homeUrl = "https://anikai.to/home"

    private val allowedHosts = listOf(
        "animekai.to",
        "www.animekai.to",
        "anikai.to",
        "www.anikai.to",
        "animekai.fi",
        "www.animekai.fi",
        "animekai.fo",
        "www.animekai.fo",
        "animekai.gs",
        "www.animekai.gs",
        "animekai.la",
        "www.animekai.la",
        "animekai.to",
        "www.animekai.to",
    )

    private fun isAllowed(url: String): Boolean {
        return try {
            val host = java.net.URI(url).host?.lowercase() ?: return false
            allowedHosts.any { host == it || host.endsWith(".$it") }
        } catch (e: Exception) {
            false
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        root = FrameLayout(this)
        root.setBackgroundColor(0xFF000000.toInt())

        webView = WebView(this)
        webView.setBackgroundColor(0xFF000000.toInt())
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            useWideViewPort = true
            loadWithOverviewMode = true
            userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                return !isAllowed(url)
            }

            override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                progressBar.visibility = View.VISIBLE
                errorLayout.visibility = View.GONE
            }

            override fun onPageFinished(view: WebView, url: String) {
                progressBar.visibility = View.GONE
                view.evaluateJavascript("""
                    console.log = function(){};
                    console.warn = function(){};
                    console.error = function(){};
                    console.info = function(){};
                """.trimIndent(), null)
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) {
                    progressBar.visibility = View.GONE
                    errorLayout.visibility = View.VISIBLE
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progressBar.progress = newProgress
                progressBar.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
            }

            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                customView = view
                customViewCallback = callback
                root.addView(view, FrameLayout.LayoutParams(-1, -1))
                webView.visibility = View.GONE
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                hideSystemUi()
            }

            override fun onHideCustomView() {
                customView?.let {
                    root.removeView(it)
                    customView = null
                }
                customViewCallback?.onCustomViewHidden()
                customViewCallback = null
                webView.visibility = View.VISIBLE
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                showSystemUi()
            }

            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
            }
        }

        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
        progressBar.isIndeterminate = false
        progressBar.max = 100
        progressBar.progressTintList = android.content.res.ColorStateList.valueOf(0xFFFF4444.toInt())
        progressBar.visibility = View.GONE
        val pbParams = FrameLayout.LayoutParams(-1, 6)

        errorLayout = LinearLayout(this)
        errorLayout.orientation = LinearLayout.VERTICAL
        errorLayout.gravity = android.view.Gravity.CENTER
        errorLayout.visibility = View.GONE
        errorLayout.setBackgroundColor(0xFF000000.toInt())

        val icon = ImageView(this)
        icon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        icon.layoutParams = LinearLayout.LayoutParams(128, 128).apply {
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }

        val title = TextView(this)
        title.text = "Failed to load page"
        title.textSize = 18f
        title.setTextColor(0xFFFFFFFF.toInt())
        title.gravity = android.view.Gravity.CENTER
        title.setPadding(0, 32, 0, 8)

        val subtitle = TextView(this)
        subtitle.text = "Check your internet connection and try again."
        subtitle.textSize = 14f
        subtitle.setTextColor(0x99FFFFFF.toInt())
        subtitle.gravity = android.view.Gravity.CENTER
        subtitle.setPadding(48, 0, 48, 32)

        val retryBtn = Button(this)
        retryBtn.text = "Retry"
        retryBtn.setOnClickListener {
            errorLayout.visibility = View.GONE
            webView.loadUrl(homeUrl)
        }

        errorLayout.addView(icon)
        errorLayout.addView(title)
        errorLayout.addView(subtitle)
        errorLayout.addView(retryBtn)

        root.addView(webView, FrameLayout.LayoutParams(-1, -1))
        root.addView(errorLayout, FrameLayout.LayoutParams(-1, -1))
        root.addView(progressBar, pbParams)

        setContentView(root)

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        webView.loadUrl(homeUrl)
    }

    override fun onBackPressed() {
        if (customView != null) {
            webView.webChromeClient?.onHideCustomView()
        } else if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun hideSystemUi() {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        root.setPadding(0, 0, 0, 0)
    }

    private fun showSystemUi() {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            show(WindowInsetsCompat.Type.systemBars())
        }
        ViewCompat.requestApplyInsets(root)
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}