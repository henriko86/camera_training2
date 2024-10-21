package com.yuruneji.camera_training.presentation.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.yuruneji.camera_training.R
import com.yuruneji.camera_training.common.AuthMethod
import com.yuruneji.camera_training.common.CommonUtils
import com.yuruneji.camera_training.common.CommonUtils.fullscreen
import com.yuruneji.camera_training.common.LensFacing
import com.yuruneji.camera_training.common.MultiAuthState
import com.yuruneji.camera_training.common.MultiAuthType
import com.yuruneji.camera_training.common.service.KtorWebServer
import com.yuruneji.camera_training.common.service.KtorWebServerObserver
import com.yuruneji.camera_training.common.service.LocationObserver
import com.yuruneji.camera_training.common.service.NanoTestWebServerObserver
import com.yuruneji.camera_training.common.service.SoundObserver
import com.yuruneji.camera_training.data.local.setting.AppPreferences
import com.yuruneji.camera_training.data.local.setting.AppSettingModel
import com.yuruneji.camera_training.data.local.setting.convert
import com.yuruneji.camera_training.databinding.FragmentCameraBinding
import com.yuruneji.camera_training.domain.model.AppRequestModel
import com.yuruneji.camera_training.domain.model.AppResponseModel
import com.yuruneji.camera_training.presentation.camera.view.DrawRectView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class CameraFragment : Fragment(), KtorWebServer.Callback {

    companion object {
        /** 権限 */
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
        ).toTypedArray()
    }

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CameraViewModel by viewModels()

    /** 効果音 */
    private lateinit var soundObserver: SoundObserver

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
        soundObserver = SoundObserver(requireContext())
        lifecycle.addObserver(soundObserver)

        // 位置情報
        val locationObserver = LocationObserver(requireActivity()) {
            viewModel.setLocation(it)
        }
        lifecycle.addObserver(locationObserver)

        // Webサーバ
        val webServerObserver = NanoTestWebServerObserver { keyA, keyB ->
            Timber.d("keyA: $keyA, keyB: $keyB")
        }
        lifecycle.addObserver(webServerObserver)

        // ktor Webサーバ
        val ktorWebServerObserver = KtorWebServerObserver(8000, this)
        lifecycle.addObserver(ktorWebServerObserver)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)


        // 設定
        setting = AppPreferences(requireContext()).convert()
        Timber.d(setting.toString())

        val cameraImageSize = CommonUtils.getCameraImageSize(requireContext())
        val isFlipped = setting.lensFacing == LensFacing.FRONT

        drawFaceView = DrawRectView(requireContext(), cameraImageSize.width, cameraImageSize.height, isFlipped)
        binding.root.addView(drawFaceView)

        drawQrView = DrawRectView(requireContext(), cameraImageSize.width, cameraImageSize.height, isFlipped)
        binding.root.addView(drawQrView)


        updateCameraSettingView(setting)
        setupViewEvent()
        observeAuthStateChanges()
        observeDeviceInfoChanges()
    }

    override fun onResume() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume()

        // フルスクリーン
        fullscreen(requireActivity(), true)

        // リスナーをセットアップ
        setupListeners()
    }

    override fun onPause() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onPause()

        // リスナーを解除
        stopListeners()
        // フルスクリーン
        fullscreen(requireActivity(), false)
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }

    override fun onKtorReceive(ids: List<String>) {
        Timber.d("ktor受信:$ids")

        lifecycleScope.launch {
            Toast.makeText(requireContext(), ids.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onKtorFailure(t: Throwable) {
        Timber.e(t, "ktor Webサーバーエラー")
    }

    private fun setupViewEvent() {
        // ログイン画面表示
        binding.root.setOnLongClickListener {
            findNavController().navigate(R.id.action_CameraFragment_to_LoginFragment)
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

        viewModel.sound.observe(viewLifecycleOwner) {
            soundObserver.play(it)
        }

        // 認証待ちプログレスバー
        viewModel.authWaitProgressbar.observe(viewLifecycleOwner) {
            binding.authWaitProgressbar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }

        // 顔認証
        viewModel.faceAuthState.observe(viewLifecycleOwner) { state ->
            state?.let {
                if (state.isLoading) {
                    Timber.d("顔認証 読み込み中.....")
                } else {
                    if (setting.authMethod == AuthMethod.SINGLE) {
                        updateAuthResultView(state.request, state.response, state.error)
                        viewModel.setAuthResultView()
                    } else {
                        viewModel.updateBeforeMultiAuthState(MultiAuthState.Before)

                        updateAuthResultView(state.request, state.response, state.error)
                        viewModel.setAuthResultView()
                    }
                }
            }
        }

        // カード認証
        viewModel.cardAuthState.observe(viewLifecycleOwner) { state ->
            state?.let {
                if (state.isLoading) {
                    Timber.d("カード認証 読み込み中.....")
                } else {
                    if (setting.authMethod == AuthMethod.SINGLE) {
                        updateAuthResultView(state.request, state.response, state.error)
                        viewModel.setAuthResultView()
                    } else {
                        viewModel.updateBeforeMultiAuthState(MultiAuthState.After)
                    }
                }
            }
        }

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
    }

    private fun observeDeviceInfoChanges() {
        // ネットワーク状態
        viewModel.networkState.observe(viewLifecycleOwner) {
            Timber.d("networkState: $it ${Thread.currentThread().name}")
            when (it) {
                true -> {
                    binding.text1.text = getString(R.string.debug_network_online, getLogTime())
                    // IPアドレス
                    viewModel.getIpAddress()

                    updateNetworkState(it)
                }

                false -> {
                    binding.text1.text = getString(R.string.debug_network_offline, getLogTime())
                    binding.text4.text = ""

                    updateNetworkState(it)
                }
            }
        }

        // 時間
        viewModel.timeOff.observe(viewLifecycleOwner) {
            Timber.i("timeCheck: $it")
            binding.text3.text = getString(R.string.debug_time_off, it.toString(), getLogTime())
        }

        // IPアドレス
        viewModel.ipAddress.observe(viewLifecycleOwner) {
            Timber.i("IP Address: $it")
            binding.text4.text = getString(R.string.debug_ip_address, it, getLogTime())
        }

        // 位置情報
        viewModel.location.observe(viewLifecycleOwner) {
            binding.text2.text = getString(R.string.debug_location, it.latitude.toString(), it.longitude.toString(), getLogTime())
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
        viewModel.startCheckTimeOff()

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

        val orientation = CommonUtils.getOrientation(requireContext())
        when (orientation) {
            android.content.res.Configuration.ORIENTATION_PORTRAIT -> {
                println("縦")
            }

            android.content.res.Configuration.ORIENTATION_LANDSCAPE -> {
                println("横")
            }
        }

        val rotation = CommonUtils.getRotation(requireContext())
        when (rotation) {
            Surface.ROTATION_0 -> {
                println("0")
            }

            Surface.ROTATION_90 -> {
                println("90")
            }

            Surface.ROTATION_180 -> {
                println("180")
            }

            Surface.ROTATION_270 -> {
                println("270")
            }
        }

        // プレビューが開始
        binding.previewView.previewStreamState.observe(viewLifecycleOwner) { streamState ->
            streamState?.let {
                when (streamState) {
                    PreviewView.StreamState.STREAMING -> {
                        Timber.d("PreviewView.StreamState.STREAMING")
                    }

                    PreviewView.StreamState.IDLE -> {
                        Timber.d("PreviewView.StreamState.IDLE")
                    }
                }
            }
        }


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


        // 認証結果
        binding.resultName.visibility = View.GONE
        binding.resultMessage.visibility = View.GONE


        val singleAuthFlag = setting.authMethod == AuthMethod.SINGLE
        val isMultiCardFaceAuth = setting.multiAuthType == MultiAuthType.CARD_FACE
        val isMultiQrFaceAuth = setting.multiAuthType == MultiAuthType.QR_FACE

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
                binding.iconFaceAuthState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_person_24_on))
                binding.iconFaceAuthState.visibility = View.VISIBLE

                binding.iconCardAuthState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_credit_card_24_on))
                binding.iconCardAuthState.visibility = View.VISIBLE

                binding.iconQrAuthState.visibility = View.GONE
            }

            if (isMultiQrFaceAuth) {
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

    /**
     * ログ時間を取得
     * @return ログ時間
     */
    private fun getLogTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
    }
}
