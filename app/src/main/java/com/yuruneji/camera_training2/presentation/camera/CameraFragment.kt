package com.yuruneji.camera_training2.presentation.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuruneji.camera_training2.R
import com.yuruneji.camera_training2.common.CommonUtil
import com.yuruneji.camera_training2.databinding.FragmentCameraBinding
import com.yuruneji.camera_training2.domain.usecase.LocationSensor
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class CameraFragment : Fragment() {

    companion object {
        fun newInstance() = CameraFragment()

        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
        ).toTypedArray()
    }

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CameraViewModel by viewModels()

    /** 権限リクエスト */
    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    activity, "Permissions not granted by the user.", Toast.LENGTH_SHORT
                ).show()
            }
        }

    // 位置情報
    private lateinit var locationSensor: LocationSensor


    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)

        // 位置情報
        locationSensor = LocationSensor(requireActivity())
        locationSensor.requestLocationPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)

        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        binding.root.setOnLongClickListener {
            findNavController().navigate(R.id.action_CameraFragment_to_SettingSignInFragment)
            true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated()")


        // ネットワーク状態
        viewModel.networkState.observe(viewLifecycleOwner) {
            Timber.d("networkState: $it ${Thread.currentThread().name}")
            when (it) {
                true -> {
                    binding.text1.text = "オンライン"
                }

                false -> {
                    binding.text1.text = "オフライン"
                }
            }
        }

        // 位置情報
        locationSensor.location.observe(viewLifecycleOwner) {
            Timber.d("location: $it ${Thread.currentThread().name}")
            binding.text2.text = "緯度:${it.latitude},経度:${it.longitude}"
        }

        // 時間
        viewModel.timeCheck.observe(viewLifecycleOwner) {
            Timber.d("timeCheck: $it ${Thread.currentThread().name}")
            binding.text3.text = "時刻チェック ${it}"
        }
    }

    override fun onResume() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume()
        Timber.d("onResume()")

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            permissionRequest.launch(REQUIRED_PERMISSIONS)
        }

        val today = LocalDateTime.now()
        val fileName =
            "${today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}.log"
        val logFile = File(requireContext().filesDir, fileName)
        viewModel.logUpload(fileName, logFile)

        // Webサーバを起動
        viewModel.startWebServer()

        // ネットワーク状態
        viewModel.startNetworkSensor(10_000)

        // 位置情報
        locationSensor.start(11_000)

        // 時間
        viewModel.startTimeCheck()


        // IPアドレス
        CommonUtil.getIpAddress(requireContext())
    }

    override fun onPause() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onPause()
        Timber.d("onPause()")
        stopCamera()

        // Webサーバを停止
        viewModel.stopWebServer()

        // ネットワーク状態
        viewModel.stopNetworkSensor()

        // 位置情報
        locationSensor.stop()

        // 時間
        viewModel.stopTimeCheck()
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        Timber.d("onDestroyView()")
        _binding = null
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        Timber.i(Throwable().stackTrace[0].methodName)
        viewModel.startCamera(requireContext(), this, binding.previewView, binding.surfaceView)
    }

    private fun stopCamera() {
        Timber.i(Throwable().stackTrace[0].methodName)
        viewModel.stopCamera()
    }
}
