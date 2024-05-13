package money.myhubble.store_sdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import java.lang.Exception

class HubbleStore (): ComponentActivity() {
    private lateinit var clientId: String
    private lateinit var  clientSecret: String
    private lateinit var  authToken: String
    private var isProdEnv = true
    val baseUrl: String
        get() = if (isProdEnv){
            "vouchers.myhubble.money"
        }else {
            "vouchers.dev.myhubble.money"
        }


    fun initiate(context: Context, clientId: String, clientSecret: String, authToken: String, isProdEnv: Boolean){

        if(clientId.isEmpty() || clientSecret.isEmpty() || authToken.isEmpty()){
            return
        }

        val intent = Intent(context, money.myhubble.store_sdk.HubbleStore::class.java).apply {
            putExtra("clientId", clientId  )
            putExtra("clientSecret", clientSecret)
            putExtra("authToken", authToken)
            putExtra("isProdEnv", isProdEnv)
        }
        context.startActivity(intent)

    }




    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clientId = intent.getStringExtra("clientId") ?: ""
        clientSecret = intent.getStringExtra("clientSecret") ?: ""
        authToken = intent.getStringExtra("authToken") ?: ""
        isProdEnv = intent.getBooleanExtra("isProdEnv", false)

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
        webView.addJavascriptInterface(WebAppInterface(this), "HostComponent")

        webView.webViewClient = MyWebViewClient(this@HubbleStore, webView, baseUrl)
        constraintLayout.addView(webView)

        val url = "https://$baseUrl/classic?clientId=$clientId&clientSecret=$clientSecret&token=$authToken"
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


private class MyWebViewClient(val activity: money.myhubble.store_sdk.HubbleStore, val webView: WebView, val baseUrl: String) : WebViewClient() {

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
        } catch (err: Exception){
            return false;
        }
        return true
    }


}
class WebAppInterface(private  val activity: HubbleStore){
    @JavascriptInterface
    fun onReady() {
        activity.showWebView()
    }

    @JavascriptInterface
    fun close() {
        activity.finish()
    }

}