package ru.alexalekhin.todomanager.presentation.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_NONE
import ru.alexalekhin.todomanager.R
import ru.alexalekhin.todomanager.presentation.inbox.InboxFragment
import ru.alexalekhin.todomanager.presentation.head.HeadFragment
import ru.alexalekhin.todomanager.presentation.project.ProjectFragment
import ru.alexalekhin.todomanager.presentation.projectcreator.ProjectCreationDialogFragment
import ru.alexalekhin.todomanager.presentation.head.ProjectOrDomainSelectionFragment
import ru.alexalekhin.todomanager.presentation.taskcreator.TaskCreationDialogFragment
import ru.alexalekhin.todomanager.presentation.misc.OnFragmentInteractionListener

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
        TaskCreationDialogFragment.newInstance(projectId, folderId)
            .show(supportFragmentManager, TaskCreationDialogFragment.TAG)
    }

    override fun showAddProjectDialog(folderId: Int?) {
        ProjectCreationDialogFragment.newInstance(folderId)
            .show(supportFragmentManager, ProjectCreationDialogFragment.TAG)
    }

    override fun showAddProjectOrDomain() {
        ProjectOrDomainSelectionFragment.newInstance()
            .show(supportFragmentManager, ProjectOrDomainSelectionFragment.TAG)
    }

    override fun onSuccessfulTaskCreation(taskData: Bundle, projectId: Int?, folderId: Int?) {
        val flowFragment = supportFragmentManager.findFragmentByTag(MainFlowFragment.TAG)
        if (flowFragment is MainFlowFragment) {
            val fragment: Fragment? =
                if (projectId == null) {
                    when (folderId) {
                        resources.getInteger(R.integer.id_folder_inbox) -> {
                            flowFragment.childFragmentManager.findFragmentByTag(InboxFragment.TAG)
                                ?: flowFragment.childFragmentManager.findFragmentByTag(HeadFragment.TAG)
                        }
                        //add other folders here
                        else -> throw IllegalArgumentException("projectId and folderId can't be null at the same time")
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
            val fragment = mainFlowFragment.childFragmentManager.findFragmentByTag(HeadFragment.TAG)
            if (fragment is HeadFragment) fragment.onAddProject(projectData)
        }
    }

    override fun showProjectEditingDialog(projectId: Int, extraData: Bundle?) {
        val flowFragment = supportFragmentManager.findFragmentByTag(MainFlowFragment.TAG)
        if (flowFragment is MainFlowFragment)
            flowFragment.showProjectEditingDialog(projectId, extraData)
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

    override fun updateProjectData(projectId: Int) {
        val flowFragment = supportFragmentManager.findFragmentByTag(MainFlowFragment.TAG)
        if (flowFragment is MainFlowFragment) {
            val fragment = flowFragment.childFragmentManager.findFragmentByTag(ProjectFragment.TAG)
            if (fragment is ProjectFragment) fragment.viewModel.loadProjectData(projectId)
        }
    }

    companion object {

        const val BACK_STACK_MIN_COUNT = 1
    }
}
