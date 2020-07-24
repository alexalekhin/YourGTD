package ru.alexalekhin.todomanager.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_main.*
import ru.alexalekhin.todomanager.R
import ru.alexalekhin.todomanager.TODOManagerApp
import ru.alexalekhin.todomanager.presentation.misc.OnFragmentInteractionListener
import ru.alexalekhin.todomanager.domain.viewModels.MainViewModel
import ru.alexalekhin.todomanager.presentation.adapters.MainScreenProjectsAdapter
import ru.alexalekhin.todomanager.data.folder.DBFolder
import ru.alexalekhin.todomanager.di.ViewModelFactory
import ru.alexalekhin.todomanager.presentation.misc.CustomItemTouchHelperCallback
import ru.alexalekhin.todomanager.presentation.misc.CustomRecyclerViewAnimator
import javax.inject.Inject

class MainFragment : Fragment(R.layout.fragment_main),
    MainScreenProjectsAdapter.OnItemInteractionListener {

    private lateinit var mainScreenProjectsAdapter: MainScreenProjectsAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var listener: OnFragmentInteractionListener? = null
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var viewModel: MainViewModel

    //TODO: move to model+viewmodel
    private val inbox: DBFolder = DBFolder(0, "Inbox")

    override fun onAttach(context: Context) {
        (context.applicationContext as TODOManagerApp).component.inject(this)
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw IllegalStateException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        setupObservers()
        val projects = viewModel.projectLiveData.value!!
        with(recyclerViewDomainedProjects) {
            layoutManager = LinearLayoutManager(this@MainFragment.context)
            mainScreenProjectsAdapter =
                MainScreenProjectsAdapter(this@MainFragment)
            mainScreenProjectsAdapter.projects = projects
            adapter = mainScreenProjectsAdapter
            itemAnimator = CustomRecyclerViewAnimator()
            itemTouchHelper =
                ItemTouchHelper(CustomItemTouchHelperCallback(mainScreenProjectsAdapter))
            itemTouchHelper.attachToRecyclerView(this)
        }
        btn_folder_inbox.setOnClickListener {
            listener?.openFolder(resources.getInteger(R.integer.id_folder_inbox))
        }
        floatinActionButtonAddNewProjectOrDomain.setOnClickListener {
            listener?.showAddProjectOrDomain()
        }
    }

    private fun setupObservers() {
        viewModel.projectLiveData.observe(
            viewLifecycleOwner,
            Observer { mainScreenProjectsAdapter.projects = it }
        )
    }

    override fun onStop() {
        viewModel.projectLiveData.removeObservers(this)
        super.onStop()
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    fun onAddProject(projectData: Bundle, position: Int = 0) {
        val project = viewModel.createProject(projectData)
        viewModel.addCreatedProject(position, project)
        recyclerViewDomainedProjects.scrollToPosition(position)
    }

    override fun onItemsReorder(fromPos: Int, toPos: Int) {
        viewModel.updateProjects(
            listOf(
                mainScreenProjectsAdapter.projects[fromPos],
                mainScreenProjectsAdapter.projects[toPos]
            )
        )
    }

    override fun onDismiss(position: Int) {
        val project = mainScreenProjectsAdapter.projects[position].copy()
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.message_deletion_project)
            .setPositiveButton(R.string.label_action_yes) { _, _ ->
                viewModel.deleteProject(position, project)
            }
            .setNegativeButton(R.string.label_action_no) { _, _ ->
                mainScreenProjectsAdapter.projects =
                    ArrayList(mainScreenProjectsAdapter.projects).apply {
                        removeAt(position)
                        add(position, project)
                    }
            }
            .setTitle(R.string.title_warning)
            .show()
    }

    override fun onProjectClick(position: Int) {
        val project = mainScreenProjectsAdapter.projects[position]
        listener?.openProject(project.id,
            Bundle().apply {
                putString("projectTitle", project.title)
                putString("projectDescription", project.description)
                putString("projectDeadline", project.deadline)
            })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            MainFragment().apply {
                arguments = Bundle().apply {

                }
            }

        const val TAG = "MainFragment"
    }
}
