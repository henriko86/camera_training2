package com.yuruneji.camera_training.presentation.setting

import android.R
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.UiThread
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.yuruneji.camera_training.common.ApiType
import com.yuruneji.camera_training.common.AuthMethod
import com.yuruneji.camera_training.common.CommonUtil
import com.yuruneji.camera_training.common.LensFacing
import com.yuruneji.camera_training.common.MinFaceSize
import com.yuruneji.camera_training.common.MultiAuthType
import com.yuruneji.camera_training.databinding.FragmentSettingBinding
import com.yuruneji.camera_training.presentation.view.DatePickerFragment
import com.yuruneji.camera_training.presentation.view.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class SettingFragment : Fragment(), DatePickerFragment.OnSelectedDateListener, TimePickerFragment.OnSelectedTimeListener {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)

        // val toolbar: Toolbar = binding.toolbarParent.toolbar
        // toolbar.title = "設定"
        // val toolbarTitle = binding.toolbarParent.toolbarTitle
        // toolbarTitle.text = "設定"

        // val activity = requireActivity() as AppCompatActivity
        // activity.setSupportActionBar(toolbar)
        // activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        // activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // activity.supportActionBar?.show()

        CommonUtil.fullscreenToolbarFragment(requireActivity(), true)
        // PreferenceManager.getDefaultSharedPreferences(requireContext()).
        // requireContext().deleteSharedPreferences(CameraPreferences.PREF_NAME)


        // val contextView = findViewById<View>(R.id.context_view)
        binding.snackbarBtn.setOnClickListener {
            Snackbar.make(binding.root, "xxxxx", Snackbar.LENGTH_LONG)
                .setAction("Action") {
                    Timber.d("action")
                }
                .show()
        }


        // val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        // binding.listview.addItemDecoration(decoration)


        val lensFacingList = LensFacing.valueList()
        val lensFacingAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, lensFacingList)
        binding.lensFacing.setAdapter(lensFacingAdapter)
        // binding.lensFacing.additem
        binding.lensFacing.addTextChangedListener {
            viewModel.updateLensFacing(LensFacing.toNo(it.toString()))
        }


        val apiTypeList = ApiType.valueList()
        val apiTypeAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, apiTypeList)
        binding.apiType.setAdapter(apiTypeAdapter)
        binding.apiType.addTextChangedListener {
            viewModel.updateApiType(ApiType.toNo(it.toString()))
        }


        val authMethodList = AuthMethod.valueList()
        val authMethodAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, authMethodList)
        binding.authMethod.setAdapter(authMethodAdapter)
        binding.authMethod.addTextChangedListener {
            viewModel.updateAuthMethod(AuthMethod.toNo(it.toString()))
            updateAuthMethod(it.toString() == AuthMethod.MULTI.value)
        }


        val multiAuthTypeList = MultiAuthType.valueList()
        val multiAuthTypeAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, multiAuthTypeList)
        binding.multiAuthType.setAdapter(multiAuthTypeAdapter)
        binding.multiAuthType.addTextChangedListener {
            viewModel.updateMultiAuthType(MultiAuthType.toNo(it.toString()))
        }


        binding.faceAuthSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateFaceAuth(isChecked)
        }
        binding.cardAuthSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateCardAuth(isChecked)
        }
        binding.qrAuthSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateQrAuth(isChecked)
        }


        // binding.spinner1.adapter = apiTypeAdapter
        // binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        //     override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //         Timber.i("${parent?.getItemAtPosition(position)} ${position}")
        //     }
        //
        //     override fun onNothingSelected(parent: AdapterView<*>?) {
        //     }
        // }


        // 日付
        binding.date.setOnClickListener {
            val datePicker = DatePickerFragment()
            datePicker.show(childFragmentManager, "datePicker")
        }

        // 時間
        binding.time.setOnClickListener {
            val timePicker = TimePickerFragment()
            timePicker.show(childFragmentManager, "timePicker")
        }


        viewModel.lensFacing.observe(viewLifecycleOwner) {
            binding.lensFacing.setText(LensFacing.toValue(it), false)
        }
        viewModel.apiType.observe(viewLifecycleOwner) {
            binding.apiType.setText(ApiType.toValue(it), false)
        }
        viewModel.authMethod.observe(viewLifecycleOwner) {
            val flag = AuthMethod.toValue(it)
            binding.authMethod.setText(flag, false)
            updateAuthMethod(flag == AuthMethod.MULTI.value)
        }

        viewModel.multiAuthType.observe(viewLifecycleOwner) {
            binding.multiAuthType.setText(MultiAuthType.toValue(it), false)
        }

        viewModel.faceAuth.observe(viewLifecycleOwner) {
            binding.faceAuthSwitch.isChecked = it
        }
        viewModel.cardAuth.observe(viewLifecycleOwner) {
            binding.cardAuthSwitch.isChecked = it
        }
        viewModel.qrAuth.observe(viewLifecycleOwner) {
            binding.qrAuthSwitch.isChecked = it
        }


        val minFaceSizeList = MinFaceSize.valueList()
        val minFaceSizeAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, minFaceSizeList)
        binding.minFaceSize.setAdapter(minFaceSizeAdapter)
        binding.minFaceSize.addTextChangedListener {
            viewModel.updateMinFaceSize(MinFaceSize.toSize(it.toString()))
        }
        viewModel.minFaceSize.observe(viewLifecycleOwner) {
            binding.minFaceSize.setText(MinFaceSize.toValue(it), false)
        }
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }

    override fun selectedDate(year: Int, month: Int, dayOfMonth: Int) {
        lifecycleScope.launch {
            val date = LocalDate.of(year, month + 1, dayOfMonth)
            binding.date.text = Editable.Factory.getInstance()
                .newEditable(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            // viewModel.setDate(date)
        }
    }

    override fun selectedTime(hour: Int, minute: Int) {
        lifecycleScope.launch {
            val time = LocalTime.of(hour, minute)
            binding.time.text = Editable.Factory.getInstance()
                .newEditable(time.format(DateTimeFormatter.ofPattern("HH:mm")))
            // viewModel.setTime(time)
        }
    }

    /**
     * 多要素認証のレイアウト更新
     * @param flag true: 多要素認証, false: 単要素認証
     */
    @UiThread
    private fun updateAuthMethod(flag: Boolean) {
        if (flag) { // 多要素認証
            // binding.multiAuthLayout.visibility = View.VISIBLE
            // binding.singleAuthLayout.visibility = View.GONE

            binding.multiAuthTypeLayout.isEnabled = true
            binding.faceAuthSwitch.isEnabled = false
            binding.cardAuthSwitch.isEnabled = false
            binding.qrAuthSwitch.isEnabled = false
        } else { // 単要素認証
            // binding.multiAuthLayout.visibility = View.GONE
            // binding.singleAuthLayout.visibility = View.VISIBLE

            binding.multiAuthTypeLayout.isEnabled = false
            binding.faceAuthSwitch.isEnabled = true
            binding.cardAuthSwitch.isEnabled = true
            binding.qrAuthSwitch.isEnabled = true
        }
    }
}
