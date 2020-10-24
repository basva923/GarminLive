package com.github.basva923.garminphoneactivity

import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.github.basva923.garminphoneactivity.controller.ActivityController
import com.github.basva923.garminphoneactivity.controller.Controllers
import com.github.basva923.garminphoneactivity.garmin.GarminActivityControl
import com.github.basva923.garminphoneactivity.garmin.GarminConnection
import com.github.basva923.garminphoneactivity.model.Model
import com.github.basva923.garminphoneactivity.settings.Settings
import com.github.basva923.garminphoneactivity.ui.dashboard.DashboardFragment
import com.github.basva923.garminphoneactivity.ui.dashboard.SmallDashboardFragment
import com.github.basva923.garminphoneactivity.ui.dashboard.TimerDashboardFragment
import com.github.basva923.garminphoneactivity.ui.map.MapFragment
import com.github.basva923.garminphoneactivity.ui.settings.SettingsFragment

internal const val TAG = "MainActivity"

class MainActivity : FragmentActivity() {
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live)

//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)

        // Instantiate a ViewPager2 and a PagerAdapter.
        Settings.load(this)

        viewPager = findViewById(R.id.pager)

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        viewPager.setCurrentItem(3, false)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        setupActivity()
    }

    private fun setupActivity() {
        if (Controllers.activityController == null)

        //////////// COMMENT THIS /////////////////////////
            Controllers.activityController = ActivityController(
                Model.track, GarminActivityControl(
                    GarminConnection(this)
                )
            )
        //////////// COMMENT THIS /////////////////////////


        //////////// UNCOMMENT THIS /////////////////////////
//        Controllers.activityController = ActivityController(Model.track, MockActivityControl())
        //////////// UNCOMMENT THIS /////////////////////////
    }

    override fun onPause() {
        Settings.save(this)
        super.onPause()
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 6

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MapFragment()
//                0 -> Fragment()
                1 -> DashboardFragment()
                2 -> SmallDashboardFragment()
                3 -> TimerDashboardFragment()
//                4 -> ChartFragment()
                else -> SettingsFragment()
            }
        }

    }
}
