package com.yuruneji.cameratraining2.presentation.camera2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.databinding.FragmentCamera2Binding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class Camera2Fragment : Fragment() {

    companion object {
        fun newInstance() = Camera2Fragment()

        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
        ).toTypedArray()
    }

    private val viewModel: Camera2ViewModel by viewModels()
    private var _binding: FragmentCamera2Binding? = null
    private val binding get() = _binding!!

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** 権限リクエスト */
    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (allPermissionsGranted()) {
                viewModel.startCamera(requireContext(), binding.previewView, binding.surfaceView)
            } else {
                Toast.makeText(
                    activity, "Permissions not granted by the user.", Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCamera2Binding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_camera2_to_home)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()) {
            viewModel.startCamera(requireContext(), binding.previewView, binding.surfaceView)
        } else {
            permissionRequest.launch(REQUIRED_PERMISSIONS)
        }
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause()")

        viewModel.stopCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    // @SuppressLint("MissingPermission")
    // private fun openCamera() = lifecycleScope.launch(Dispatchers.Main) {
    //     cameraManager.openCamera(cameraID, cameraStateCallback, cameraHandler)
    // }

}
