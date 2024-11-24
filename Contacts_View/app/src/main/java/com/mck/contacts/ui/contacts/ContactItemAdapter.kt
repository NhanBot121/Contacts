package com.mck.contacts.ui.contacts

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mck.contacts.R
import com.mck.contacts.model.Contact

class ContactItemAdapter(val clickListener: (contactId: Long) -> Unit) :
    RecyclerView.Adapter<ContactItemAdapter.ContactViewHolder>() {

    var contacts = listOf<Contact>()
        get() = field
        set(value) {
            field = value
            notifyDataSetChanged()
            Log.d("ContactItemAdapter", "Contacts: ${value.map { it.name }}")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact, clickListener)
    }

    override fun getItemCount() = contacts.size

    class ContactViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val contactName = itemView.findViewById<TextView>(R.id.contact_name)

        fun bind(contact: Contact, clickListener: (contactId: Long) -> Unit) {
            contactName.text = contact.name
            itemView.setOnClickListener { clickListener(contact.id) }
        }
    }
}
