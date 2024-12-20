package com.mck.contacts.ui.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mck.contacts.R
import com.mck.contacts.databinding.FragmentContactInfoBinding
import com.mck.contacts.model.ContactDatabase


class ContactInfoFragment : Fragment() {
    var _binding: FragmentContactInfoBinding? = null
    val binding get() = _binding!!

    private lateinit var viewModel: ContactInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // get the contact id
        val contactId = ContactInfoFragmentArgs.fromBundle(requireArguments()).contactId
        // get dao
        val application = requireNotNull(this.activity).application
        val dao = ContactDatabase.getInstance(application).contactDao
        // get the view model
        val viewModelFactory = ContactInfoViewModelFactory(contactId, dao)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        )[ContactInfoViewModel::class.java]

        _binding = FragmentContactInfoBinding.inflate(inflater, container, false).apply {
            viewModel = this@ContactInfoFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setUpObservers(contactId)

        return binding.root
    }

    private fun setUpObservers(contactId: Long) {
        // navigate to list
        viewModel.navigateToContacts.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController()
                    .navigate(R.id.action_contactInfoFragment_to_contactFragment)
                viewModel.onNavigatedToList() // reset
            }
        })

        viewModel.navigateToEdit.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                val action = ContactInfoFragmentDirections.actionContactInfoFragmentToEditContactFragment(contactId)
                findNavController().navigate(action)
                viewModel.onNavigatedToEdit() // reset
            }
        }

        // Observe the call phone event
        viewModel.callPhoneEvent.observe(viewLifecycleOwner) { phoneNumber ->
            phoneNumber?.let {
                Toast.makeText(requireContext(), "Make call", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$it")
                }
                startActivity(intent)
                viewModel.onCallPhoneHandled() // Reset the event
            }
            Toast.makeText(requireContext(), "Make call", Toast.LENGTH_SHORT).show()
        }
    }

}