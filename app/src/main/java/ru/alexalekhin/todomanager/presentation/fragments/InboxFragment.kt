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
import kotlinx.android.synthetic.main.fragment_inbox.*

import ru.alexalekhin.todomanager.R
import ru.alexalekhin.todomanager.TODOManagerApp
import ru.alexalekhin.todomanager.presentation.misc.OnFragmentInteractionListener
import ru.alexalekhin.todomanager.domain.viewModels.InboxViewModel
import ru.alexalekhin.todomanager.presentation.adapters.InboxTaskAdapter
import ru.alexalekhin.todomanager.presentation.misc.CustomItemTouchHelperCallback
import ru.alexalekhin.todomanager.presentation.misc.CustomRecyclerViewAnimator

import javax.inject.Inject

class InboxFragment : Fragment(R.layout.fragment_inbox),
    InboxTaskAdapter.OnItemInteractionListener {
    private lateinit var inboxAdapter: InboxTaskAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var listener: OnFragmentInteractionListener? = null
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: InboxViewModel

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
        viewModel = ViewModelProvider(this, viewModelFactory)[InboxViewModel::class.java]
        setupObservers()
        val tasks = viewModel.tasksLiveData.value!!.toList()
        with(recyclerViewInboxTasks) {
            layoutManager = LinearLayoutManager(this@InboxFragment.activity)
            inboxAdapter =
                InboxTaskAdapter(this@InboxFragment)
            inboxAdapter.tasks = tasks
            adapter = inboxAdapter
            itemAnimator = CustomRecyclerViewAnimator()
            itemTouchHelper = ItemTouchHelper(CustomItemTouchHelperCallback(inboxAdapter))
            itemTouchHelper.attachToRecyclerView(this)
        }

        floatingActionButtonAddTaskToInbox.setOnClickListener {
            listener!!.showAddTaskDialog(
                null,
                resources.getInteger(R.integer.id_folder_inbox)
            )
        }
        buttonExitFromInbox.setOnClickListener { listener!!.onBackPressed() }
    }

    private fun setupObservers() {
        viewModel.tasksLiveData.observe(viewLifecycleOwner, Observer { inboxAdapter.tasks = it })

        viewModel.dataLoadingState.observe(viewLifecycleOwner, Observer {
            when (it) {
                InboxViewModel.DataLoadingState.LOADED -> {
                    textViewInboxHeader.visibility = View.VISIBLE
                    recyclerViewInboxTasks.visibility = View.VISIBLE
                    cordinatorLayoutInbox.visibility = View.VISIBLE
                }
                InboxViewModel.DataLoadingState.LOADING -> {
                    textViewInboxHeader.visibility = View.INVISIBLE
                    recyclerViewInboxTasks.visibility = View.INVISIBLE
                    cordinatorLayoutInbox.visibility = View.INVISIBLE
                }
                InboxViewModel.DataLoadingState.ERROR -> {
                }
                InboxViewModel.DataLoadingState.IDLE -> {
                }
                null -> {
                }
            }
        })
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    fun onAdd(taskData: Bundle, position: Int = 0) {
        val task = viewModel.createTask(taskData)
        inboxAdapter.tasks = ArrayList(inboxAdapter.tasks).apply { add(position, task) }
        recyclerViewInboxTasks.scrollToPosition(position)
        viewModel.addCreatedTask(task)
    }

    override fun onCheck(position: Int) {
        val task = inboxAdapter.tasks[position].copy()
        Snackbar.make(
            floatingActionButtonAddTaskToInbox,
            R.string.message_snack_bar_task_done,
            Snackbar.LENGTH_SHORT
        )
            .setAction(R.string.label_action_undo) {
                viewModel.updateTasks(listOf(task))
                inboxAdapter.tasks = ArrayList(inboxAdapter.tasks).apply { add(position, task) }
            }.show()

        viewModel.markTaskAsDone(inboxAdapter.tasks[position])
        inboxAdapter.tasks = ArrayList(inboxAdapter.tasks).apply { removeAt(position) }
    }

    override fun onDismiss(position: Int) {
        val task = inboxAdapter.tasks[position].copy()
        Snackbar.make(
            floatingActionButtonAddTaskToInbox,
            R.string.message_snack_bar_task_deleted,
            Snackbar.LENGTH_SHORT
        )
            .setAction(R.string.label_action_undo) {
                viewModel.addCreatedTask(task)
                inboxAdapter.tasks = ArrayList(inboxAdapter.tasks).apply { add(position, task) }
            }.show()
        viewModel.deleteTask(inboxAdapter.tasks[position])
        inboxAdapter.tasks = ArrayList(inboxAdapter.tasks).apply { removeAt(position) }
    }

    override fun onItemsReorder(fromPos: Int, toPos: Int) {
        viewModel.updateTasks(listOf(inboxAdapter.tasks[fromPos], inboxAdapter.tasks[toPos]))
    }

    companion object {
        fun newInstance() = InboxFragment().apply {
            arguments = Bundle().apply {

            }
        }

        const val TAG = "InboxFragment"
    }
}
