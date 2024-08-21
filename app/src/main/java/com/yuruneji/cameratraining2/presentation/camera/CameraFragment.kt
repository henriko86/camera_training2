package com.yuruneji.cameratraining2.presentation.camera

import android.Manifest
import android.content.Context
import android.content.Context.DISPLAY_SERVICE
import android.content.Context.WINDOW_SERVICE
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.databinding.FragmentCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class CameraFragment : Fragment() {

    companion object {
        fun newInstance() = CameraFragment()

        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
        ).toTypedArray()
    }

    private val viewModel: CameraViewModel by viewModels()
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainHandler: Handler
    private lateinit var handler1: Handler
    private lateinit var handler2: Handler

    private val executor1: ExecutorService = Executors.newSingleThreadExecutor()
    private val executor2: ExecutorService = Executors.newFixedThreadPool(3)
    private val executor3: ExecutorService = Executors.newCachedThreadPool()

    private val isTask: AtomicBoolean = AtomicBoolean(false)

    private val cameraManager: CameraManager by lazy {
        requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    private val displayManager: DisplayManager by lazy {
        requireContext().getSystemService(DISPLAY_SERVICE) as DisplayManager
    }
    private val windowManager: WindowManager by lazy {
        requireContext().getSystemService(WINDOW_SERVICE) as WindowManager
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainHandler = Handler(requireContext().mainLooper)

        var thread = HandlerThread("hoge1")
        thread.start()
        handler1 = Handler(thread.looper)

        thread = HandlerThread("hoge2")
        thread.start()
        handler2 = Handler(thread.looper)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("onCreateView()")

        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_camera_to_home)
        }

        // binding.root.setOnLongClickListener {
        //     findNavController().navigate(R.id.action_camera_to_log_view)
        //     true
        // }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated()")
    }

    override fun onResume() {
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

    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause()")
        stopCamera()
    }

    override fun onDestroyView() {
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
        viewModel.startCamera(requireContext(), this, binding.previewView, binding.surfaceView)
    }

    private fun stopCamera() {
        viewModel.stopCamera()
    }
}
