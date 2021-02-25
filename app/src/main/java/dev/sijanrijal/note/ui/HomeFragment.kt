package dev.sijanrijal.note.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import dev.sijanrijal.note.ItemDecorator
import dev.sijanrijal.note.NoteClickListener
import dev.sijanrijal.note.NoteListAdapter
import dev.sijanrijal.note.R
import dev.sijanrijal.note.databinding.FragmentHomeBinding
import dev.sijanrijal.note.viewmodels.HomeFragmentViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: NoteListAdapter

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)

        //subscribe to be notified of the change in the note list
        viewModel.observable.subscribe { itemCount ->
            if (itemCount > 0) {
                binding.noteAnimation.visibility = View.GONE
                binding.fillerText.visibility = View.GONE
            } else {
                displayTextAndImageWhenListIsEmpty()
            }
        }
            .addTo(disposable)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        topAppBarMenuItemClickListener()

        binding.lifecycleOwner = this

        // sets the click listener in recycler view so that if the user taps in a note, it will
        // take the user to Update Note Fragment so that the user can read/update the note
        adapter = NoteListAdapter(NoteClickListener { noteTitle, noteContent, date, noteId->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(
                    noteTitle, noteContent, date, noteId
                )
            )
        })

        val itemDecorator = ItemDecorator()
        binding.notesRecyclerView.adapter = adapter
        binding.notesRecyclerView.addItemDecoration(itemDecorator)
        addSwipeToDelete()

        //if there is an update to the database, update the recycler view as well
        viewModel.isDatabaseChanged.observe(viewLifecycleOwner, { isDatabaseChanged ->
            if (isDatabaseChanged) {
                adapter.addHeaderAndNoteList(viewModel.notesList)
            }
        })

        // click listener to naviagate user to update note fragment to create a new note
        binding.fab.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(
                    "",
                    "",
                    Date(),
                    ""
                )
            )
        }
        return binding.root
    }

    //show the animation when the note list is empty
    private fun displayTextAndImageWhenListIsEmpty() {
        binding.fillerText.text = getString(R.string.filler_string, FirebaseAuth.getInstance().currentUser?.displayName?.substringBefore(" ") ?: "")
        binding.fillerText.visibility = View.VISIBLE
        binding.fillerText.alpha = 0f
        binding.fillerText.animate().alpha(1f).apply {
            duration = 500
        }.setStartDelay(1).start()
        binding.noteAnimation.visibility = View.VISIBLE
        binding.noteAnimation.playAnimation()
    }


    //click listener for menu item in toolbar
    private fun topAppBarMenuItemClickListener() {
        binding.toolbar.overflowIcon = resources.getDrawable(R.drawable.ic_menu)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
           when (menuItem.itemId) {
                R.id.logout_menu -> {
                    logoutUser()
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                    true
                }
               else -> false
            }
        }
    }

    /**
     * Log the user out of the application
     * **/
    private fun logoutUser() {
        Timber.d("Logging out user")
        FirebaseAuth.getInstance().signOut()
    }


    private fun addSwipeToDelete() {
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            private val backgroundColor = ColorDrawable(ContextCompat.getColor(context!!, R.color.color_delete))
            private val deleteIcon = ContextCompat.getDrawable(context!!, R.drawable.delete_icon)!!
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                    val note = viewModel.notesList[position]
                    Timber.d("Note $note")
                    viewModel.deleteNote(note)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                val itemView = viewHolder.itemView
                val backgroundCornerOffset = 20

                when  {
                    dX > 0 -> {
                        backgroundColor.setBounds(itemView.left,
                            itemView.top,
                            itemView.left + dX.toInt() + backgroundCornerOffset,
                            itemView.bottom)
                        deleteIcon.setBounds(
                            itemView.left+backgroundCornerOffset,
                            itemView.top + 2*backgroundCornerOffset,
                            itemView.right/6 + (backgroundCornerOffset/2),
                            itemView.bottom - 2*backgroundCornerOffset
                        )

                    }
                    dX < 0 -> {
                        backgroundColor.setBounds(
                            itemView.right + dX.toInt() - backgroundCornerOffset,
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                    }
                    else -> {
                        backgroundColor.setBounds(0,0,0,0)
                    }
                }
                backgroundColor.draw(c)
                deleteIcon.draw(c)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.notesRecyclerView)
    }

    //dispose the subscription
    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}