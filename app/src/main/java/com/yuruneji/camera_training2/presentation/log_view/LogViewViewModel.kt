package com.yuruneji.camera_training2.presentation.log_view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.yuruneji.camera_training2.common.data_store.LogViewDataStore
import com.yuruneji.camera_training2.data.local.LogEntity
import com.yuruneji.camera_training2.domain.repository.LogRepository
import com.yuruneji.camera_training2.presentation.log_view.model.SomeEntity
import com.yuruneji.camera_training2.presentation.log_view.model.TasksUiModel
import com.yuruneji.camera_training2.presentation.log_view.state.LogViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class LogViewViewModel @Inject constructor(
    private val repository: LogRepository
) : ViewModel() {


    // 検索条件のLiveData
    private val searchCond = MutableLiveData<LogViewState>()

    // MainActivityから検索条件を設定してやる関数
    fun setSelectCond(searchCd: LogViewState) {
        searchCond.value = searchCd
    }

    // 検索する関数
    fun selectData(): LiveData<List<LogEntity>> = searchCond.switchMap { searchCd ->
        if (searchCd == null) {
            MutableLiveData()
        } else {
            val dateTime = LocalDateTime.of(searchCd.date, LocalDateTime.MIN.toLocalTime())
            repository.log(dateTime).asLiveData()
        }
    }


    // val initialSetupEvent: LiveData<LogViewPreferences> = liveData {
    //     emit(logViewPreferencesRepository.fetchInitialPreferences())
    // }

    // private val _dateFlow = MutableStateFlow(LocalDateTime.now())
    // val dateFlow: StateFlow<LocalDateTime> = _dateFlow

    // private val logRepositoryFlow = repository.log(dateFlow.value)

    // private val logViewPreferencesFlow: Flow<LogViewPreferences> = logViewPreferencesRepository.logViewPreferencesFlow()

    // private val tasksUiModelFlow: Flow<TasksUiModel> = combine(
    //     logRepositoryFlow,
    //     logViewPreferencesFlow
    // ) { tasks: List<LogEntity>, logViewPreferences: LogViewPreferences ->
    //     // val tasks2 = repository.log(LocalDateTime.parse(logViewPreferences.date, LogViewDataStore.FORMAT))
    //
    //     return@combine TasksUiModel(
    //         tasks = filterSortTasks(
    //             tasks,
    //             logViewPreferences.priorityVerbose,
    //             logViewPreferences.priorityDebug,
    //             logViewPreferences.priorityInfo,
    //             logViewPreferences.priorityWarn,
    //             logViewPreferences.priorityError,
    //             logViewPreferences.priorityAssert
    //         ),
    //         priorityVerbose = logViewPreferences.priorityVerbose,
    //         priorityDebug = logViewPreferences.priorityDebug,
    //         priorityInfo = logViewPreferences.priorityInfo,
    //         priorityWarn = logViewPreferences.priorityWarn,
    //         priorityError = logViewPreferences.priorityError,
    //         priorityAssert = logViewPreferences.priorityAssert
    //     )
    // }

    // val tasksUiModel: LiveData<TasksUiModel> = tasksUiModelFlow.asLiveData()

    private fun filterSortTasks(
        tasks: List<LogEntity>,
        priorityVerbose: Boolean,
        priorityDebug: Boolean,
        priorityInfo: Boolean,
        priorityWarn: Boolean,
        priorityError: Boolean,
        priorityAssert: Boolean
    ): List<LogEntity> {
        var filteredTasks = tasks
        if (!priorityVerbose) {
            filteredTasks = filteredTasks.filter { it.priority != Log.VERBOSE }
        }
        if (!priorityDebug) {
            filteredTasks = filteredTasks.filter { it.priority != Log.DEBUG }
        }
        if (!priorityInfo) {
            filteredTasks = filteredTasks.filter { it.priority != Log.INFO }
        }
        if (!priorityWarn) {
            filteredTasks = filteredTasks.filter { it.priority != Log.WARN }
        }
        if (!priorityError) {
            filteredTasks = filteredTasks.filter { it.priority != Log.ERROR }
        }
        if (!priorityAssert) {
            filteredTasks = filteredTasks.filter { it.priority != Log.ASSERT }
        }

        return filteredTasks
    }

    // fun setDate(year: Int, month: Int, dayOfMonth: Int) {
    //     viewModelScope.launch {
    //         // _dateFlow.value = LocalDateTime.of(year, month, dayOfMonth, 0, 0)
    //
    //         // logViewPreferencesRepository.updateDate(dateFlow.value)
    //     }
    // }

    // fun showDebug(priority: BooleanArray) {
    //     viewModelScope.launch {
    //         // logViewPreferencesRepository.updatePriority(priority)
    //     }
    // }
}
