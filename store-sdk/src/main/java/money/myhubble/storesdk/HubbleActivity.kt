package money.myhubble.storesdk

import android.R
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat


class HubbleActivity : AppCompatActivity() {

    private lateinit var hubbleFragment: HubbleFragment
    private var hubbleFragmentTag = "hubble-webview-fragment"
    private var defaultStatusBarColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        val window = window
        defaultStatusBarColor = window.statusBarColor
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true

        hubbleFragment = HubbleFragment().apply {
            arguments = intent.extras
        }

        supportFragmentManager.beginTransaction()
                .replace(
                        android.R.id.content,
                        hubbleFragment,
                        hubbleFragmentTag
                )
                .commit()
    }

    override fun onKeyDown(keyCode: Int, eventName: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val hubbleBackCompleted = hubbleFragment.goBack()
            if (hubbleBackCompleted) {
                return true
            }
        }

        return super.onKeyDown(keyCode, eventName);
    }

    override fun onStop() {
        super.onStop()
        val window = window
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        window.statusBarColor = defaultStatusBarColor;
        windowInsetsController.isAppearanceLightStatusBars = false
    }
}
