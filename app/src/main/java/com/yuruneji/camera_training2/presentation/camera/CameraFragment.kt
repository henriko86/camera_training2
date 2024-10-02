package com.yuruneji.camera_training2.presentation.camera

import android.Manifest
import android.app.ProgressDialog.show
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.snapshots.Snapshot.Companion.observe
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yuruneji.camera_training2.R
import com.yuruneji.camera_training2.databinding.FragmentCameraBinding
import com.yuruneji.camera_training2.domain.usecase.LocationSensor
import com.yuruneji.camera_training2.presentation.camera.state.AuthStateEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class CameraFragment : Fragment() {

    companion object {
        /** 権限 */
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


    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)

        // 位置情報
        viewModel.initLocationSensor(requireActivity(), this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated()")

        setupViewEvent()
        observeAuthStateChanges()
        observeDeviceInfoChanges()
    }

    override fun onResume() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume()
        Timber.d("onResume()")

        // フルスクリーン
        toggleFullScreen(true)

        // リスナーをセットアップ
        setupListeners()
    }

    override fun onPause() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onPause()

        // リスナーを解除
        stopListeners()

        // フルスクリーン
        toggleFullScreen(false)
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }

    private fun setupViewEvent() {
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
    }

    private fun observeAuthStateChanges() {
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
    }

    private fun observeDeviceInfoChanges() {

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
        viewModel.location.observe(viewLifecycleOwner) {
            Timber.i("location: ${it.latitude}, ${it.longitude}")
            binding.text2.text = "緯度:${it.latitude},経度:${it.longitude} (${getTimeStr()})"
        }

        // 時間
        viewModel.timeCheck.observe(viewLifecycleOwner) {
            Timber.i("timeCheck: $it")
            binding.text3.text = "時刻チェック ${it} (${getTimeStr()})"
        }

        // IPアドレス
        viewModel.ipAddress.observe(viewLifecycleOwner) {
            Timber.i("ipAddress: $it")
            binding.text4.text = "IPアドレス ${it} (${getTimeStr()})"
        }
    }

    private fun setupListeners() {
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
        viewModel.startLocationSensor(60_000)

        // 時間チェック
        viewModel.startTimeCheck(300_000)

        // IPアドレス
        // viewModel.getIpAddress(requireContext())
    }

    private fun stopListeners() {
        // カメラ
        stopCamera()

        // Webサーバを停止
        viewModel.stopWebServer()

        // ネットワーク状態
        viewModel.stopNetworkSensor()

        // 位置情報
        viewModel.stopLocationSensor()

        // 時間
        viewModel.stopTimeCheck()
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

    /**
     * 時間文字列を取得
     */
    private fun getTimeStr(): String {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now())
    }

    /**
     * フルスクリーン
     * @param isFullScreen フルスクリーン表示
     */
    private fun toggleFullScreen(isFullScreen: Boolean) {
        if (isFullScreen) {
            lifecycleScope.launch {
                activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    activity?.window?.insetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    activity?.window?.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                } else {
                    val flags =
                        View.SYSTEM_UI_FLAG_LOW_PROFILE or
                                View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    activity?.window?.decorView?.systemUiVisibility = flags
                }

                (activity as? AppCompatActivity)?.supportActionBar?.hide()
            }
        } else {
            lifecycleScope.launch {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    activity?.window?.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                } else {
                    activity?.window?.decorView?.systemUiVisibility = 0
                }

                (activity as? AppCompatActivity)?.supportActionBar?.show()
            }
        }
    }
}
