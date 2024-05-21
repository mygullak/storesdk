package money.myhubble.storesdk

import android.content.Intent
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONException
import org.json.JSONObject


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
        if (properties==null) {
            Hubble.processAnalyticsEvent(eventName, null)
        }
        else {
            Hubble.processAnalyticsEvent(eventName, decodeString(properties))
        }
    }

    private fun decodeString(properties: String): Map<String, Any> {
        try {
            val jsonObject = JSONObject(properties)
            val map: MutableMap<String, Any> = HashMap()
            val iterator = jsonObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next()
                val value = jsonObject[key]
                map[key] = value
            }
            return map
        } catch (e: JSONException) {
            Log.e(Hubble.LOG_TAG, "Failure parsing event properties. Returning empty map. ${e.message}")
            return mapOf()
        }
    }

}