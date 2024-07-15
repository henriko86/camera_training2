package com.yuruneji.cameratraining2.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.databinding.ActivityMainBinding
import com.yuruneji.cameratraining2.presentation.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate()")

        // enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // val navView: BottomNavigationView = binding.navView
        //
        // val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // // Passing each menu ID as a set of Ids because each
        // // menu should be considered as top level destinations.
        // val appBarConfiguration = AppBarConfiguration(
        //     setOf(
        //         R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
        //     )
        // )
        // setupActionBarWithNavController(navController, appBarConfiguration)
        // navView.setupWithNavController(navController)

        // if (savedInstanceState == null) {
        //     supportFragmentManager.beginTransaction()
        //         .replace(R.id.container, HomeFragment.newInstance())
        //         .commitNow()
        // }
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart()")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume()")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause()")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop()")
    }

    override fun onRestart() {
        super.onRestart()
        Timber.d("onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy()")
    }
}
