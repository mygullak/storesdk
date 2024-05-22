package money.myhubble.storesdk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log


data class HubbleStyleOptions(
        val disableHomeBackBtn: Boolean
)

object Hubble {
    private lateinit var env: String
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private lateinit var token: String
    private lateinit var onAnalyticsEvent: (eventName: String, Map<String, Any>) -> Unit
//    private lateinit var style: HubbleStyleOptions

    const val LOG_TAG = "hubble"

    fun init(
            env: String,
            clientId: String,
            clientSecret: String,
            token: String,
            // style: HubbleStyleOptions = HubbleStyleOptions(disableHomeBackBtn = false), TODO later
            onAnalyticsEvent: (eventName: String, properties: Map<String, Any>) -> Unit = { e, p ->
                Log.i(
                        LOG_TAG,
                        "Event - $e - received from Hubble webview"
                )
            }
    ) {
        this.env = env
        this.clientId = clientId
        this.clientSecret = clientSecret
        this.token = token
        this.onAnalyticsEvent = onAnalyticsEvent
//        this.style = style
    }

    fun open(context: Context) {
        if (clientId.isEmpty() || clientSecret.isEmpty() || token.isEmpty()) {
            return
        }

        val intent = Intent(context, HubbleActivity::class.java).apply {
            putExtra(Key.CLIENT_ID, clientId)
            putExtra(Key.CLIENT_SECRET, clientSecret)
            putExtra(Key.TOKEN, token)
            putExtra(Key.ENV, env)
        }
        context.startActivity(intent)
    }

    fun processAnalyticsEvent(event: String, properties: Map<String, Any>?) {
        this.onAnalyticsEvent(event, properties ?: mapOf())
    }

    fun getFragment(): HubbleFragment {
        val fragment: HubbleFragment = HubbleFragment().apply {
            arguments = Bundle().apply {
                putString(Key.CLIENT_ID, clientId)
                putString(Key.CLIENT_SECRET, clientSecret)
                putString(Key.TOKEN, token)
                putString(Key.ENV, env)
            }
        }

        return fragment
    }
}



