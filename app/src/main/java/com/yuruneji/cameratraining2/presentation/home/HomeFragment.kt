package com.yuruneji.cameratraining2.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnCamerax.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_camera)
        }

        binding.btnSetting.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_setting)
        }

        binding.btnLogView.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_log_view)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStart()
    }

    override fun onResume() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume()
    }

    override fun onPause() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onPause()
    }

    override fun onStop() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }

}
