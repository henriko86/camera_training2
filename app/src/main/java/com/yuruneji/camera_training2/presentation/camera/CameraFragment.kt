package com.yuruneji.camera_training2.presentation.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuruneji.camera_training2.R
import com.yuruneji.camera_training2.common.CommonUtil
import com.yuruneji.camera_training2.databinding.FragmentCameraBinding
import com.yuruneji.camera_training2.domain.usecase.LocationSensor
import com.yuruneji.camera_training2.presentation.camera.state.AuthStateEnum
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CameraViewModel by viewModels()

    private val hideHandler = Handler(Looper.myLooper()!!)


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

    // @Suppress("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        val flags =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        activity?.window?.decorView?.systemUiVisibility = flags
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated()")


        // 設定画面表示
        binding.root.setOnLongClickListener {
            findNavController().navigate(R.id.action_CameraFragment_to_SettingSignInFragment)
            true
        }

        // カード認証したてい
        binding.cardAuthBtn.setOnClickListener {
            val cardNo = "X1234567890"
            viewModel.startCardFaceAuth()
        }

        viewModel.cardFaceAuthState.observe(viewLifecycleOwner) { state ->
            state?.let {
                when (state) {
                    AuthStateEnum.LOADING -> {
                        Timber.d("カード&顔認証 読み込み中.....")
                    }

                    AuthStateEnum.SUCCESS -> {
                        Timber.d("カード&顔認証 成功")
                    }

                    AuthStateEnum.FAIL -> {
                        Timber.d("カード&顔認証 失敗")
                    }
                }
            }
        }

        viewModel.cardAuthState.observe(viewLifecycleOwner) { state ->
            state?.let {
                when (state) {
                    AuthStateEnum.LOADING -> {
                        Timber.d("カード認証 読み込み中.....")
                    }

                    AuthStateEnum.SUCCESS -> {
                        Timber.d("カード認証 成功")
                    }

                    AuthStateEnum.FAIL -> {
                        Timber.d("カード認証 失敗")
                    }
                }
            }
        }

        viewModel.faceAuthState.observe(viewLifecycleOwner) { state ->
            state?.let {
                when (state) {
                    AuthStateEnum.LOADING -> {
                        Timber.d("顔認証 読み込み中.....")
                    }

                    AuthStateEnum.SUCCESS -> {
                        Timber.d("顔認証 成功")
                    }

                    AuthStateEnum.FAIL -> {
                        Timber.d("顔認証 失敗")
                    }
                }
            }
        }


        // ネットワーク状態
        viewModel.networkState.observe(viewLifecycleOwner) {
            Timber.d("networkState: $it ${Thread.currentThread().name}")
            when (it) {
                true -> {
                    binding.text1.text = "ネットワーク状態: オンライン (${getTimeStr()})"
                    // IPアドレス
                    viewModel.getIpAddress(requireContext())
                }

                false -> {
                    binding.text1.text = "ネットワーク状態: オフライン (${getTimeStr()})"
                    binding.text4.text = ""
                }
            }
        }

        // 位置情報
        locationSensor.location.observe(viewLifecycleOwner) {
            Timber.d("location: $it ${Thread.currentThread().name}")
            binding.text2.text = "緯度:${it.latitude},経度:${it.longitude} (${getTimeStr()})"
        }

        // 時間
        viewModel.timeCheck.observe(viewLifecycleOwner) {
            Timber.d("timeCheck: $it ${Thread.currentThread().name}")
            binding.text3.text = "時刻チェック ${it} (${getTimeStr()})"
        }

        // IPアドレス
        viewModel.ipAddress.observe(viewLifecycleOwner) {
            Timber.d("ipAddress: $it ${Thread.currentThread().name}")
            binding.text4.text = "IPアドレス ${it} (${getTimeStr()})"
        }
    }

    override fun onResume() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume()
        Timber.d("onResume()")


        // フルスクリーン
        show()

        // カメラ
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            permissionRequest.launch(REQUIRED_PERMISSIONS)
        }


        // ログアップロード
        // val today = LocalDateTime.now()
        // val fileName =
        //     "${today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}.log"
        // val logFile = File(requireContext().filesDir, fileName)
        // viewModel.logUpload(fileName, logFile)

        // Webサーバを起動
        viewModel.startWebServer()

        // ネットワーク状態
        viewModel.startNetworkSensor(10_000)

        // 位置情報
        locationSensor.start(60_000)

        // 時間チェック
        viewModel.startTimeCheck(300_000)

        // IPアドレス
        // viewModel.getIpAddress(requireContext())
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


        // フルスクリーン
        hide()
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }

    /**
     * 権限の確認
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * カメラの開始
     */
    private fun startCamera() {
        Timber.i(Throwable().stackTrace[0].methodName)
        viewModel.startCamera(requireContext(), this, binding.previewView, binding.surfaceView)
    }

    /**
     * カメラの停止
     */
    private fun stopCamera() {
        Timber.i(Throwable().stackTrace[0].methodName)
        viewModel.stopCamera()
    }

    private fun getTimeStr(): String {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now())
    }

    private fun show() {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        hideHandler.postDelayed(hidePart2Runnable, 100)
    }

    private fun hide() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        activity?.window?.decorView?.systemUiVisibility = 0
        hideHandler.removeCallbacks(hidePart2Runnable)
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    companion object {
        /** 権限 */
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
        ).toTypedArray()
    }
}
