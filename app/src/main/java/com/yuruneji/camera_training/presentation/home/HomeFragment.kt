package com.yuruneji.camera_training.presentation.home

import android.os.Bundle
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
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }
}
