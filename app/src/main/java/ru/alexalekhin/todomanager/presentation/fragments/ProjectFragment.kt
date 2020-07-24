package ru.alexalekhin.todomanager.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_project.*
import ru.alexalekhin.todomanager.R
import ru.alexalekhin.todomanager.TODOManagerApp
import ru.alexalekhin.todomanager.presentation.misc.OnFragmentInteractionListener
import ru.alexalekhin.todomanager.domain.viewModels.ProjectViewModel
import ru.alexalekhin.todomanager.presentation.adapters.ProjectTaskAdapter
import ru.alexalekhin.todomanager.di.ViewModelFactory
import ru.alexalekhin.todomanager.presentation.misc.CustomItemTouchHelperCallback
import ru.alexalekhin.todomanager.presentation.misc.CustomRecyclerViewAnimator
import javax.inject.Inject

class ProjectFragment : Fragment(R.layout.fragment_project),
    ProjectTaskAdapter.OnItemInteractionListener {
    private lateinit var projectTaskAdapter: ProjectTaskAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var listener: OnFragmentInteractionListener? = null
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var viewModel: ProjectViewModel

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
        viewModel =
            ViewModelProvider(
                this,
                viewModelFactory
            )[ProjectViewModel::class.java]
        setupObservers()
        val projectId: Int
        arguments!!.let {
            projectId = it.getInt("projectId")
            viewModel.loadProjectData(projectId)
            viewModel.loadTasksDataOfProject(projectId)
        }

        with(recyclerViewProjectTasks) {
            layoutManager = LinearLayoutManager(this@ProjectFragment.activity)
            projectTaskAdapter =
                ProjectTaskAdapter(this@ProjectFragment)
            adapter = projectTaskAdapter
            itemTouchHelper = ItemTouchHelper(CustomItemTouchHelperCallback(projectTaskAdapter))
            itemTouchHelper.attachToRecyclerView(this)
            itemAnimator = CustomRecyclerViewAnimator()
        }
        floatingActionButtonAddTaskToProject.setOnClickListener {
            listener?.showAddTaskDialog(projectId)
        }
        buttonEditProject.setOnClickListener {
            listener?.showProjectEditingDialog(projectId,
                Bundle().apply {
                    putString("projectTitle", textViewProjectTitle.text.toString())
                    putString("projectDeadline", textViewDeadline.text.toString())
                    putString("projectDescription", textViewProjectDescription.text.toString())
                })
        }
    }

    private fun setupObservers() {
        viewModel.dataLoadingState.observe(viewLifecycleOwner, Observer {
            when (it) {
                ProjectViewModel.DataLoadingState.LOADED -> {
                    textViewProjectTitle.visibility = View.VISIBLE
                    recyclerViewProjectTasks.visibility = View.VISIBLE
                }
                ProjectViewModel.DataLoadingState.LOADING -> {
                    textViewProjectTitle.visibility = View.INVISIBLE
                    recyclerViewProjectTasks.visibility = View.INVISIBLE
                }
                ProjectViewModel.DataLoadingState.ERROR -> {
                }
                ProjectViewModel.DataLoadingState.IDLE -> {
                }
                null -> {
                }
            }
        })

        viewModel.tasksLiveData.observe(viewLifecycleOwner, Observer { projectTaskAdapter.tasks = it })
        viewModel.projectLiveData.observe(viewLifecycleOwner, Observer {
            textViewProjectTitle.text = it.title
            textViewDeadline.text = it.deadline
            textViewProjectDescription.text = it.description
            //TODO: set domain
        })
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    fun onAdd(taskData: Bundle, projectId: Int?, position: Int = 0) {
        val task = viewModel.createTaskInProject(taskData, projectId)
        projectTaskAdapter.tasks = ArrayList(projectTaskAdapter.tasks).apply { add(position, task) }
        recyclerViewProjectTasks.scrollToPosition(position)
        viewModel.addCreatedTaskToProject(task, projectId)
    }

    override fun onCheck(position: Int) {
        val task = projectTaskAdapter.tasks[position].copy()
        Snackbar.make(floatingActionButtonAddTaskToProject, R.string.message_snack_bar_task_done, Snackbar.LENGTH_SHORT)
            .setAction(R.string.label_action_undo) {
                viewModel.updateTasksOfProject(listOf(task), arguments!!.getInt("projectId"))
                projectTaskAdapter.tasks =
                    ArrayList(projectTaskAdapter.tasks).apply { add(position, task) }
            }.show()
        viewModel.markTaskAsDone(
            projectTaskAdapter.tasks[position],
            arguments!!.getInt("projectId")
        )
        projectTaskAdapter.tasks = ArrayList(projectTaskAdapter.tasks).apply { removeAt(position) }
    }

    override fun onDismiss(position: Int) {
        val task = projectTaskAdapter.tasks[position].copy()
        Snackbar.make(floatingActionButtonAddTaskToProject, R.string.message_snack_bar_task_deleted, Snackbar.LENGTH_SHORT)
            .setAction(R.string.label_action_undo) {
                viewModel.addCreatedTaskToProject(task, arguments!!.getInt("projectId"))
                projectTaskAdapter.tasks =
                    ArrayList(projectTaskAdapter.tasks).apply { add(position, task) }
            }.show()

        viewModel.deleteTaskFromProject(
            projectTaskAdapter.tasks[position],
            arguments!!.getInt("projectId")
        )
        projectTaskAdapter.tasks = ArrayList(projectTaskAdapter.tasks).apply { removeAt(position) }
    }

    override fun onItemsReorder(fromPos: Int, toPos: Int) {
        viewModel.updateTasksOfProject(
            listOf(
                projectTaskAdapter.tasks[fromPos],
                projectTaskAdapter.tasks[toPos]
            ), arguments!!.getInt("projectId")
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(projectId: Int, extraData: Bundle? = null) =
            ProjectFragment().apply {
                arguments = Bundle().apply {
                    putInt("projectId", projectId)
                    extraData?.let {
                        putAll(it)
                    }
                }
            }

        const val TAG = "ProjectFragment"
    }
}
