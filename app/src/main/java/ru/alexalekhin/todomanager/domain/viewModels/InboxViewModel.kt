package ru.alexalekhin.todomanager.domain.viewModels

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.alexalekhin.todomanager.domain.models.InboxModel

import ru.alexalekhin.todomanager.data.task.DBTask

import javax.inject.Inject

class InboxViewModel @Inject constructor(private val model: InboxModel) : ViewModel() {

    val tasksLiveData: MutableLiveData<List<DBTask>> = MutableLiveData(emptyList())
    val dataLoadingState: MutableLiveData<DataLoadingState> = MutableLiveData()

    private var currentBiggestWeight = 0
    private var currentBiggestId = 0

    init {
        loadTasksData()
    }

    fun loadTasksData() {
        viewModelScope.launch {
            //TODO: filter in adapter
            tasksLiveData.postValue(model.loadTasks().sortedByDescending { it.weight }.filter { !it.checked })
            currentBiggestWeight = model.getLargestWeight() ?: 0
            currentBiggestId = model.getLargestId() ?: 0
            dataLoadingState.postValue(DataLoadingState.LOADED)
        }
        dataLoadingState.postValue(DataLoadingState.LOADING)
    }

    fun updateTasks(tasks: List<DBTask>) {
        viewModelScope.launch {
            when {
                tasks.isEmpty() -> {
                    //TODO: ERROR
                }
                else -> model.updateTasks(tasks)
            }
        }
    }

    fun createTask(taskData: Bundle): DBTask {
        return DBTask(
            ++currentBiggestId,
            taskData.getString("taskTitle") ?: "",
            taskData.getBoolean("isChecked"),
            if (taskData.getInt("projectId") == ID_PROJECT_NULL) null else taskData.getInt("projectId"),
            if (taskData.getInt("domainId") == ID_DOMAIN_NULL) null else taskData.getInt("domainId"),
            if (taskData.getInt("folderId") == ID_FOLDER_NULL) null else taskData.getInt("folderId"),
            ++currentBiggestWeight
        )
    }

    fun addCreatedTask(task: DBTask) {
        viewModelScope.launch { model.addTask(task.copy()) }
    }

    fun createAndAddTask(taskData: Bundle) {
        viewModelScope.launch {
            val task = with(taskData) {
                DBTask(
                    getInt("taskId"),
                    getString("taskTitle") ?: "",
                    getBoolean("isChecked"),
                    if (getInt("projectId") == ID_PROJECT_NULL) null else getInt("projectId"),
                    if (getInt("domainId") == ID_DOMAIN_NULL) null else getInt("domainId"),
                    if (getInt("folderId") == ID_FOLDER_NULL) null else getInt("folderId"),
                    ++currentBiggestWeight
                )
            }
            model.addTask(task)
        }
    }

    fun markTaskAsDone(task: DBTask) {
        viewModelScope.launch { model.markTaskAsDone(task) }
    }

    fun deleteTask(task: DBTask) {
        viewModelScope.launch { model.deleteTask(task) }
    }

    companion object {
        const val ID_PROJECT_NULL = 0
        const val ID_FOLDER_NULL = 0
        const val ID_DOMAIN_NULL = 0
    }

    enum class DataLoadingState {
        LOADED,
        LOADING,
        ERROR,
        IDLE
    }
}
