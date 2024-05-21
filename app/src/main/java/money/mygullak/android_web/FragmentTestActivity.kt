package money.mygullak.android_web

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import money.myhubble.storesdk.Hubble
import money.myhubble.storesdk.HubbleFragment


class FragmentTestActivity: AppCompatActivity() {

    private lateinit var hubbleFragment: HubbleFragment

    init {
        Hubble.init(
            env = "debug",
            clientId = "visit-health",
            clientSecret = "sCOZ07mzht",
            token = "JtKogLnhk0huM2wHMbr288d7iok_xrKwkv9N5PqwbE9D5HzAMrPr9WyUj6DJ0r_L4AeF0DIXZshTXr0PLNdOJ6IcTeiR49AhP5eb5ximvQ8",
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hubbleFragment = Hubble.getFragment(this)

        supportFragmentManager.beginTransaction()
            .replace(
                android.R.id.content,
                hubbleFragment,
                "hubbleFragmentTag"
            )
            .commit()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val hubbleBackCompleted = hubbleFragment.goBack()
            if (hubbleBackCompleted) {
                return true
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}