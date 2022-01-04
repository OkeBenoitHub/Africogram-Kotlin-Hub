package com.africogram.www.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.africogram.www.R
import com.africogram.www.utils.*

class MainActivity : AppCompatActivity() {
    private lateinit var mNavController: NavController
    private val networkMonitor = NetworkUtil(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Add support for up button for fragment navigation
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, mNavController)

        // Change action Bar background color
        MainUtil().setActionBarBackgroundColor(this, supportActionBar, R.color.colorPrimary)

        // Set navigation bottom background color
        MainUtil().setBottomBarNavigationBackgroundColor(
            window = window,
            this,
            R.color.colorPrimary,
            R.color.bottom_black_color
        )

        /**
         * Monitor network status
         * Save status state to Shared preferences
         */
        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {
                                SharedPrefUtil().writeDataStringToSharedPreferences(this,
                                    NETWORK_STATUS_PREF_KEY, NETWORK_WIFI)
                            }
                            ConnectionType.Cellular -> {
                                SharedPrefUtil().writeDataStringToSharedPreferences(this,
                                    NETWORK_STATUS_PREF_KEY, NETWORK_CELLULAR)
                            }
                            else -> { }
                        }
                    }
                    false -> {
                        SharedPrefUtil().writeDataStringToSharedPreferences(this,
                            NETWORK_STATUS_PREF_KEY, NO_NETWORK)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        networkMonitor.register()
    }

    override fun onStop() {
        super.onStop()
        networkMonitor.unregister()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(mNavController, null)
    }
}