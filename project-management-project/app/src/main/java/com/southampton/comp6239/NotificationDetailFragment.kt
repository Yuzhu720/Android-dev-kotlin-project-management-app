package com.southampton.comp6239

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.green
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.southampton.comp6239.base.Database

class NotificationDetailFragment: Fragment() {
    private lateinit var database: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootview: View = inflater.inflate(R.layout.fragment_notification_detail, container, false)
        val notiId = requireArguments().getString("notiId")
        val notiContent = requireArguments().getString("notiContent")
        val notiSender = requireArguments().getString("notiSender")
        val notiTime = requireArguments().getString("notiTime")

        val nBarText = rootview.findViewById<TextView>(R.id.noti_detail_bartext)
        val nContent = rootview.findViewById<TextView>(R.id.noti_detail_text)
        val nTime = rootview.findViewById<TextView>(R.id.noti_detail_time)

        nBarText.text = notiSender
        nContent.text = notiContent
        nTime.text = notiTime

        val myToolbar = rootview.findViewById<Toolbar>(R.id.noti_detail_toolbar)
        myToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        myToolbar.setNavigationOnClickListener { view ->
            (context as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment, NotificationListFragment()).addToBackStack(null).commit()
        }
        myToolbar.inflateMenu(R.menu.noti_detail_menu)
        myToolbar.setOnMenuItemClickListener {
            database = Firebase.firestore
            when(it.itemId){
                R.id.noti_delete -> {
                    database.collection("notification").document(notiId!!)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("tag", "DocumentSnapshot successfully deleted!")
                            (context as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment, NotificationListFragment()).addToBackStack(null).commit()
                        }
                        .addOnFailureListener { e -> Log.w("tag", "Error deleting document", e) }
                    true
                }
                else -> false
            }
        }

        return rootview
    }
}