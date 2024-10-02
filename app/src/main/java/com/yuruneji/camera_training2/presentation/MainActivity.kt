package com.yuruneji.camera_training2.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.yuruneji.camera_training2.R
import com.yuruneji.camera_training2.common.CipherUtil
import com.yuruneji.camera_training2.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var keyStoreUtil: CipherUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        keyStoreUtil = CipherUtil(this)
        val plainText = "ここに入力された文字列を暗号化・復号します"
        val encryptedText: String? = keyStoreUtil.encrypt(plainText)
        val decryptedText: String? = keyStoreUtil.decrypt(encryptedText ?: "")

        Timber.d("plainText    =$plainText")
        Timber.d("encryptedText=$encryptedText")
        Timber.d("decryptedText=$decryptedText")
        Timber.d("")

        // binding.fab.setOnClickListener { view ->
        //     // onFabClick(view)
        // }
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

    // override fun onFabClick(view: View) {
    //     Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
    //         .setAction("Action", null)
    //         .setAnchorView(R.id.fab).show()
    // }
    //
    // interface FabClickCallback {
    //     fun onFabClick(view: View)
    // }
}
