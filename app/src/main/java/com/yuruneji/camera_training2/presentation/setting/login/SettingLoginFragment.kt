package com.yuruneji.camera_training2.presentation.setting.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuruneji.camera_training2.R
import com.yuruneji.camera_training2.databinding.FragmentSettingLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SettingLoginFragment : Fragment() {

    private var _binding: FragmentSettingLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingLoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentSettingLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_SettingSignInFragment_to_CameraFragment)
        }

        binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_SettingSignInFragment_to_SettingHomeFragment)
        }
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }
}
