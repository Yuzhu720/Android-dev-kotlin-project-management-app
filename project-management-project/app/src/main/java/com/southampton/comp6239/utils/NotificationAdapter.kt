package com.southampton.comp6239.utils

import android.content.Context
import android.os.Bundle
import android.service.autofill.Dataset
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.southampton.comp6239.NotificationDetailFragment
import com.southampton.comp6239.NotificationListFragment
import com.southampton.comp6239.R
import com.southampton.comp6239.TaskDetailFragment
import com.southampton.comp6239.bean.Notification
import java.util.*

private lateinit var database: FirebaseFirestore

class NotificationAdapter(private val context: Context, private val dataSet: ArrayList<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content: TextView
        val sender: TextView
        val time: TextView
        val new: TextView

        init {
            // Define click listener for the ViewHolder's View.
            content = view.findViewById(R.id.not_content)
            sender = view.findViewById(R.id.not_sender)
            time = view.findViewById(R.id.not_time)
            new = view.findViewById(R.id.not_new)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.notification_item_layout, viewGroup, false)
        dataSet.sortBy { it.read }
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val notData = dataSet[position]
        val notificationDetailFragment = NotificationDetailFragment()
        viewHolder.sender.text = notData.sender
        viewHolder.content.text = notData.content
        viewHolder.time.text = DateUtil.getStringDate(notData?.time!!,DateUtil.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
        if(notData.read == false){
            viewHolder.new.visibility = View.VISIBLE
        }


        viewHolder.itemView.setOnClickListener {
            val notPos = viewHolder.layoutPosition
            val noti = dataSet[notPos]
            val bundle = Bundle()
            val notiId = noti.notiId
            val notiSender = noti.sender
            val notiContent = noti.content
            val notiTime = DateUtil.getStringDate(noti?.time!!,DateUtil.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
            database = Firebase.firestore
            database.collection("notification").document(notiId)
                .update("read", true)
                .addOnSuccessListener {
                    Log.d("success", "update read successfully")
                }
            bundle.putString("notiId", notiId)
            bundle.putString("notiSender", notiSender)
            bundle.putString("notiContent", notiContent)
            bundle.putString("notiTime", notiTime)
            notificationDetailFragment.setArguments(bundle)
            (context as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment, notificationDetailFragment).addToBackStack(null).commit()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size


    fun deleteItem(i : Int) {
        database = Firebase.firestore
        database.collection("notification").document(dataSet[i].notiId!!)
            .delete()
            .addOnSuccessListener {
                Log.d("tag", "DocumentSnapshot successfully deleted!")
                notifyDataSetChanged()
                (context as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment, NotificationListFragment()).addToBackStack(null).commit()
            }
            .addOnFailureListener { e -> Log.w("tag", "Error deleting document", e) }
    }

}
