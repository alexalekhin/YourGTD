package ru.alexalekhin.todomanager.presentation.misc

import android.os.Bundle

interface OnFragmentInteractionListener {
    fun showAddTaskDialog(projectId: Int? = null, folderId: Int? = null)
    fun showAddProjectDialog(folderId: Int? = null)
    fun showAddProjectOrDomain()
    fun showProjectEditingDialog(projectId: Int, extraData: Bundle? = null)
    fun onBackPressed()
    fun openFolder(folderId: Int)
    fun openProject(projectId: Int, extraData: Bundle? = null)
    fun hideKeyboard()
    fun updateProjectData(projectId: Int)
}