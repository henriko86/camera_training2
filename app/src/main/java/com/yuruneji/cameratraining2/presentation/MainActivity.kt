package com.yuruneji.cameratraining2.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yuruneji.cameratraining2.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // トップレベルに記述する必要がある
    // private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)

        // enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // val value: Flow<String> = dataStore
        //     .data
        //     .map { preferences ->
        //         preferences[stringPreferencesKey("name")] ?: "not set"
        //     }
        // Timber.i("hoge=${value.toString()}")


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
}
