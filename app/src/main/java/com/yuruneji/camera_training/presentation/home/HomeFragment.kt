package com.yuruneji.camera_training.presentation.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yuruneji.camera_training.BuildConfig
import com.yuruneji.camera_training.R
import com.yuruneji.camera_training.databinding.FragmentHomeBinding
import timber.log.Timber

class HomeFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE = 1
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)

        // カメラ
        binding.cameraBtn.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_CameraFragment)
        }

        // 設定
        binding.settingBtn.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_SettingFragment)
        }

        // ログ表示
        binding.blogViewBtn.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_LogViewFragment)
        }

        // Timber.d(resources.getString(R.string.STRING_RESOURCE))
        Timber.d("xxx=${BuildConfig.API_URL_BASE}")
        Timber.d("xxx=${BuildConfig.API_URL_DEVELOP}")
        Timber.d("xxx=${BuildConfig.API_URL_STAGING}")
        Timber.d("xxx=${BuildConfig.API_URL_PRODUCTION}")
        Timber.d("xxx=")
        Timber.d("")


        // ACTION_INTERNET_CONNECTIVITY
        binding.settingsInternetConnectivity.setOnClickListener {
            startActivityForResult(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY), REQUEST_CODE)
        }

        // ACTION_WIFI
        binding.settingsWifi.setOnClickListener {
            startActivityForResult(Intent(Settings.Panel.ACTION_WIFI), REQUEST_CODE)
        }

        // ACTION_NFC
        binding.settingsNfc.setOnClickListener {
            startActivityForResult(Intent(Settings.Panel.ACTION_NFC), REQUEST_CODE)
        }

        // ACTION_VOLUME
        binding.settingsVolume.setOnClickListener {
            startActivityForResult(Intent(Settings.Panel.ACTION_VOLUME), REQUEST_CODE)
        }
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }
}
