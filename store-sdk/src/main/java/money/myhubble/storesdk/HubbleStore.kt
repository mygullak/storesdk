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
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import android.os.Build
import android.text.Html


object Hubble {

    private lateinit var env: String
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private lateinit var token: String

    lateinit var onAnalyticsEvent: (eventName: String, properties: JsonObject?) -> Unit
    lateinit var onAppBarBackButtonClicked: () -> Unit

    var frg: HubbleFragment? = null


    fun init(
        env: String,
        clientId: String,
        clientSecret: String,
        token: String,
        onAnalyticsEvent: (eventName: String, properties: JsonObject?) -> Unit,
        onAppBarBackButtonClicked: () -> Unit,
    ) {
        this.env = env
        this.clientId = clientId
        this.clientSecret = clientSecret
        this.token = token
        this.onAnalyticsEvent = onAnalyticsEvent
        this.onAppBarBackButtonClicked = onAppBarBackButtonClicked
    }


    fun getHubbleFragment(): HubbleFragment {

        if (frg != null) {
            return frg as HubbleFragment
        }

        frg = HubbleFragment(this).apply {
            arguments = Bundle().apply {
                putString("clientId", clientId)
                putString("clientSecret", clientSecret)
                putString("authToken", token)
                putString("env", env)
            }
        }

        return frg as HubbleFragment


    }


    fun launchActivity(context: Context) {
        if (clientId.isEmpty() || clientSecret.isEmpty() || token.isEmpty()) {
            return
        }

        val intent = Intent(context, HubbleStoreActivity::class.java).apply {
            putExtra("clientId", clientId)
            putExtra("clientSecret", clientSecret)
            putExtra("authToken", token)
            putExtra("env", env)
        }
        context.startActivity(intent)
    }


}


class HubbleStoreActivity : AppCompatActivity() {

//    private val hubbleFragmentController = HubbleFragmentController(supportFragmentManager);


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Hubble.init(
            env = intent.getStringExtra("env") ?: "prod",
            clientId = intent.getStringExtra("clientId") ?: "",
            clientSecret = intent.getStringExtra("clientSecret") ?: "",
            token = intent.getStringExtra("authToken") ?: "",
            onAnalyticsEvent = { eventName, properties -> Unit },
            onAppBarBackButtonClicked = {
                finish()
            }
        );



        supportFragmentManager.beginTransaction()
            .replace(
                android.R.id.content,
                Hubble.getHubbleFragment(),
                "hubble_sdk"
            )
            .commit()
    }

    //Handles back button for web-view
    override fun onKeyDown(keyCode: Int, eventName: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Hubble.getHubbleFragment().isVisible && Hubble.getHubbleFragment().webView.canGoBack()) {
                Hubble.getHubbleFragment().webView.goBack();
                return true;
            }

        }
        return super.onKeyDown(keyCode, eventName);
    }
}

class HubbleFragment(private val hubble: Hubble) : Fragment() {
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private lateinit var authToken: String
    private var env = "prod"
    lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    val baseUrl: String
        get() = if (env == "prod") {
            "vouchers.myhubble.money"
        } else {
            "vouchers.dev.myhubble.money"
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        clientId = arguments?.getString("clientId") ?: ""
        clientSecret = arguments?.getString("clientSecret") ?: ""
        authToken = arguments?.getString("authToken") ?: ""
        env = arguments?.getString("env") ?: "prod"
        val context = requireContext()

        val constraintLayout = ConstraintLayout(context)
        constraintLayout.setBackgroundColor(Color.WHITE)
        constraintLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        setupProgressBar(context)
        constraintLayout.addView(progressBar)
        setupConstraints(constraintLayout)
        webView = getWebView(context)
        constraintLayout.addView(webView)

        return constraintLayout
    }

    fun getWebView(context: Context): WebView {
        val activity = requireActivity()

        val webView = WebView(context)
        webView.id = View.generateViewId()
        webView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        webView.visibility = View.INVISIBLE

        webView.apply {
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.javaScriptEnabled = true
            settings.databaseEnabled = true
            settings.domStorageEnabled = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webView.addJavascriptInterface(
            WebAppInterface(hubble),
            "AndroidHost"
        )
        webView.webViewClient = MyWebViewClient(activity, baseUrl, this)

        val url =
            "https://$baseUrl/classic?clientId=$clientId&clientSecret=$clientSecret&token=$authToken&wrap-plt=an"
        webView.loadUrl(url)

        return webView
    }


    private fun setupProgressBar(context: Context) {
        progressBar = ProgressBar(context)
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

    fun showWebView() {
        progressBar.visibility = View.INVISIBLE
        webView.visibility = View.VISIBLE
    }
}


private class MyWebViewClient(
    val activity: ComponentActivity,
    val baseUrl: String,
    val fragment: HubbleFragment,
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        fragment.showWebView()
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

class WebAppInterface(
    private val hubble: Hubble
) {
    @JavascriptInterface
    fun close() {
        hubble.onAppBarBackButtonClicked()
    }

    @JavascriptInterface
    fun onAnalyticsEvent(eventName: String, properties: String?) {
        hubble.onAnalyticsEvent(eventName, decodeString(properties ?: "{}"))
    }

    @Throws(JSONException::class)
    private fun decodeString(response: String): JsonObject {
        return if (Build.VERSION.SDK_INT >= 24) {
            Gson().fromJson(
                Html.fromHtml(response, Html.FROM_HTML_MODE_LEGACY).toString(),
                JsonObject::class.java
            )
        } else {
            Gson().fromJson(Html.fromHtml(response).toString(), JsonObject::class.java)
        }
    }

}
