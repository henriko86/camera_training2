package com.yuruneji.cameratraining2.presentation.setting.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.databinding.FragmentSettingLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SettingLoginFragment : Fragment() {

    companion object {
        // fun newInstance() = SettingLoginFragment()
    }

    private var _binding: FragmentSettingLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingLoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingLoginBinding.inflate(inflater, container, false)

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_setting_login_to_home)
        }

        binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_setting_login_to_setting_main)
        }

        return binding.root
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }
}
