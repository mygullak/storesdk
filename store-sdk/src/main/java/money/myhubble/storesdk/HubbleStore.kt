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


object Hubble {
    private lateinit var env: String
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private lateinit var token: String

    fun init(
        env: String,
        clientId: String,
        clientSecret: String,
        token: String
    ) {
        this.env = env
        this.clientId = clientId
        this.clientSecret = clientSecret
        this.token = token
    }

    fun open(context: Context) {
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


    // function to get fragment
    fun getFragment(): WebViewFragment {
        val fragment = WebViewFragment().apply {
            arguments = Bundle().apply {
                putString("clientId", clientId)
                putString("clientSecret", clientSecret)
                putString("authToken", token)
                putString("env", env)
            }
        }
        return fragment
    }

}

class HubbleStoreActivity : AppCompatActivity() {
    private lateinit var fragment: WebViewFragment
    private val fragmentTag = "hubble_webview_fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragment = WebViewFragment().apply {
            arguments = Bundle().apply {
                putString("clientId", intent.getStringExtra("clientId"))
                putString("clientSecret", intent.getStringExtra("clientSecret"))
                putString("authToken", intent.getStringExtra("authToken"))
                putString("env", intent.getStringExtra("env"))
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment, fragmentTag)
            .commit()
    }

    //Handles back button for web-view
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && fragment.webView.canGoBack()) {
            fragment.webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

class WebViewFragment : Fragment() {
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
        webView.addJavascriptInterface(WebAppInterface(activity), "AndroidHost")
        webView.webViewClient = MyWebViewClient(activity, baseUrl, this)

        val url =
            "https://$baseUrl/classic?clientId=$clientId&clientSecret=$clientSecret&token=$authToken"
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
    val fragment: WebViewFragment,
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

class WebAppInterface(private val activity: ComponentActivity) {
    @JavascriptInterface
    fun close() {
        activity.finish()
    }

}