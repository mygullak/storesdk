package money.myhubble.storesdk

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment


class HubbleFragment() : Fragment() {
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private lateinit var token: String
    private var env = HubbleEnv.PROD
    lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    val baseUrl: String
        get() = if (env == HubbleEnv.PROD) {
            "vouchers.myhubble.money"
        } else {
            "vouchers.dev.myhubble.money"
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        clientId = arguments?.getString(Key.CLIENT_ID) ?: ""
        clientSecret = arguments?.getString(Key.CLIENT_SECRET) ?: ""
        token = arguments?.getString(Key.TOKEN) ?: ""
        env = arguments?.getString(Key.ENV) ?: HubbleEnv.PROD
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
            WebAppInterface(this),
            "AndroidHost"
        )
        webView.webViewClient = HubbleWebViewClient(baseUrl, this)

        val url =
            "https://$baseUrl/classic?clientId=$clientId&clientSecret=$clientSecret&token=$token&wrap-plt=an"
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

    private fun canGoBack(): Boolean {
        return webView.canGoBack()
    }

    fun goBack(): Boolean {
        if (canGoBack()) {
            webView.goBack()
            return true
        }

        return false
    }

    fun showWebView() {
        progressBar.visibility = View.INVISIBLE
        webView.visibility = View.VISIBLE
    }

    fun reload() {
        webView.reload()
    }
}