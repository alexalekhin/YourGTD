package ru.alexalekhin.todomanager.presentation.inbox

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
import ru.alexalekhin.todomanager.presentation.entities.DataLoadingState
import ru.alexalekhin.todomanager.presentation.misc.CustomItemTouchHelperCallback
import ru.alexalekhin.todomanager.presentation.misc.CustomRecyclerViewAnimator
import ru.alexalekhin.todomanager.presentation.misc.OnFragmentInteractionListener
import javax.inject.Inject

class InboxFragment : Fragment(R.layout.fragment_inbox) {
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
        viewModel = ViewModelProvider(this, viewModelFactory)[InboxViewModel::class.java]
        setupObservers()

        with(recyclerViewInboxTasks) {
            layoutManager = LinearLayoutManager(this@InboxFragment.activity)
            inboxAdapter = InboxTaskAdapter(::onCheck, ::onDismiss, ::onItemsReorder)
                .apply { tasks = listOf() }
                .also { inboxAdapter -> adapter = inboxAdapter }

            itemTouchHelper = ItemTouchHelper(CustomItemTouchHelperCallback(inboxAdapter))
            itemTouchHelper.attachToRecyclerView(recyclerViewInboxTasks)

            itemAnimator = CustomRecyclerViewAnimator()
        }

        floatingActionButtonAddTaskToInbox.setOnClickListener {
            listener?.showAddTaskDialog(
                null,
                resources.getInteger(R.integer.id_folder_inbox)
            )
        }
        buttonExitFromInbox.setOnClickListener { listener!!.onBackPressed() }

        viewModel.loadTasksData()
    }

    private fun setupObservers() {
        viewModel.tasksLiveData.observe(viewLifecycleOwner, Observer { inboxAdapter.tasks = it })

        viewModel.dataLoadingState.observe(viewLifecycleOwner, Observer {
            when (it) {
                DataLoadingState.LOADED -> {
                    textViewInboxHeader.visibility = View.VISIBLE
                    recyclerViewInboxTasks.visibility = View.VISIBLE
                    cordinatorLayoutInbox.visibility = View.VISIBLE
                }
                DataLoadingState.LOADING -> {
                    textViewInboxHeader.visibility = View.INVISIBLE
                    recyclerViewInboxTasks.visibility = View.INVISIBLE
                    cordinatorLayoutInbox.visibility = View.INVISIBLE
                }
                DataLoadingState.ERROR -> {
                }
                DataLoadingState.IDLE -> {
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

    private fun onCheck(position: Int) {
        val task = inboxAdapter.tasks[position].copy()
        Snackbar.make(constraintLayoutInboxRoot, R.string.message_snack_bar_task_done, Snackbar.LENGTH_SHORT)
            .setAction(R.string.label_action_undo) {
                viewModel.updateTasks(listOf(task))
                inboxAdapter.tasks = ArrayList(inboxAdapter.tasks).apply { add(position, task) }
            }.show()

        viewModel.markTaskAsDone(inboxAdapter.tasks[position])
        inboxAdapter.tasks = ArrayList(inboxAdapter.tasks).apply { removeAt(position) }
    }

    private fun onDismiss(position: Int) {
        val task = inboxAdapter.tasks[position].copy()

        Snackbar.make(constraintLayoutInboxRoot, R.string.message_snack_bar_task_deleted, Snackbar.LENGTH_SHORT)
            .setAction(R.string.label_action_undo) {
                viewModel.addCreatedTask(task)
                inboxAdapter.tasks = ArrayList(inboxAdapter.tasks).apply { add(position, task) }
            }.show()

        viewModel.deleteTask(inboxAdapter.tasks[position])
        inboxAdapter.tasks = ArrayList(inboxAdapter.tasks).apply { removeAt(position) }
    }

    private fun onItemsReorder(fromPos: Int, toPos: Int) {
        viewModel.updateTasks(listOf(inboxAdapter.tasks[fromPos], inboxAdapter.tasks[toPos]))
    }

    companion object {

        fun newInstance() = InboxFragment()

        const val TAG = "InboxFragment"
    }
}
