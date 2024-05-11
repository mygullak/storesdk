package money.myhubble.store_sdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
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

        val webViewLayout = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        webView = WebView(this)
        webView.id = View.generateViewId()
        webView.layoutParams = webViewLayout
        constraintLayout.addView(webView)

        webView.apply {
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.javaScriptEnabled = true
            settings.databaseEnabled = true
            settings.domStorageEnabled = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.webViewClient = MyWebViewClient(this@HubbleStore, webView, baseUrl)

        val url = "https://$baseUrl/classic?clientId=$clientId&clientSecret=$clientSecret&token=$authToken"
        webView.loadUrl(url)

    }


    //Handles back button for web-view
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
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