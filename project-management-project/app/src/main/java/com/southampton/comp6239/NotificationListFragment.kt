package com.southampton.comp6239

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.southampton.comp6239.base.Database
import com.southampton.comp6239.base.Role
import com.southampton.comp6239.base.Status
import com.southampton.comp6239.base.UserInfo
import com.southampton.comp6239.bean.Notification
import com.southampton.comp6239.bean.Project
import com.southampton.comp6239.utils.NotificationAdapter
import com.southampton.comp6239.utils.NotificationWorker
import com.southampton.comp6239.utils.RecyclerAdapter
import com.southampton.comp6239.utils.SwipeGesture
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class NotificationListFragment: Fragment() {
    private lateinit var database: FirebaseFirestore
    lateinit var recyclerView : RecyclerView
    lateinit var notificationList : ArrayList<Notification>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_notification_list, container, false)
        //database init
        database = Firebase.firestore
        recyclerView= rootView.findViewById(R.id.notification_list)
        notificationList = ArrayList()

        database.collection(Database.NOTIFICATION).whereEqualTo("userId", UserInfo.user.uid).get().addOnSuccessListener {
            if(it.size() == 0) {
                rootView.findViewById<TextView>(R.id.no_notification).visibility = View.VISIBLE
            }
            else {
                rootView.findViewById<TextView>(R.id.no_notification).visibility = View.INVISIBLE
                for(data in it.documents) {
                    var notiId = data.get("notiId") as String
                    var content = data.get("content") as String
                    var sender = data.get("sender") as String
                    var time = data.getDate("time")
                    var read = data.getBoolean("read")
                    notificationList.add(Notification(content, sender, time, notiId, read))

                }
                val adapter = NotificationAdapter(requireContext(), notificationList)
                val swipeGesture = object : SwipeGesture(requireContext()){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        when(direction) {
                            ItemTouchHelper.LEFT -> {
                                adapter.deleteItem(viewHolder.adapterPosition)
                            }
                        }
                    }
                }
                val touchHelper = ItemTouchHelper(swipeGesture)
                touchHelper.attachToRecyclerView(recyclerView)
                recyclerView.adapter = adapter
            }
        }

        //Listen to the new messages
        val myWorkerRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(
                workDataOf(
                    "userId" to UserInfo.user.uid
                ))
            .build()
        WorkManager.getInstance(requireContext()).enqueue(myWorkerRequest)

        return rootView
    }
}