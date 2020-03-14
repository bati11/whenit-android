package info.bati11.whenit.ui.app.event_list


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import info.bati11.whenit.App
import info.bati11.whenit.databinding.FragmentEventListBinding
import org.threeten.bp.LocalDate

/**
 * A simple [Fragment] subclass.
 */
class EventListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentEventListBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val viewModelFactory =
            (activity!!.application as App).appComponent.viewModelFactory()
        val viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(EventListViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.loadEvents(LocalDate.now())

        viewModel.navigateToEventCreate.observe(viewLifecycleOwner, Observer {
            if (it) {
                val navController = findNavController()
                navController.navigate(EventListFragmentDirections.actionEventFragmentToEventCreateFragment())
                viewModel.onNavigatedToEventCreate()
            }
        })

        val adapter =
            EventListAdapter(EventMenuClickListener { event ->
                viewModel.displayEventMenu(event)
            })
        binding.eventList.adapter = adapter
        viewModel.events.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.showSelectedEventMenu.observe(viewLifecycleOwner, Observer { event ->
            event?.let {
                val navController = findNavController()
                navController.navigate(
                    EventListFragmentDirections.actionEventFragmentToEventMenuBottomSheetDialog(
                        it
                    )
                )
                viewModel.displayEventMenuComplete()
            }
        })

        val appCompatActivity: AppCompatActivity? = (activity as AppCompatActivity?)
        appCompatActivity?.supportActionBar?.show()

        return binding.root
    }

}
