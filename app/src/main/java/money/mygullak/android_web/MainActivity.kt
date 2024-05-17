package money.mygullak.android_web

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import money.mygullak.android_web.ui.theme.HubbleandroidwebTheme
import money.myhubble.storesdk.HubbleFragment
import androidx.appcompat.app.AppCompatActivity
import money.myhubble.storesdk.Hubble
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import android.webkit.WebView
import android.view.KeyEvent
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject


class MainActivity : AppCompatActivity() {


    // this must be there in your activity if you want
    // to launch the Hubble webview in a fragment
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val hubbleFragment = Hubble.getHubbleFragment();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(hubbleFragment.isVisible && hubbleFragment.webView.canGoBack()){
                hubbleFragment.webView.goBack();
                return true;
            }

            else if (hubbleFragment.isVisible) {
                supportFragmentManager.beginTransaction().hide(hubbleFragment).commit()
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Hubble.init(
            env = "debug",
            clientId = "visit-health",
            clientSecret = "sCOZ07mzht",
            token = "JtKogLnhk0huM2wHMbr288d7iok_xrKwkv9N5PqwbE9D5HzAMrPr9WyUj6DJ0r_L4AeF0DIXZshTXr0PLNdOJ6IcTeiR49AhP5eb5ximvQ8",
            onAnalyticsEvent = { eventName, properties ->
                println("Event from fragment is : $eventName")
                // decode and print the properties
                // println("Properties: $properties")
                val jsonObject = Gson().fromJson(properties, JsonObject::class.java)
                println("Properties: $jsonObject")
            },
            onAppBarBackButtonClicked = {
                supportFragmentManager.beginTransaction().hide(Hubble.getHubbleFragment()).commit()
            }
        )


        setContent {
            // HubbleandroidwebTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HubbleFragmentButton()
                    Spacer(modifier = Modifier.height(16.dp)) // Add some space between the buttons
                    HubbleActivityButton()
                }

            }
            // }
        }
    }

    @Composable
    // This function will open the Hubble webview in a fragment
    fun HubbleFragmentButton() {
        val context = LocalContext.current
        val hubbleFragment = Hubble.getHubbleFragment()
        Box(
        ) {
            Button(
                onClick = {
                        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                            .replace(
                                android.R.id.content,
                                hubbleFragment,
                                "hubble_sdk"
                            )
                            .commit()
                },
                modifier = Modifier
                    .width(130.dp)
                    .height(50.dp),
                shape = RectangleShape
            ) {
                Text(text = "Open Fragment")
            }
        }
    }

    @Composable
    // This function will open the Hubble webview in an activity
    fun HubbleActivityButton() {
        val context = LocalContext.current
        Box(
        ) {
            Button(
                onClick = {
                    Hubble.launchActivity(context)
                },

                modifier = Modifier
                    .width(130.dp)
                    .height(50.dp),
                shape = RectangleShape
            ) {
                Text(text = "Open Activity")
            }
        }

    }
}



