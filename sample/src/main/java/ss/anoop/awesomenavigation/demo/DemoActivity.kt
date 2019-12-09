package ss.anoop.awesomenavigation.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_demo_activity.*
import ss.anoop.awesomenavigation.OnNavigationSelectedListener

class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_demo_activity)

        bottomNavigation.setOnNavigationSelectedListener(object : OnNavigationSelectedListener {
            override fun onSelectNavigation(id: Int, position: Int) {
                Log.d("AwesomeNavigation", "onSelectNavigation, id = $id, position = $position")
            }

            override fun onReselectNavigation(id: Int, position: Int) {
                Log.d("AwesomeNavigation", "onReselectNavigation, id = $id, position = $position")
            }
        })
    }
}