package com.yuruneji.camera_training2.presentation.log_view2

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.yuruneji.camera_training2.data.local.LogEntity
import com.yuruneji.camera_training2.data.repository.LogViewPreferences
import com.yuruneji.camera_training2.domain.repository.LogRepository
import com.yuruneji.camera_training2.domain.repository.LogViewPreferencesRepository
import com.yuruneji.camera_training2.presentation.log_view.model.TasksUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class LogView2ViewModel @Inject constructor(
    private val logViewPreferencesRepository: LogViewPreferencesRepository,
    repository: LogRepository
) : ViewModel() {

    val initialSetupEvent: LiveData<LogViewPreferences> = liveData {
        emit(logViewPreferencesRepository.fetchInitialPreferences())
    }

    private val _dateFlow = MutableStateFlow(LocalDateTime.now())
    val dateFlow: StateFlow<LocalDateTime> = _dateFlow

    private val logRepositoryFlow = repository.log(dateFlow.value)

    private val logViewPreferencesFlow: Flow<LogViewPreferences> = logViewPreferencesRepository.logViewPreferencesFlow()

    private val tasksUiModelFlow: Flow<TasksUiModel> = combine(
        logRepositoryFlow,
        logViewPreferencesFlow
    ) { tasks: List<LogEntity>, logViewPreferences: LogViewPreferences ->
        // val tasks2 = repository.log(LocalDateTime.parse(logViewPreferences.date, LogViewDataStore.FORMAT))

        return@combine TasksUiModel(
            tasks = filterSortTasks(
                tasks,
                logViewPreferences.priorityVerbose,
                logViewPreferences.priorityDebug,
                logViewPreferences.priorityInfo,
                logViewPreferences.priorityWarn,
                logViewPreferences.priorityError,
                logViewPreferences.priorityAssert
            ),
            priorityVerbose = logViewPreferences.priorityVerbose,
            priorityDebug = logViewPreferences.priorityDebug,
            priorityInfo = logViewPreferences.priorityInfo,
            priorityWarn = logViewPreferences.priorityWarn,
            priorityError = logViewPreferences.priorityError,
            priorityAssert = logViewPreferences.priorityAssert
        )
    }

    val tasksUiModel: LiveData<TasksUiModel> = tasksUiModelFlow.asLiveData()

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

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        viewModelScope.launch {
            _dateFlow.value = LocalDateTime.of(year, month , dayOfMonth, 0, 0)

            //LogViewDataStore
            // val date = dateFlow.value.format(LogViewDataStore.FORMAT)
            logViewPreferencesRepository.updateDate(dateFlow.value)
        }
    }

    fun showDebug(priority: BooleanArray) {
        viewModelScope.launch {
            logViewPreferencesRepository.updatePriority(priority)
        }
    }
}
