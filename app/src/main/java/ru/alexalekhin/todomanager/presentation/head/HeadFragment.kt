package ru.alexalekhin.todomanager.presentation.head

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
import ru.alexalekhin.todomanager.data.folder.DBFolder
import ru.alexalekhin.todomanager.di.ViewModelFactory
import ru.alexalekhin.todomanager.presentation.misc.CustomItemTouchHelperCallback
import ru.alexalekhin.todomanager.presentation.misc.CustomRecyclerViewAnimator
import ru.alexalekhin.todomanager.presentation.misc.OnFragmentInteractionListener
import javax.inject.Inject

class HeadFragment : Fragment(R.layout.fragment_main) {

    private lateinit var headProjectsAdapter: HeadProjectsAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var listener: OnFragmentInteractionListener? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var viewModel: HeadViewModel

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
        viewModel = ViewModelProvider(this, viewModelFactory)[HeadViewModel::class.java]
        setupObservers()

        with(recyclerViewDomainedProjects) {
            layoutManager = LinearLayoutManager(this@HeadFragment.context)
            headProjectsAdapter = HeadProjectsAdapter(::onProjectClick, ::onItemsReorder, ::onDismiss)
                .apply { projects = listOf() }
                .also { mainScreenProjectsAdapter -> adapter = mainScreenProjectsAdapter }

            itemTouchHelper = ItemTouchHelper(CustomItemTouchHelperCallback(headProjectsAdapter))
            itemTouchHelper.attachToRecyclerView(recyclerViewDomainedProjects)

            itemAnimator = CustomRecyclerViewAnimator()
        }

        btn_folder_inbox.setOnClickListener {
            listener?.openFolder(resources.getInteger(R.integer.id_folder_inbox))
        }
        floatinActionButtonAddNewProjectOrDomain.setOnClickListener {
            listener?.showAddProjectOrDomain()
        }

        viewModel.loadProjectsData()
    }

    private fun setupObservers() {
        viewModel.projectLiveData.observe(viewLifecycleOwner, Observer { projects ->
            headProjectsAdapter.projects = projects
        })
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

    private fun onItemsReorder(fromPos: Int, toPos: Int) {
        viewModel.updateProjects(
            listOf(
                headProjectsAdapter.projects[fromPos],
                headProjectsAdapter.projects[toPos]
            )
        )
    }

    private fun onDismiss(position: Int) {
        val project = headProjectsAdapter.projects[position].copy()

        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.message_deletion_project)
            .setPositiveButton(R.string.label_action_yes) { _, _ ->
                viewModel.deleteProject(position, project)
            }
            .setNegativeButton(R.string.label_action_no) { _, _ ->
                headProjectsAdapter.projects = ArrayList(headProjectsAdapter.projects).apply {
                    removeAt(position)
                    add(position, project)
                }
            }
            .setTitle(R.string.title_warning)
            .show()
    }

    private fun onProjectClick(position: Int) {
        val project = headProjectsAdapter.projects[position]
        listener?.openProject(
            projectId = project.id,
            extraData = Bundle().apply {
                putString("projectTitle", project.title)
                putString("projectDescription", project.description)
                putString("projectDeadline", project.deadline)
            })
    }

    companion object {

        @JvmStatic
        fun newInstance() = HeadFragment()

        const val TAG = "MainFragment"
    }
}
