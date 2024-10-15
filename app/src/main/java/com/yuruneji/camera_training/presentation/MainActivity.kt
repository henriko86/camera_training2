package com.yuruneji.camera_training.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
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

    // @Inject
    // lateinit var cipherExtractor: CipherExtractor

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

    override fun onSupportNavigateUp(): Boolean {
        Timber.i(Throwable().stackTrace[0].methodName)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroy()
    }

    // private fun fullscreen() {
    //     lifecycleScope.launch {
    //         supportActionBar?.hide()
    //         // if (Build.VERSION.SDK_INT >= 30) {
    //         //     binding.root.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
    //         // } else {
    //             // Note that some of these constants are new as of API 16 (Jelly Bean)
    //             // and API 19 (KitKat). It is safe to use them, as they are inlined
    //             // at compile-time and do nothing on earlier devices.
    //             binding.root.systemUiVisibility =
    //                 View.SYSTEM_UI_FLAG_LOW_PROFILE or
    //                         View.SYSTEM_UI_FLAG_FULLSCREEN or
    //                         View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
    //                         View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
    //                         View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
    //                         View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    //         // }
    //     }
    // }
}
