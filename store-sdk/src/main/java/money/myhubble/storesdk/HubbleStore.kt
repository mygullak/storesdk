package money.myhubble.storesdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet


object Hubble {
    private lateinit var env: String
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private lateinit var token: String

    fun init(env: String,
             clientId: String,
             clientSecret: String,
             token: String) {
        this.env = env
        this.clientId = clientId
        this.clientSecret = clientSecret
        this.token = token
    }

    fun open(context: Context) {
        if (clientId.isEmpty() || clientSecret.isEmpty() || token.isEmpty()) {
            return
        }

        val intent = Intent(context, HubbleStore::class.java).apply {
            putExtra("clientId", clientId)
            putExtra("clientSecret", clientSecret)
            putExtra("authToken", token)
            putExtra("env", env)
        }
        context.startActivity(intent)
    }

}

class HubbleStore() : ComponentActivity() {
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private lateinit var authToken: String
    private var env = "prod"

    val baseUrl: String
        get() = if (env == "prod") {
            "vouchers.myhubble.money"
        } else {
            "vouchers.dev.myhubble.money"
        }

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clientId = intent.getStringExtra("clientId") ?: ""
        clientSecret = intent.getStringExtra("clientSecret") ?: ""
        authToken = intent.getStringExtra("authToken") ?: ""
        env = intent.getStringExtra("env") ?: "prod"

        val constraintLayout = ConstraintLayout(this)
        constraintLayout.setBackgroundColor(Color.WHITE)
        constraintLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        setContentView(constraintLayout)
        setupProgressBar()
        constraintLayout.addView(progressBar)
        setupConstraints(constraintLayout)
        val webViewLayout = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        webView = WebView(this)
        webView.id = View.generateViewId()
        webView.layoutParams = webViewLayout
        webView.visibility = View.INVISIBLE


        webView.apply {
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.javaScriptEnabled = true
            settings.databaseEnabled = true
            settings.domStorageEnabled = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webView.addJavascriptInterface(WebAppInterface(this), "AndroidHost")

        webView.webViewClient = MyWebViewClient(this@HubbleStore, baseUrl)
        constraintLayout.addView(webView)

        val url =
            "https://$baseUrl/classic?clientId=$clientId&clientSecret=$clientSecret&token=$authToken"
        webView.loadUrl(url)

    }

    private fun setupProgressBar() {
        progressBar = ProgressBar(this)
        progressBar.id = View.generateViewId()
        val progressBarParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
        )
        progressBar.layoutParams = progressBarParams
    }

    private fun setupConstraints(constraintLayout: ConstraintLayout) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout);
        constraintSet.connect(
            progressBar.getId(), ConstraintSet.LEFT,
            ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0
        );
        constraintSet.connect(
            progressBar.getId(), ConstraintSet.RIGHT,
            ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0
        );
        constraintSet.connect(
            progressBar.getId(), ConstraintSet.TOP,
            ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0
        );
        constraintSet.connect(
            progressBar.getId(), ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0
        );
        constraintSet.applyTo(constraintLayout);
    }


    //Handles back button for web-view
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun showWebView() {
        runOnUiThread {
            progressBar.visibility = View.INVISIBLE
            webView.visibility = View.VISIBLE
        }
    }

}


private class MyWebViewClient(
    val activity: HubbleStore,
    val baseUrl: String
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        activity.showWebView()
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url
        url?.let {
            if (url.toString().startsWith(baseUrl)
            ) {
                return false
            }
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW, url)
            activity.startActivity(intent)
        } catch (err: Exception) {
            return false;
        }
        return true
    }

}

class WebAppInterface(private val activity: HubbleStore) {
    @JavascriptInterface
    fun close() {
        activity.finish()
    }

}