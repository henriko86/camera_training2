package com.yuruneji.camera_training2.presentation.setting.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuruneji.camera_training2.R
import com.yuruneji.camera_training2.databinding.FragmentSettingHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SettingHomeFragment : Fragment() {

    private var _binding: FragmentSettingHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentSettingHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)


        // カメラ設定
        binding.btnCamera.setOnClickListener {
            findNavController().navigate(R.id.action_SettingHomeFragment_to_SettingCameraFragment)
        }

        // 表示設定
        binding.btnDisplay.setOnClickListener {
            findNavController().navigate(R.id.action_SettingHomeFragment_to_SettingDisplayFragment)
        }

        // 音設定
        binding.btnSound.setOnClickListener {
            findNavController().navigate(R.id.action_SettingHomeFragment_to_SettingSoundFragment)
        }

        // ログ表示
        binding.btnLogView.setOnClickListener {
            findNavController().navigate(R.id.action_SettingHomeFragment_to_LogViewFragment)
        }

        viewModel.hoge()

    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }
}
