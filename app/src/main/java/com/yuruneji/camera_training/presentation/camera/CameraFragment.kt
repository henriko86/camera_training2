package com.yuruneji.camera_training.presentation.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuruneji.camera_training.R
import com.yuruneji.camera_training.common.CommonUtil.getTimeStr
import com.yuruneji.camera_training.data.local.preference.CameraPreferences
import com.yuruneji.camera_training.data.local.preference.convertModel
import com.yuruneji.camera_training.databinding.FragmentCameraBinding
import com.yuruneji.camera_training.domain.model.AppRequestModel
import com.yuruneji.camera_training.domain.model.AppResponseModel
import com.yuruneji.camera_training.domain.model.CameraSettingModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

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

    @Inject
    lateinit var cameraPref: CameraPreferences

    private lateinit var cameraSettingModel: CameraSettingModel


    /** 権限リクエスト */
    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(activity, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)

        // 位置情報
        viewModel.initLocation(requireActivity(), this)
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

        cameraSettingModel = cameraPref.convertModel()

        updateCameraSettingView(cameraSettingModel)
        setupViewEvent()
        observeAuthStateChanges()
        observeDeviceInfoChanges()
    }

    override fun onResume() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume()
        Timber.d("onResume()")

        // フルスクリーン
        // toggleFullScreen(true)

        // リスナーをセットアップ
        setupListeners()
    }

    override fun onPause() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onPause()

        // リスナーを解除
        stopListeners()

        // フルスクリーン
        // toggleFullScreen(false)
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }

    private fun setupViewEvent() {
        // 設定画面表示
        // binding.root.setOnLongClickListener {
        //     findNavController().navigate(R.id.action_CameraFragment_to_SettingSignInFragment)
        //     true
        // }

        // カード認証したてい
        // binding.cardAuthBtn.setOnClickListener {
        //     // val cardNo = "X1234567890"
        //     viewModel.startCardFaceAuth()
        // }

        // 設定
        binding.settingBtn.setOnClickListener {
            findNavController().navigate(R.id.action_CameraFragment_to_SettingFragment)
        }

        // ログ
        binding.logBtn.setOnClickListener {
            findNavController().navigate(R.id.action_CameraFragment_to_LogViewFragment)
        }

    }

    private fun observeAuthStateChanges() {

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

        // 位置情報
        viewModel.location.observe(viewLifecycleOwner) {
            Timber.i("location: ${it.latitude}, ${it.longitude}")
            binding.text2.text = getString(R.string.debug_location, it.latitude.toString(), it.longitude.toString(), getTimeStr())
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

        // Webサーバを起動
        viewModel.startWebServer()

        // ネットワーク状態
        viewModel.startNetworkSensor()

        // 位置情報
        viewModel.startLocation()

        // 時間チェック
        viewModel.startTimeCheck()

        // IPアドレス
        // viewModel.getIpAddress(requireContext())
    }

    private fun stopListeners() {
        // カメラ
        stopCamera()

        // ログアップロード
        viewModel.stopLogUpload()

        // Webサーバを停止
        viewModel.stopWebServer()

        // ネットワーク状態
        viewModel.stopNetworkSensor()

        // 位置情報
        viewModel.stopLocation()

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

        // プレビューが開始
        binding.previewView.previewStreamState.observe(viewLifecycleOwner) { streamState ->
            streamState?.let {
                when (streamState) {
                    PreviewView.StreamState.STREAMING -> setupPreviewView()
                    PreviewView.StreamState.IDLE -> {}
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

    /**
     * プレビュー表示のセットアップ
     */
    private fun setupPreviewView() {
        val matrix = binding.previewView.matrix
        val width = binding.previewView.width
        val height = binding.previewView.height
        viewModel.initDrawFaceView(requireContext(), binding.svFace, matrix, width, height)
        viewModel.initDrawQrView(requireContext(), binding.svQr, matrix, width, height)
    }

    @UiThread
    fun updateCameraSettingView(cameraSettingState: CameraSettingModel) {
        Timber.d("updateCameraSettingView() $cameraSettingState")


        // 認証結果
        binding.resultName.visibility = View.GONE
        binding.resultMessage.visibility = View.GONE


        // 顔認証
        if (cameraSettingState.faceAuth) {
            binding.iconFaceAuthState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_person_24_on))
            binding.iconFaceAuthState.visibility = View.VISIBLE
        } else {
            binding.iconFaceAuthState.visibility = View.GONE
        }

        // カード認証
        if (cameraSettingState.cardAuth) {
            binding.iconCardAuthState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_credit_card_24_on))
            binding.iconCardAuthState.visibility = View.VISIBLE
        } else {
            binding.iconCardAuthState.visibility = View.GONE
        }

        // QRコード認証
        if (cameraSettingState.qrAuth) {
            binding.iconQrAuthState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_qr_code_24_on))
            binding.iconQrAuthState.visibility = View.VISIBLE
        } else {
            binding.iconQrAuthState.visibility = View.GONE
        }
    }

    @UiThread
    fun updateAuthResultView(req: AppRequestModel?, resp: AppResponseModel?, error: Throwable?) {
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
    fun updateNetworkState(state: Boolean) {
        if (state) {
            binding.iconNetworkState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_wifi_24_on))
        } else {
            binding.iconNetworkState.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_wifi_24_off))
        }
    }


    // /**
    //  * フルスクリーン
    //  * @param isFullScreen フルスクリーン表示
    //  */
    // private fun toggleFullScreen(isFullScreen: Boolean) {
    //     if (isFullScreen) {
    //         lifecycleScope.launch {
    //             activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    //
    //             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    //                 activity?.window?.insetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
    //                 activity?.window?.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    //             } else {
    //                 val flags =
    //                     View.SYSTEM_UI_FLAG_LOW_PROFILE or
    //                             View.SYSTEM_UI_FLAG_FULLSCREEN or
    //                             View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
    //                             View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
    //                             View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
    //                             View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    //                 activity?.window?.decorView?.systemUiVisibility = flags
    //             }
    //
    //             (activity as? AppCompatActivity)?.supportActionBar?.hide()
    //         }
    //     } else {
    //         lifecycleScope.launch {
    //             activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    //
    //             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    //                 activity?.window?.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
    //             } else {
    //                 activity?.window?.decorView?.systemUiVisibility = 0
    //             }
    //
    //             (activity as? AppCompatActivity)?.supportActionBar?.show()
    //         }
    //     }
    // }
}
