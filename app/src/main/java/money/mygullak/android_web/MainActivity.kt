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
import money.myhubble.store_sdk.HubbleStore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val hubbleSDK = HubbleStore()
        setContent {
            HubbleandroidwebTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HubbleButton()
                }
            }
        }
    }
}

@Composable
fun HubbleButton() {
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                      val hubbleSDK = HubbleStore()
                hubbleSDK.initiate(context,
                    "visit-health",
                    "sCOZ07mzht",
                    "JtKogLnhk0huM2wHMbr288d7iok_xrKwkv9N5PqwbE9D5HzAMrPr9WyUj6DJ0r_L4AeF0DIXZshTXr0PLNdOJ6IcTeiR49AhP5eb5ximvQ8",
                    false)
            },

            modifier = Modifier.width(130.dp).height(50.dp),
            shape = RectangleShape
        ) {
            Text(text = "Open Hubble")
        }
    }

}


