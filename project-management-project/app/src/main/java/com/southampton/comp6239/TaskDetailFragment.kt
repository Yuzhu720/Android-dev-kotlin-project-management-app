package com.southampton.comp6239

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.southampton.comp6239.base.Database
import com.southampton.comp6239.bean.TaskItem
import com.southampton.comp6239.utils.DateUtil
import kotlinx.android.synthetic.main.frag_task_detail.*
import java.util.*
import kotlin.collections.ArrayList

class TaskDetailFragment:Fragment() {
    private lateinit var database: FirebaseFirestore
    var itemList : ArrayList<TaskItem> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.frag_task_detail, container, false)
        database = Firebase.firestore
        //get taskid
        val taskids = requireArguments().getString("taskId")
        if (taskids != null) {
            Log.d("taskid",taskids)
        }
        //get data of this task from database
        database.collection(Database.TASK).whereEqualTo("taskId",taskids).get()
            .addOnSuccessListener {
                for(document in it.documents){
                    val taskid = document.get("taskId") as String
                    val name = document.get("taskName") as String
                    task_detail_name.setText(name)
                    val projectid = document.get("projectId") as String
                    val status = document.get("status") as String
                    detail_status.setText(status)
                    if(status == "Assigned"){
                       val btn = rootView.findViewById<Button>(R.id.change_status)
                       btn.visibility = View.VISIBLE
                    }
                    val creattime = document.getDate("createTime")
                    detail_create_time.setText(DateUtil.getStringDate(creattime!!,DateUtil.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND))
                    val deadtime = document.getDate("deadline")
                    detail_deadline.setText(DateUtil.getStringDate(deadtime!!,DateUtil.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND))
                    val description = document.get("description") as String
                    task_detail_description.setText(description)
                    //get projectname and managername
                    database.collection(Database.PROJECT).whereEqualTo("projectId",projectid).get().addOnSuccessListener {
                        for (document in it.documents){
                            val projectname = document.get("projectName") as String
                            val managerid = document.get("userId") as String
                            project_name_detail.setText(projectname)
                            database.collection(Database.USER).whereEqualTo("uid",managerid).get().addOnSuccessListener {
                                for (document in it.documents){
                                    val managername = document.get("firstname") as String + " " + document.get("lastname")
                                    manager_name.setText(managername)
                                    val email = document.get("email") as String
                                    manager_email.setText(email)
                                }
                            }
                        }
                    }
                  }
                }
        //set the clickevent for complete button
        val complete_btn = rootView.findViewById<Button>(R.id.change_status)
        complete_btn.setOnClickListener {
            AlertDialog.Builder(context).apply {
                 setTitle("Complete the task")
                 setMessage("Are you sure you have completed this task?")
                 setCancelable(false)
                 setPositiveButton("Comfirm"){
                     //change this task's status into completed
                     dialog,which->
                     Log.d("task","have been completed")
                     val db = database.collection(Database.TASK).document(taskids.toString())
                     db.update("status","Completed").addOnSuccessListener {
                         Log.d("data", "DocumentSnapshot successfully updated!")
                         database.collection(Database.TASK).whereEqualTo("taskId",taskids).get().addOnSuccessListener {
                             for (document in it.documents){
                                 val status = document.get("status") as String
                                 detail_status.setText(status)
                                 if(status == "Completed"){
                                     val btn = rootView.findViewById<Button>(R.id.change_status)
                                     btn.visibility = View.GONE
                                 }
                                 //add notification
                                 database.collection(Database.PROJECT).whereEqualTo("projectId",document.get("projectId") as String)
                                     .get()
                                     .addOnSuccessListener {
                                         for(document2 in it.documents){
                                             val newNotiRef = database.collection("notification").document()
                                             val notiData = hashMapOf(
                                                 "notiId" to newNotiRef.id,
                                                 "time" to Date(),
                                                 "pop" to false,
                                                 "read" to false,
                                                 "sender" to "Task Completed Already",
                                                 "content" to "The task ${document.get("taskName") as String} in Project ${document.get("projectName") as String} has been completed by ${document.get("userName")}.",
                                                 "userId" to document2.get("userId") as String
                                             )
                                             newNotiRef.set(notiData)
                                         }
                                     }


                             }
                         }
                     }.addOnFailureListener{
                         e->Log.w("error", "Error updating document", e)
                     }

                 }
                 setNegativeButton("Cancel"){
                        dialog,which->
                 }
                 show()
            }
        }
        //back to list
        val toolbar = rootView.findViewById<Toolbar>(R.id.toolbar_detail)
        toolbar.setNavigationOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
        return rootView
    }

}