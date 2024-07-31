package com.yuruneji.cameratraining2.presentation.setting.main

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.databinding.FragmentHomeBinding
import com.yuruneji.cameratraining2.databinding.FragmentSettingMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SettingMainFragment : Fragment() {

    companion object {
        // fun newInstance() = SettingMainFragment()
    }

    private var _binding: FragmentSettingMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingMainViewModel by viewModels()

    // override fun onCreate(savedInstanceState: Bundle?) {
    //     super.onCreate(savedInstanceState)
    //
    //     // TODO: Use the ViewModel
    // }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentSettingMainBinding.inflate(inflater, container, false)

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_setting_main_to_home)
        }

        return binding.root
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }
}
