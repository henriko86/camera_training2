package com.yuruneji.camera_training.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.yuruneji.camera_training.R
import com.yuruneji.camera_training.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    // @Inject
    // lateinit var cipherExtractor: CipherExtractor

    // public interface HomeCallback {
    //     fun onHomeState(str: String)
    // }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // navController.addOnDestinationChangedListener { _, destination, _ ->
        //     binding.toolbar.visibility = if (destination.id == R.id.CameraFragment) View.GONE else View.VISIBLE
        // }
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // val plainText = "ここに入力された文字列を暗号化・復号します"
        // val encryptedText: String? = cipherExtractor.encrypt(plainText)
        // val decryptedText: String? = cipherExtractor.decrypt(encryptedText ?: "")

        // Timber.d("plainText    =$plainText")
        // Timber.d("encryptedText=$encryptedText")
        // Timber.d("decryptedText=$decryptedText")
        // Timber.d("")
        //
        // Timber.e(Exception(), "エラーメッセージ")


        // window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // fullscreen()
    }

    override fun onStart() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStart()
    }

    override fun onResume() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume()

        viewModel.startMainJob()

        viewModel.mainState.observe(this) { state ->
            Timber.v(state.toString())
        }
    }

    override fun onPause() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onPause()

        viewModel.cancelMainJob()
    }

    override fun onStop() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStop()
    }

    override fun onDestroy() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        Timber.i(Throwable().stackTrace[0].methodName)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
