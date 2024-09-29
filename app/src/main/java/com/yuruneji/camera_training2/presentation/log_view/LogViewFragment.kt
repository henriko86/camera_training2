package com.yuruneji.camera_training2.presentation.log_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuruneji.camera_training2.common.data_store.LogViewDataStore
import com.yuruneji.camera_training2.data.local.convert
import com.yuruneji.camera_training2.databinding.FragmentLogViewBinding
import com.yuruneji.camera_training2.presentation.log_view.state.LogViewState
import com.yuruneji.camera_training2.presentation.log_view.view.LogViewAdapter
import com.yuruneji.camera_training2.presentation.log_view.view.LogViewItem
import com.yuruneji.camera_training2.presentation.view.DatePickerFragment
import com.yuruneji.camera_training2.presentation.view.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime

@AndroidEntryPoint
class LogViewFragment : Fragment(), DatePickerFragment.OnSelectedDateListener, TimePickerFragment.OnSelectedTimeListener {

    private var _binding: FragmentLogViewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LogViewViewModel by viewModels()
    private val adapter: LogViewAdapter = LogViewAdapter()

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

        // viewModel.initialSetupEvent.observe(viewLifecycleOwner) { initialSetupEvent ->
        //     updateTaskFilters(
        //         initialSetupEvent.priorityVerbose,
        //         initialSetupEvent.priorityDebug,
        //         initialSetupEvent.priorityInfo,
        //         initialSetupEvent.priorityWarn,
        //         initialSetupEvent.priorityError,
        //         initialSetupEvent.priorityAssert,
        //     )
        //     setupOnCheckedChangeListeners()
        //     observePreferenceChanges()
        // }

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
        binding.time.setOnClickListener {
            val datePicker = DatePickerFragment()
            datePicker.show(childFragmentManager, "datePicker")
        }

        // ログレベル
        binding.logPriorityBtn.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("デバッグレベル")
                // .setMessage("メッセージ内容\nここに表示される文字は自動で改行されるようになっています．")
                .setMultiChoiceItems(debugTextList, debugList) { dialog, which, isChecked ->
                    Timber.d("which: $which, isChecked: $isChecked")
                    when (which) {
                        0 -> debugList[0] = isChecked
                        1 -> debugList[1] = isChecked
                        2 -> debugList[2] = isChecked
                        3 -> debugList[3] = isChecked
                        4 -> debugList[4] = isChecked
                        5 -> debugList[5] = isChecked
                    }

                    // viewModel.showDebug(debugList)
                    viewModel.setSelectCond(
                        LogViewState(
                            date = date,
                            priorityVerbose = debugList[0],
                            priorityDebug = debugList[1],
                            priorityInfo = debugList[2],
                            priorityWarn = debugList[3],
                            priorityError = debugList[4],
                            priorityAssert = debugList[5],
                        )
                    )
                }
                .show()
        }
    }

    private fun observePreferenceChanges() {
        // viewModel.tasksUiModel.observe(viewLifecycleOwner) { tasksUiModel ->
        //     tasksUiModel.tasks.let { items ->
        //         val items2: List<LogViewItem> = items.map {
        //             it.convert()
        //         }
        //         adapter.submitList(items2)
        //     }
        //     updateTaskFilters(
        //         tasksUiModel.priorityVerbose,
        //         tasksUiModel.priorityDebug,
        //         tasksUiModel.priorityInfo,
        //         tasksUiModel.priorityWarn,
        //         tasksUiModel.priorityError,
        //         tasksUiModel.priorityAssert
        //     )
        // }

        // 画面から取ってきた検索条件を設定する
        viewModel.setSelectCond(
            LogViewState(
                date = date,
                priorityVerbose = debugList[0],
                priorityDebug = debugList[1],
                priorityInfo = debugList[2],
                priorityWarn = debugList[3],
                priorityError = debugList[4],
                priorityAssert = debugList[5],
            )
        )

        // 検索結果のObserve
        viewModel.selectData().observe(viewLifecycleOwner) { someEntity ->
            // 結果をViewに表示してやる
            // displayData(someEntity)
            someEntity?.let {
                val items2: List<LogViewItem> = someEntity.map {
                    it.convert()
                }
                adapter.submitList(items2)
            }
        }
    }

    private var date: LocalDate = LocalDate.now()
    private val debugTextList = arrayOf("Verbose", "Debug", "Info", "Warn", "Error", "Assert")
    private val debugList = booleanArrayOf(false, false, false, false, false, false)


    private fun updateTaskFilters(
        priorityVerbose: Boolean,
        priorityDebug: Boolean,
        priorityInfo: Boolean,
        priorityWarn: Boolean,
        priorityError: Boolean,
        priorityAssert: Boolean
    ) {
        with(binding) {
            // showDebugShowDebug.isChecked = showCompleted
            // sortDeadline.isChecked = sortOrder == SortOrder.BY_DEADLINE || sortOrder == SortOrder.BY_DEADLINE_AND_PRIORITY
            // sortPriority.isChecked = sortOrder == SortOrder.BY_PRIORITY || sortOrder == SortOrder.BY_DEADLINE_AND_PRIORITY
        }

        debugList[0] = priorityVerbose
        debugList[1] = priorityDebug
        debugList[2] = priorityInfo
        debugList[3] = priorityWarn
        debugList[4] = priorityError
        debugList[5] = priorityAssert

        // when (which) {
        //     0 -> debugList[0] = isChecked
        //     1 -> debugList[1] = isChecked
        //     2 -> debugList[2] = isChecked
        //     3 -> debugList[3] = isChecked
        //     4 -> debugList[4] = isChecked
        //     5 -> debugList[5] = isChecked
        // }
    }

    override fun selectedDate(year: Int, month: Int, dayOfMonth: Int) {
        Timber.d("year: $year, month: $month, dayOfMonth: $dayOfMonth")

        lifecycleScope.launch {
            binding.date.setText("$year-${month + 1}-$dayOfMonth")

            // viewModel.setDate(year, month + 1, dayOfMonth)

            date = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0).toLocalDate()

            viewModel.setSelectCond(
                LogViewState(
                    date = date,
                    priorityVerbose = debugList[0],
                    priorityDebug = debugList[1],
                    priorityInfo = debugList[2],
                    priorityWarn = debugList[3],
                    priorityError = debugList[4],
                    priorityAssert = debugList[5],
                )
            )
        }
    }

    override fun selectedTime(hour: Int, minute: Int) {
        Timber.d("hour: $hour, minute: $minute")
    }
}
