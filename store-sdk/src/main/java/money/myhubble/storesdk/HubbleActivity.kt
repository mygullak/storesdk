package money.myhubble.storesdk

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity

class HubbleActivity : AppCompatActivity() {

    private lateinit var hubbleFragment: HubbleFragment
    private var hubbleFragmentTag = "hubble-webview-fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hubbleFragment = Hubble.getFragment(this)

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
}
