package com.yuruneji.cameratraining2.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // トップレベルに記述する必要がある
    // private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)

        // enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // binding.fab.setOnClickListener { view ->
        //     Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //         .setAction("Action", null)
        //         .setAnchorView(R.id.fab).show()
        // }
    }

    override fun onStart() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStart()
    }

    override fun onResume() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume()
    }

    override fun onPause() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onPause()
    }

    override fun onStop() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStop()
    }

    override fun onRestart() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onRestart()
    }

    override fun onDestroy() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroy()
    }

    // override fun onCreateOptionsMenu(menu: Menu): Boolean {
    //     // Inflate the menu; this adds items to the action bar if it is present.
    //     menuInflater.inflate(R.menu.menu_main, menu)
    //     return true
    // }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                openSettings()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun openSettings() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.navigate(R.id.action_home_to_setting)
    }
}
