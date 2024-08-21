package com.yuruneji.cameratraining2.presentation.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SettingFragment : Fragment() {

    companion object {
        // fun newInstance() = SettingLoginFragment()
    }

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_setting_to_home)
        }

        binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_setting_to_home)
        }

        return binding.root
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }
}
