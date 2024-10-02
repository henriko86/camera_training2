package com.yuruneji.camera_training2.presentation.log_view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.yuruneji.camera_training2.R
import com.yuruneji.camera_training2.data.local.datastore.convert
import com.yuruneji.camera_training2.databinding.FragmentLogViewBinding
import com.yuruneji.camera_training2.presentation.log_view.state.LogPeriod
import com.yuruneji.camera_training2.presentation.log_view.state.LogViewState
import com.yuruneji.camera_training2.presentation.log_view.view.LogViewAdapter
import com.yuruneji.camera_training2.presentation.view.DatePickerFragment
import com.yuruneji.camera_training2.presentation.view.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class LogViewFragment : Fragment(), DatePickerFragment.OnSelectedDateListener, TimePickerFragment.OnSelectedTimeListener {

    private var _binding: FragmentLogViewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LogViewViewModel by viewModels()
    private val adapter: LogViewAdapter = LogViewAdapter()
    private var logViewState: LogViewState = LogViewState()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentLogViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        setupOnCheckedChangeListeners()
        observePreferenceChanges()
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.listview.addItemDecoration(decoration)
        binding.listview.adapter = adapter
    }

    private fun setupOnCheckedChangeListeners() {

        // 日付
        binding.date.setOnClickListener {
            val datePicker = DatePickerFragment()
            datePicker.show(childFragmentManager, "datePicker")
        }

        // 時間
        // binding.time.setOnClickListener {
        //     val timePicker = TimePickerFragment()
        //     timePicker.show(childFragmentManager, "timePicker")
        // }

        // 期間
        val periodList = arrayOf("1日", "半日", "6時間", "3時間", "1時間")
        val periodListAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, periodList)

        binding.period.setAdapter(periodListAdapter)
        binding.period.setText(periodList[0], false)
        binding.period.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    when (s.toString()) {
                        periodList[0] -> viewModel.setPeriod(LogPeriod.DAY)
                        periodList[1] -> viewModel.setPeriod(LogPeriod.HALF_DAY)
                        periodList[2] -> viewModel.setPeriod(LogPeriod.HOUR6)
                        periodList[3] -> viewModel.setPeriod(LogPeriod.HOUR3)
                        periodList[4] -> viewModel.setPeriod(LogPeriod.HOUR)
                    }
                }
            }
        })

        // フローティングボタン
        binding.priorityBtn.setOnClickListener {
            val dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_log_view_setting, null)

            // val dateEditText = dialogLayout.findViewById<TextInputEditText>(R.id.date)
            // dateEditText.setOnClickListener {
            //     val datePicker = DatePickerFragment()
            //     datePicker.show(childFragmentManager, "datePicker")
            // }

            // val timeEditText = dialogLayout.findViewById<TextInputEditText>(R.id.time)
            // timeEditText.setOnClickListener {
            //     val timePicker = TimePickerFragment()
            //     timePicker.show(childFragmentManager, "timePicker")
            // }

            // val periodList = arrayOf("1日", "半日", "時間")
            // val periodListAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, periodList)

            // val periodRadioGroup = dialogLayout.findViewById<AutoCompleteTextView>(R.id.period)
            // periodRadioGroup.setAdapter(periodListAdapter)
            // periodRadioGroup.setText(periodList[0], false)
            // periodRadioGroup.addTextChangedListener(object : TextWatcher {
            //     override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //     }
            //
            //     override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //     }
            //
            //     override fun afterTextChanged(s: Editable?) {
            //         s?.let {
            //             when (s.toString()) {
            //                 periodList[0] -> viewModel.setPeriod(LogPeriod.DAY)
            //                 periodList[1] -> viewModel.setPeriod(LogPeriod.HALF_DAY)
            //                 periodList[2] -> viewModel.setPeriod(LogPeriod.HOUR)
            //             }
            //         }
            //     }
            // })

            val verboseSwitch = dialogLayout.findViewById<SwitchMaterial>(R.id.switch_verbose)
            verboseSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setPriorityVerbose(isChecked)
            }

            val debugSwitch = dialogLayout.findViewById<SwitchMaterial>(R.id.switch_debug)
            debugSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setPriorityDebug(isChecked)
            }

            val infoSwitch = dialogLayout.findViewById<SwitchMaterial>(R.id.switch_info)
            infoSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setPriorityInfo(isChecked)
            }

            val warnSwitch = dialogLayout.findViewById<SwitchMaterial>(R.id.switch_warn)
            warnSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setPriorityWarn(isChecked)
            }

            val errorSwitch = dialogLayout.findViewById<SwitchMaterial>(R.id.switch_error)
            errorSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setPriorityError(isChecked)
            }

            val assertSwitch = dialogLayout.findViewById<SwitchMaterial>(R.id.switch_assert)
            assertSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setPriorityAssert(isChecked)
            }

            // dateEditText.text = Editable.Factory.getInstance().newEditable(logViewState.date.toString())
            verboseSwitch.isChecked = logViewState.priorityVerbose
            debugSwitch.isChecked = logViewState.priorityDebug
            infoSwitch.isChecked = logViewState.priorityInfo
            warnSwitch.isChecked = logViewState.priorityWarn
            errorSwitch.isChecked = logViewState.priorityError
            assertSwitch.isChecked = logViewState.priorityAssert

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("ログレベル")
                .setView(dialogLayout)
                .show()
        }
    }

    private fun observePreferenceChanges() {

        viewModel.logViewState.observe(viewLifecycleOwner) {
            logViewState = it
            viewModel.setSelectCond(logViewState)
        }

        // 検索結果のObserve
        viewModel.selectData().observe(viewLifecycleOwner) { someEntity ->
            someEntity?.let {
                adapter.submitList(someEntity.map { it.convert() })
            }
        }
    }

    override fun selectedDate(year: Int, month: Int, dayOfMonth: Int) {
        lifecycleScope.launch {
            val date = LocalDate.of(year, month + 1, dayOfMonth)
            binding.date.text = Editable.Factory.getInstance().newEditable(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            viewModel.setDate(date)
        }
    }

    override fun selectedTime(hour: Int, minute: Int) {
        lifecycleScope.launch {
            val time = LocalTime.of(hour, minute)
            // binding.time.text = Editable.Factory.getInstance().newEditable(time.format(DateTimeFormatter.ofPattern("HH:mm")))
            // viewModel.setTime(time)
        }
    }
}
