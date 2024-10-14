package com.yuruneji.camera_training.presentation.camera

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.yuruneji.camera_training.R
import com.yuruneji.camera_training.common.AuthMethod
import com.yuruneji.camera_training.common.CommonUtil
import com.yuruneji.camera_training.common.CommonUtil.getTimeStr
import com.yuruneji.camera_training.common.MultiAuthType
import com.yuruneji.camera_training.common.service.LocationService
import com.yuruneji.camera_training.common.service.SoundService
import com.yuruneji.camera_training.data.local.preference.AppPreferences
import com.yuruneji.camera_training.data.local.preference.convert
import com.yuruneji.camera_training.databinding.FragmentCameraBinding
import com.yuruneji.camera_training.domain.model.AppRequestModel
import com.yuruneji.camera_training.domain.model.AppResponseModel
import com.yuruneji.camera_training.data.local.preference.AppSettingModel
import com.yuruneji.camera_training.domain.usecase.TestWebServerService
import com.yuruneji.camera_training.presentation.camera.view.DrawRectView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

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

    /** 顔枠表示 */
    private lateinit var drawFaceView: DrawRectView

    /** QR枠表示 */
    private lateinit var drawQrView: DrawRectView

    /** 設定 */
    private var setting: AppSettingModel = AppSettingModel()

    /** 権限リクエスト */
    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            Toast.makeText(activity, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)

        // 効果音
        val soundService = SoundService(requireContext())
        lifecycle.addObserver(soundService)
        viewModel.setSoundService(soundService)

        // 位置情報
        val locationService = LocationService(requireActivity()) {
            // viewModel.setLocation(it)
            Timber.i("location: ${it.latitude}, ${it.longitude}")

            lifecycleScope.launch {
                binding.text2.text = getString(R.string.debug_location, it.latitude.toString(), it.longitude.toString(), getTimeStr())
            }
        }
        lifecycle.addObserver(locationService)

        // Webサーバ
        val webServerService = TestWebServerService { keyA, keyB ->
            Timber.d("keyA: $keyA, keyB: $keyB")
        }
        lifecycle.addObserver(webServerService)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        val windowSize = CommonUtil.getWindowSize()
        val cameraImageSize = if (windowSize.width > windowSize.height) {
            Size(640, 480)
        } else {
            Size(480, 640)
        }

        drawFaceView = DrawRectView(requireContext())
        drawFaceView.setImageSize(cameraImageSize.width, cameraImageSize.height)
        binding.root.addView(drawFaceView)

        drawQrView = DrawRectView(requireContext())
        drawQrView.setImageSize(cameraImageSize.width, cameraImageSize.height)
        binding.root.addView(drawQrView)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated()")

        // 設定
        setting = AppPreferences(requireContext()).convert()
        Timber.d(setting.toString())

        updateCameraSettingView(setting)
        setupViewEvent()
        observeAuthStateChanges()
        observeDeviceInfoChanges()
    }

    override fun onStart() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStart()
        // フルスクリーン
        CommonUtil.fullscreenFragment(requireActivity(), true)
    }

    override fun onResume() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume()
        Timber.d("onResume()")

        // リスナーをセットアップ
        setupListeners()
    }

    override fun onPause() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onPause()

        // リスナーを解除
        stopListeners()
    }

    override fun onStop() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStop()
        // フルスクリーン
        CommonUtil.fullscreenFragment(requireActivity(), false)
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }

    private fun setupViewEvent() {
        // 設定画面表示
        binding.root.setOnLongClickListener {
            findNavController().navigate(R.id.action_CameraFragment_to_SettingFragment)
            true
        }
    }

    private fun observeAuthStateChanges() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.drawFaceView.collect { list ->
                    drawFaceView.draw(list)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.drawQrView.collect { list ->
                    drawQrView.draw(list)
                }
            }
        }


        // 認証待ちプログレスバー
        viewModel.authWaitProgressbar.observe(viewLifecycleOwner) {
            if (it) {
                binding.authWaitProgressbar.visibility = View.VISIBLE
            } else {
                binding.authWaitProgressbar.visibility = View.INVISIBLE
            }
        }

        // 顔認証
        viewModel.faceAuthState.observe(viewLifecycleOwner) { state ->
            state?.let {
                if (state.isLoading) {
                    Timber.d("顔認証 読み込み中.....")
                } else {
                    updateAuthResultView(state.req, state.resp, state.error)
                    viewModel.setAuthResultView()
                }
            }
        }

        // カード認証
        viewModel.cardAuthState.observe(viewLifecycleOwner) { state ->
            state?.let {
                if (state.isLoading) {
                    Timber.d("カード認証 読み込み中.....")
                } else {
                    updateAuthResultView(state.req, state.resp, state.error)
                    viewModel.setAuthResultView()
                }
            }
        }
    }

    private fun observeDeviceInfoChanges() {

        // 認証結果表示
        viewModel.authResultView.observe(viewLifecycleOwner) {
            if (it) {
                binding.resultName.visibility = View.VISIBLE
                binding.resultMessage.visibility = View.VISIBLE
            } else {
                binding.resultName.visibility = View.GONE
                binding.resultMessage.visibility = View.GONE
            }
        }


        // ネットワーク状態
        viewModel.networkState.observe(viewLifecycleOwner) {
            Timber.d("networkState: $it ${Thread.currentThread().name}")
            when (it) {
                true -> {
                    binding.text1.text = getString(R.string.debug_network_online, getTimeStr())
                    // IPアドレス
                    viewModel.getIpAddress()

                    updateNetworkState(it)
                }

                false -> {
                    binding.text1.text = getString(R.string.debug_network_offline, getTimeStr())
                    binding.text4.text = ""

                    updateNetworkState(it)
                }
            }
        }

        // 時間
        viewModel.timeCheck.observe(viewLifecycleOwner) {
            Timber.i("timeCheck: $it")
            binding.text3.text = getString(R.string.debug_time_check, it.toString(), getTimeStr())
        }

        // IPアドレス
        viewModel.ipAddress.observe(viewLifecycleOwner) {
            Timber.i("IP Address: $it")
            binding.text4.text = getString(R.string.debug_ip_address, it, getTimeStr())
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
        viewModel.startLogUpload(requireContext())

        // 時刻チェック
        viewModel.startTimeCheck()

        // ネットワーク状態
        viewModel.startNetworkSensor()
    }

    private fun stopListeners() {
        // カメラ
        stopCamera()
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

        val cameraManager = requireContext().getSystemService(android.content.Context.CAMERA_SERVICE) as android.hardware.camera2.CameraManager
        val cameraID = CommonUtil.getCameraID()

        val sizeList = cameraManager.getCameraCharacteristics(cameraID.front)
            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            ?.getOutputSizes(android.graphics.ImageFormat.JPEG)
        sizeList?.forEach {
            Timber.i("size: ${it.width}x${it.height}")
        }

        // プレビューが開始
        // binding.previewView.previewStreamState.observe(viewLifecycleOwner) { streamState ->
        //     streamState?.let {
        //         when (streamState) {
        //             PreviewView.StreamState.STREAMING -> {
        //                 val matrix = binding.previewView.matrix
        //                 val width = binding.previewView.width
        //                 val height = binding.previewView.height
        //
        //                 // drawFaceView.setPreviewInfo(matrix, width, height)
        //                 // drawFaceView.setImageFlipped(true)
        //
        //                 // drawQrView.setPreviewInfo(matrix, width, height)
        //                 // drawQrView.setImageFlipped(true)
        //             }
        //
        //             PreviewView.StreamState.IDLE -> {}
        //         }
        //     }
        // }


        viewModel.startCamera(requireContext(), viewLifecycleOwner, binding.previewView)
    }

    /**
     * カメラの停止
     */
    private fun stopCamera() {
        Timber.i(Throwable().stackTrace[0].methodName)
        viewModel.stopCamera()
    }

    @UiThread
    private fun updateCameraSettingView(setting: AppSettingModel) {
        Timber.d("updateCameraSettingView() $setting")

        // 状態
        binding.statusMessage.text = ""

        // 認証結果
        binding.resultName.visibility = View.GONE
        binding.resultMessage.visibility = View.GONE


        val singleAuthFlag = setting.authMethod == AuthMethod.SINGLE.no
        val isMultiCardFaceAuth = setting.multiAuthType == MultiAuthType.CARD_FACE.no
        val isMultiQrFaceAuth = setting.multiAuthType == MultiAuthType.QR_FACE.no

        if (singleAuthFlag) {
            // 顔認証
            if (setting.faceAuth) {
                binding.iconFaceAuthState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_person_24_on))
                binding.iconFaceAuthState.visibility = View.VISIBLE
            } else {
                binding.iconFaceAuthState.visibility = View.GONE
            }

            // カード認証
            if (setting.cardAuth) {
                binding.iconCardAuthState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_credit_card_24_on))
                binding.iconCardAuthState.visibility = View.VISIBLE
            } else {
                binding.iconCardAuthState.visibility = View.GONE
            }

            // QRコード認証
            if (setting.qrAuth) {
                binding.iconQrAuthState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_qr_code_24_on))
                binding.iconQrAuthState.visibility = View.VISIBLE
            } else {
                binding.iconQrAuthState.visibility = View.GONE
            }
        } else {
            if (isMultiCardFaceAuth) {
                binding.statusMessage.text = "①カード認証"

                binding.iconFaceAuthState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_person_24_on))
                binding.iconFaceAuthState.visibility = View.VISIBLE

                binding.iconCardAuthState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_credit_card_24_on))
                binding.iconCardAuthState.visibility = View.VISIBLE

                binding.iconQrAuthState.visibility = View.GONE
            }

            if (isMultiQrFaceAuth) {
                binding.statusMessage.text = "①QRコード認証"

                binding.iconFaceAuthState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_person_24_on))
                binding.iconFaceAuthState.visibility = View.VISIBLE

                binding.iconCardAuthState.visibility = View.GONE

                binding.iconQrAuthState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_qr_code_24_on))
                binding.iconQrAuthState.visibility = View.VISIBLE
            }

        }

    }

    @UiThread
    private fun updateAuthResultView(req: AppRequestModel?, resp: AppResponseModel?, error: Throwable?) {
        if (error != null) {
            binding.resultName.text = "Error"
            binding.resultMessage.text = error.message
        }
        if (resp != null) {
            binding.resultName.text = resp.name
            binding.resultMessage.text = "認証成功"
        }
    }

    @UiThread
    private fun updateNetworkState(state: Boolean) {
        if (state) {
            binding.iconNetworkState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_wifi_24_on))
        } else {
            binding.iconNetworkState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_wifi_24_off))
        }
    }
}
