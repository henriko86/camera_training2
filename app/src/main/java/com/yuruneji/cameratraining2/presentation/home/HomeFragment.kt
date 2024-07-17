package com.yuruneji.cameratraining2.presentation.home

import android.Manifest
import android.content.Context
import android.content.Context.DISPLAY_SERVICE
import android.content.Context.WINDOW_SERVICE
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.graphics.translationMatrix
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.databinding.FragmentHomeBinding
import com.yuruneji.cameratraining2.domain.usecase.FaceAnalyzer
import com.yuruneji.cameratraining2.presentation.home.view.DrawFaceView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.Executors

@AndroidEntryPoint
class HomeFragment : Fragment() {
    companion object {
        fun newInstance() = HomeFragment()

        // private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
            // Manifest.permission.ACCESS_COARSE_LOCATION,
            // Manifest.permission.ACCESS_FINE_LOCATION,
            // Manifest.permission.ACCESS_FINE_LOCATION,
            // Manifest.permission.WRITE_EXTERNAL_STORAGE,
            // Manifest.permission.READ_EXTERNAL_STORAGE
        ).toTypedArray()
    }

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // private lateinit var previewView: PreviewView
    // private lateinit var surfaceView: SurfaceView

    // /** 顔枠表示 */
    // private var drawFaceView: DrawFaceView? = null

    // /** カメラExecutor */
    // private var cameraExecutor = Executors.newSingleThreadExecutor()

    // private var camera: Camera? = null
    // private lateinit var faceAnalyzer: FaceAnalyzer

    // private val cameraManager: CameraManager by lazy {
    //     requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
    // }
    // private val displayManager: DisplayManager by lazy {
    //     requireContext().getSystemService(DISPLAY_SERVICE) as DisplayManager
    // }
    // private val windowManager: WindowManager by lazy {
    //     requireContext().getSystemService(WINDOW_SERVICE) as WindowManager
    // }
    //
    // /** 権限リクエスト */
    // private val permissionRequest =
    //     registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
    //         if (allPermissionsGranted()) {
    //             startCamera()
    //         } else {
    //             Toast.makeText(
    //                 activity, "Permissions not granted by the user.", Toast.LENGTH_SHORT
    //             ).show()
    //         }
    //     }
    //
    // private val surfaceHolderCallback = object : SurfaceHolder.Callback {
    //     override fun surfaceCreated(holder: SurfaceHolder) {
    //         Timber.i("surfaceCreated()")
    //     }
    //
    //     override fun surfaceChanged(
    //         holder: SurfaceHolder,
    //         format: Int,
    //         width: Int,
    //         height: Int
    //     ) {
    //         Timber.i("surfaceChanged()")
    //     }
    //
    //     override fun surfaceDestroyed(holder: SurfaceHolder) {
    //         Timber.i("surfaceDestroyed()")
    //     }
    // }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("onCreateView()")

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // previewView = binding.previewView
        // surfaceView = binding.surfaceView
        //
        // surfaceView.holder.addCallback(surfaceHolderCallback)
        // surfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)
        // surfaceView.setZOrderOnTop(true)
        //
        // ContextCompat.getDrawable(requireContext(), R.drawable.face_rect)?.let { drawable ->
        //
        //
        //     Timber.i("${previewView.width}, ${previewView.height}")
        //
        //     // 顔枠表示
        //     drawFaceView = DrawFaceView(
        //         // previewView = previewView,
        //         surfaceView = surfaceView,
        //         drawable = drawable
        //     )
        // }

        // val disp = windowManager.defaultDisplay
        // Timber.i("width:${disp.width}, height:${disp.height}")


        val textView: TextView = binding.textHome
        viewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // binding.previewView.setOnLongClickListener {
        //     findNavController().navigate(R.id.action_home_to_dashboard)
        //     true
        // }
        binding.btnCamerax.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_camera)
        }
        binding.btnCamera2.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_camera2)
        }
        binding.btnSetting.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_setting)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated()")

        // State
        lifecycleScope.launch {
            viewModel.state.collect { mainState ->
                var str = ""
                mainState.faceAuth?.let {
                    str += it.rect
                }

                mainState.error?.let {
                    str += it
                }

            }
        }

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                viewModel.authState.collect { authState ->
                    var str = ""

                    if (authState.isFaceAuth && authState.isCardAuth && authState.isPostProcessAuth) {
                        str += "カード認証成功\n顔認証成功\n認証後処理成功"

                        viewModel.authStatusReset()
                    } else if (authState.isFaceAuth && authState.isCardAuth) {
                        str += "カード認証成功\n顔認証成功"

                        viewModel.postProcess()
                    } else if (authState.isCardAuth) {
                        str += "カード認証成功"
                    } else {
                        //
                    }
                }
            }
        }
    }

    // override fun onStart() {
    //     super.onStart()
    //     Timber.d("onStart()")
    // }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume()")

        // if (allPermissionsGranted()) {
        //     startCamera()
        // } else {
        //     permissionRequest.launch(REQUIRED_PERMISSIONS)
        // }
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause()")

        // stopCamera()
    }

    // override fun onStop() {
    //     super.onStop()
    //     Timber.d("onStop()")
    // }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("onDestroyView()")
        _binding = null
    }

}
