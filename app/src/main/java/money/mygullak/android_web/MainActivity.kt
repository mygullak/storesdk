package money.mygullak.android_web

import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import android.view.KeyEvent
import com.google.gson.Gson
import com.google.gson.JsonObject
import money.myhubble.storesdk.Hubble
import money.myhubble.storesdk.HubbleActivity
import money.myhubble.storesdk.Key


class MainActivity : AppCompatActivity() {
    // This example show the usage of HubbleFragmentController and HubbleActivityController
    // to launch the Hubble webview in a fragment and an activity respectively

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    fun HubbleFragmentButton() {
        val context = LocalContext.current
        Box(
        ) {
            Button(
                onClick = {
                    val intent = Intent(context, FragmentTestActivity::class.java)
                    context.startActivity(intent)
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
    fun HubbleActivityButton() {
        val context = LocalContext.current
        Box(
        ) {
            Button(
                onClick = {
                    // initialising the HubbleFragment
                    Hubble.init(
                        env = "debug",
                        clientId = "visit-health",
                        clientSecret = "sCOZ07mzht",
                        token = "JtKogLnhk0huM2wHMbr288d7iok_xrKwkv9N5PqwbE9D5HzAMrPr9WyUj6DJ0r_L4AeF0DIXZshTXr0PLNdOJ6IcTeiR49AhP5eb5ximvQ8",
                    )
                    Hubble.open(context)
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



