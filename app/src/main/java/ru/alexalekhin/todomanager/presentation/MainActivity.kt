package ru.alexalekhin.todomanager.presentation

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_NONE

import ru.alexalekhin.todomanager.R
import ru.alexalekhin.todomanager.presentation.fragments.InboxFragment
import ru.alexalekhin.todomanager.presentation.fragments.MainFlowFragment
import ru.alexalekhin.todomanager.presentation.fragments.MainFragment
import ru.alexalekhin.todomanager.presentation.fragments.dialogs.ProjectCreationDialogFragment
import ru.alexalekhin.todomanager.presentation.fragments.dialogs.ProjectOrDomainSelectionFragment
import ru.alexalekhin.todomanager.presentation.fragments.ProjectFragment
import ru.alexalekhin.todomanager.presentation.fragments.dialogs.TaskCreationDialogFragment
import ru.alexalekhin.todomanager.presentation.misc.OnFragmentInteractionListener
import java.lang.IllegalArgumentException

class MainActivity : AppCompatActivity(),
    OnFragmentInteractionListener,
    TaskCreationDialogFragment.TaskCreationListener,
    ProjectCreationDialogFragment.ProjectCreationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.frameLayoutMainFlowFragmentContainer,
                    MainFlowFragment.newInstance(),
                    MainFlowFragment.TAG
                )
                .setTransition(TRANSIT_NONE)
                .commit()
        }
    }

    override fun showAddTaskDialog(projectId: Int?, folderId: Int?) {
        val ft = supportFragmentManager.beginTransaction()
            .addToBackStack(TaskCreationDialogFragment.TAG)
        TaskCreationDialogFragment.newInstance(projectId, folderId)
            .show(ft, TaskCreationDialogFragment.TAG)
    }

    override fun showAddProjectDialog(folderId: Int?) {
        val ft = supportFragmentManager.beginTransaction()
        ProjectCreationDialogFragment.newInstance(folderId)
            .show(ft, ProjectCreationDialogFragment.TAG)
    }

    override fun showAddProjectOrDomain() {
        val ft = supportFragmentManager.beginTransaction()
            .addToBackStack(ProjectOrDomainSelectionFragment.TAG)
        ProjectOrDomainSelectionFragment.newInstance()
            .show(ft, ProjectOrDomainSelectionFragment.TAG)
    }

    override fun onSuccessfulTaskCreation(taskData: Bundle, projectId: Int?, folderId: Int?) {
        val flowFragment = supportFragmentManager.findFragmentByTag(MainFlowFragment.TAG)
        if (flowFragment is MainFlowFragment) {
            val fragment: Fragment? =
                if (projectId == null) {
                    when (folderId) {
                        resources.getInteger(R.integer.id_folder_inbox) -> {
                            flowFragment.childFragmentManager.findFragmentByTag(InboxFragment.TAG)
                                ?: flowFragment.childFragmentManager.findFragmentByTag(MainFragment.TAG)
                        }
                        //add other folders here
                        else -> {
                            throw IllegalArgumentException("projectId and folderId can't be null at the same time")
                        }
                    }
                } else {
                    flowFragment.childFragmentManager.findFragmentByTag(ProjectFragment.TAG)
                }

            when (fragment) {
                is InboxFragment -> fragment.onAdd(taskData)
                is ProjectFragment -> fragment.onAdd(taskData, projectId)
            }
        }
    }

    override fun onSuccessfulProjectCreation(projectData: Bundle, domainId: Int?) {
        val mainFlowFragment = supportFragmentManager.findFragmentByTag(MainFlowFragment.TAG)
        if (mainFlowFragment is MainFlowFragment) {
            val fragment = mainFlowFragment.childFragmentManager.findFragmentByTag(MainFragment.TAG)
            if (fragment is MainFragment) fragment.onAddProject(projectData)
        }
    }

    override fun showProjectEditingDialog(projectId: Int, extraData: Bundle?) {
        val flowFragment = supportFragmentManager.findFragmentByTag(MainFlowFragment.TAG)
        if (flowFragment is MainFlowFragment)
            flowFragment.showProjectEditingDialog(
                projectId,
                extraData
            )
    }

    override fun openFolder(folderId: Int) {
        val mainFlowFragment = supportFragmentManager.findFragmentByTag(MainFlowFragment.TAG)
        if (mainFlowFragment is MainFlowFragment) mainFlowFragment.openFolder(folderId)
    }

    override fun openProject(projectId: Int, extraData: Bundle?) {
        val mainFlowFragment = supportFragmentManager.findFragmentByTag(MainFlowFragment.TAG)
        if (mainFlowFragment is MainFlowFragment) mainFlowFragment.openProject(projectId, extraData)
    }

    override fun onBackPressed() {
        val flowFragment = supportFragmentManager.findFragmentByTag(MainFlowFragment.TAG)
        if (flowFragment is MainFlowFragment) {
            val fragmentCount = flowFragment.childFragmentManager.backStackEntryCount
            if (fragmentCount > BACK_STACK_MIN_COUNT) {
                flowFragment.childFragmentManager.popBackStack()
                return
            }
        }
        super.onBackPressed()
    }

    override fun hideKeyboard() {
        val imm = ContextCompat.getSystemService(this, InputMethodManager::class.java)
        imm!!.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
    }

    override fun updateProjectData(projectId: Int) {
        val flowFragment = supportFragmentManager.findFragmentByTag(MainFlowFragment.TAG)
        if (flowFragment is MainFlowFragment) {
            val fragment =
                flowFragment.childFragmentManager.findFragmentByTag(ProjectFragment.TAG)
            if (fragment is ProjectFragment) fragment.viewModel.loadProjectData(projectId)
        }
    }

    companion object {
        const val BACK_STACK_MIN_COUNT = 1
    }
}
