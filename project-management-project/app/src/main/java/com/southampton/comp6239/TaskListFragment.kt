package com.southampton.comp6239

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.southampton.comp6239.base.Database
import com.southampton.comp6239.base.UserInfo
import com.southampton.comp6239.bean.Task
import com.southampton.comp6239.utils.NotificationWorker
import com.southampton.comp6239.utils.TaskAdapter
import kotlinx.android.synthetic.main.frag_task_list.*

class TaskListFragment:Fragment() {
    //1. connect the database
    private lateinit var database:FirebaseFirestore
    //2. recyclerview
    lateinit var recyclerView: RecyclerView
    val taskList = ArrayList<Task>()
    lateinit var adapter : TaskAdapter
    //val projectname:String =""
    //find the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.frag_task_list, container, false)
        //1. init tasklist
        recyclerView = rootView.findViewById(R.id.task_list)
        //2. database init:
        database = Firebase.firestore
        database.collection(Database.TASK).whereEqualTo("userId",UserInfo.user.uid).get().addOnSuccessListener {
            taskList.clear()
            for (document in it.documents){
                val t = document.toObject(Task::class.java) as Task
                val username = UserInfo.user.userName
                //taskList.add(Task(id,name,projectid,username,userid,status,creattime,deadtime,description))
                val x = database.collection(Database.PROJECT).whereEqualTo("projectId",t.projectId).get()
                x.addOnSuccessListener{
                    for(document in it.documents){
                        val projectname = document.get("projectName") as String
                        taskList.add(t)
                        taskList.sortByDescending {it.createTime }
                    }
                    //异步
                    adapter.notifyDataSetChanged()
                  }
                }
                adapter = TaskAdapter(requireContext(),taskList)
                recyclerView.adapter = adapter
        }
        //function ：filter list：
        val toolbar = rootView.findViewById<Toolbar>(R.id.toolbar_member)
        toolbar.inflateMenu(R.menu.member_task_menu)
        toolbar.setOnMenuItemClickListener{ menu->
            when(menu.itemId){
                R.id.sort_deadline ->{
                    recyclerView.removeAllViews()
                    taskList.clear()
                    alert_label.visibility =View.GONE
                    //getTaskList(UserInfo.user.uid,"status")
                    val list = database.collection(Database.TASK).whereEqualTo("userId",UserInfo.user.uid).get()
                    list.addOnSuccessListener {
                        for (document in it.documents){
                            val id = document.get("taskId") as String
                            val name = document.get("taskName") as String
                            val projectid = document.get("projectId") as String
                            val userid = document.get("userId") as String
                            val status = document.get("status") as String
                            val creattime = document.getDate("createTime")
                            val deadtime = document.getDate("deadline")
                            val description = document.get("description") as String
                            val username = UserInfo.user.userName
                            //taskList.add(Task(id,name,projectid,username,userid,status,creattime,deadtime,description))
                            val x = database.collection(Database.PROJECT).whereEqualTo("projectId",projectid).get()
                            x.addOnSuccessListener{
                                for(document in it.documents){
                                    val projectname = document.get("projectName") as String
                                    taskList.add(Task(id,name,projectid,username,userid,status,creattime,deadtime,description,projectname))
                                    taskList.sortBy { deadtime }
                                }
                                //异步
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }

                }
                R.id.completed_task->{
                    recyclerView.removeAllViews()
                    taskList.clear()
                    alert_label.visibility =View.GONE
                    //getTaskList(UserInfo.user.uid,"status")
                    database.collection(Database.TASK).whereEqualTo("userId",UserInfo.user.uid).whereEqualTo("status","Completed").get().addOnSuccessListener {
                        if (it.documents.size==0){
                            alert_label.visibility = View.VISIBLE
                        }else{
                        for (document in it.documents){
                            val id = document.get("taskId") as String
                            val name = document.get("taskName") as String
                            val projectid = document.get("projectId") as String
                            val userid = document.get("userId") as String
                            val status = document.get("status") as String
                            val creattime = document.getDate("createTime")
                            val deadtime = document.getDate("deadline")
                            val description = document.get("description") as String
                            val username = UserInfo.user.userName
                            //taskList.add(Task(id,name,projectid,username,userid,status,creattime,deadtime,description))
                            val x = database.collection(Database.PROJECT).whereEqualTo("projectId",projectid).get()
                            x.addOnSuccessListener{
                                for(document in it.documents){
                                    val projectname = document.get("projectName") as String
                                    taskList.add(Task(id,name,projectid,username,userid,status,creattime,deadtime,description,projectname))
                                }
                                //异步
                                adapter.notifyDataSetChanged()
                            }
                        }}
                    }
                }
                R.id.assigned_task->{
                    //getTaskList(UserInfo.user.uid,"status")
                    recyclerView.removeAllViews()
                    taskList.clear()
                    alert_label.visibility =View.GONE
                    //getTaskList(UserInfo.user.uid,"status")
                    database.collection(Database.TASK).whereEqualTo("userId",UserInfo.user.uid).whereEqualTo("status","Assigned").get().addOnSuccessListener {
                        if(it.documents.size==0){
                            alert_label.visibility = View.VISIBLE
                        }else{
                        for (document in it.documents){
                            val id = document.get("taskId") as String
                            val name = document.get("taskName") as String
                            val projectid = document.get("projectId") as String
                            val userid = document.get("userId") as String
                            val status = document.get("status") as String
                            val creattime = document.getDate("createTime")
                            val deadtime = document.getDate("deadline")
                            val description = document.get("description") as String
                            val username = UserInfo.user.userName
                            //taskList.add(Task(id,name,projectid,username,userid,status,creattime,deadtime,description))
                            val x = database.collection(Database.PROJECT).whereEqualTo("projectId",projectid).get()
                            x.addOnSuccessListener{
                                for(document in it.documents){
                                    val projectname = document.get("projectName") as String
                                    taskList.add(Task(id,name,projectid,username,userid,status,creattime,deadtime,description,projectname)!!)
                                }
                                //异步
                                adapter.notifyDataSetChanged()
                            }
                        }}}
                }
            }
            return@setOnMenuItemClickListener false
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