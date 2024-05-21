package money.myhubble.storesdk

import android.content.Intent
import android.os.Build
import android.text.Html
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException


class HubbleWebViewClient(
    private val baseUrl: String,
    private val fragment: HubbleFragment,
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
            fragment.context?.startActivity(intent)
        } catch (err: Exception) {
            return false;
        }
        return true
    }

}

class WebAppInterface(
    val fragment: HubbleFragment
) {
    @JavascriptInterface
    fun close() {
        fragment.activity?.finish()
    }

    @JavascriptInterface
    fun reload() {
        fragment.reload()
    }

    @JavascriptInterface
    fun onAnalyticsEvent(eventName: String, properties: String?) {
        Hubble.processAnalyticsEvent(eventName, decodeString(properties ?: "{}"))
    }

    @Throws(JSONException::class)
    private fun decodeString(response: String): Map<String, Any> {
        val type = object : TypeToken<Map<String, Any>>() {}.type

        return if (Build.VERSION.SDK_INT >= 24) {
            Gson().fromJson(
                Html.fromHtml(response, Html.FROM_HTML_MODE_LEGACY).toString(),
                type
            )
        } else {
            Gson().fromJson(Html.fromHtml(response).toString(), type)
        }
    }

}