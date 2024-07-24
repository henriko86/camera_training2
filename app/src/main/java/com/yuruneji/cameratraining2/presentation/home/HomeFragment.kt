package com.yuruneji.cameratraining2.presentation.home

import android.Manifest
import android.content.Context
import android.content.Context.DISPLAY_SERVICE
import android.content.Context.WINDOW_SERVICE
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextClock
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
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.translationMatrix
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.common.DataProvider
import com.yuruneji.cameratraining2.databinding.FragmentHomeBinding
import com.yuruneji.cameratraining2.domain.usecase.FaceAnalyzer
import com.yuruneji.cameratraining2.presentation.home.view.DrawFaceView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Inject

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

    @Inject
    lateinit var dataProvider: DataProvider

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

    val connectivityManager by lazy {
        requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

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

    override fun onAttach(context: Context) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onAttach(context)
    }


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

        // Timber.i(dataProvider.getUserName())
        // Timber.i(dataProvider.getUserPass())
        //
        // dataProvider.setUserName("aaaaa")
        // dataProvider.setUserPass("zzzzzz")


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

        // val myView = inflater.inflate(R.layout.my_layout, null)
        // myView?.let {
        //     myView.findViewById<TextView>(R.id.text1).text = "hoge"
        //     binding.root.addView(myView)
        // }

        binding.clock.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Timber.i("${Throwable().stackTrace[0].methodName} ${getThreadName()} ${s} ${start} ${count} ${after}")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Timber.i("${Throwable().stackTrace[0].methodName} ${getThreadName()} ${s} ${start} ${before} ${count}")

                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                val connectionType = activeNetwork?.type

                if (isConnected) {
                    binding.networkStatus.text = when (connectionType) {
                        ConnectivityManager.TYPE_WIFI -> "Wifiに接続しています"
                        ConnectivityManager.TYPE_MOBILE -> "モバイル通信に接続しています"
                        else -> "その他のネットワークに接続しています"
                    }
                } else {
                    binding.networkStatus.text = "インターネットに接続していません"
                }
                // Timber.i("networkInfo:${networkInfo == null}")
            }

            override fun afterTextChanged(s: Editable?) {
                // Timber.i("${Throwable().stackTrace[0].methodName} ${getThreadName()} ${s}")
            }
        })

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
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)

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

    override fun onStart() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStart()
    }

    override fun onResume() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume()

        // if (allPermissionsGranted()) {
        //     startCamera()
        // } else {
        //     permissionRequest.launch(REQUIRED_PERMISSIONS)
        // }
    }

    override fun onPause() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onPause()

        // stopCamera()
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

    private fun getThreadName(): String {
        return Thread.currentThread().name
    }

}
