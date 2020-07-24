package ru.alexalekhin.todomanager.domain.viewModels

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.alexalekhin.todomanager.domain.models.MainModel
import ru.alexalekhin.todomanager.data.project.DBProject
import javax.inject.Inject

class MainViewModel @Inject constructor(private val model: MainModel) : ViewModel() {

    val projectLiveData: MutableLiveData<List<DBProject>> = MutableLiveData(emptyList())
    val dataLoadingState: MutableLiveData<DataLoadingState> = MutableLiveData()

    private var currentBiggestProjectId = 0
    private var currentBiggestProjectWeight = 0

    init {
        loadProjectsData()
    }

    fun createProject(projectData: Bundle): DBProject {
        return with(projectData) {
            DBProject(
                ++currentBiggestProjectId,
                getString("projectTitle") ?: "",
                getString("projectDescription") ?: "",
                getString("projectDeadline") ?: "",
                if (getInt("domainID") == ID_DOMAIN_NULL) null else getInt("domainID"),
                if (getInt("folderID") == ID_FOLDER_NULL) null else getInt("folderID"),
                ++currentBiggestProjectWeight
            )
        }
    }

    fun addCreatedProject(position: Int, project: DBProject) {
        viewModelScope.launch {
            projectLiveData.value =
                ArrayList(projectLiveData.value!!).apply { add(position, project) }
            model.addProject(project.copy())
        }
    }

    fun createAndAddProject(projectData: Bundle) {
        viewModelScope.launch {
            val project = with(projectData) {

                DBProject(
                    ++currentBiggestProjectId,
                    getString("projectTitle") ?: "",
                    getString("projectDescription") ?: "",
                    getString("projectDeadline") ?: "",
                    if (getInt("domainID") == ID_DOMAIN_NULL) null else getInt("domainID"),
                    if (getInt("folderID") == ID_FOLDER_NULL) null else getInt("folderID"),
                    ++currentBiggestProjectWeight
                )
            }
            model.addProject(project)
        }
    }

    fun deleteProject(position: Int, project: DBProject) {
        viewModelScope.launch {
            projectLiveData.value =
                ArrayList(projectLiveData.value!!).apply { removeAt(position) }
            model.deleteProject(project)
        }
    }

    fun loadProjectsData() {
        dataLoadingState.postValue(DataLoadingState.LOADING)
        viewModelScope.launch {
            projectLiveData.postValue(model.loadProjects().sortedByDescending { it.weight })
            currentBiggestProjectWeight = model.getLargestProjectWeight() ?: 0
            currentBiggestProjectId = model.getLargestProjectId() ?: 0
            dataLoadingState.postValue(DataLoadingState.LOADED)
        }
    }

    fun updateProjects(project: List<DBProject>) {
        viewModelScope.launch { model.updateProjects(project) }
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