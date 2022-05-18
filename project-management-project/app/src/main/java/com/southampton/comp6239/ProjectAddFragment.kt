package com.southampton.comp6239

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.southampton.comp6239.base.Database
import com.southampton.comp6239.base.Status
import com.southampton.comp6239.base.UserInfo
import com.southampton.comp6239.bean.Project
import com.southampton.comp6239.bean.Task
import com.southampton.comp6239.bean.User
import com.southampton.comp6239.utils.DateUtil
import com.southampton.comp6239.utils.RecyclerAdapter
import java.util.*

class ProjectAddFragment : Fragment() {

    private lateinit var database: FirebaseFirestore
    lateinit var memberRecyclerView : RecyclerView
    lateinit var memberAdapter : RecyclerAdapter
    lateinit var  taskRecyclerView : RecyclerView
    lateinit var taskAdapter : RecyclerAdapter
    var taskList: ArrayList<Task> = ArrayList()
    var memberList: ArrayList<User> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_add_project, container, false)
        database = Firebase.firestore
        memberRecyclerView = rootView.findViewById(R.id.project_add_member_list)
        taskRecyclerView = rootView.findViewById(R.id.project_add_task_list)
        memberAdapter = RecyclerAdapter(requireContext(),memberList)
        taskAdapter = RecyclerAdapter(requireContext(),taskList)
        memberRecyclerView.adapter = memberAdapter
        taskRecyclerView.adapter = taskAdapter
        //set as update mode
        taskAdapter.taskMode = 1

        val name = rootView.findViewById<TextView>(R.id.project_add_name)
        val description = rootView.findViewById<TextView>(R.id.project_add_description)
        val deadline = rootView.findViewById<DatePicker>(R.id.project_add_deadline)
        val calendar = Calendar.getInstance()
        deadline.minDate = calendar.timeInMillis

        //add project
        val button = rootView.findViewById<Button>(R.id.project_add_button)
        button.setOnClickListener{
            if(taskList.isEmpty()){
                Toast.makeText(requireContext(),"Can not create project without tasks", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(name.text.toString().isNullOrBlank()){
                Toast.makeText(requireContext(),"Project name can not be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(context).apply {
                setTitle("Create the project")
                setMessage("Are you sure you want to create this project?")
                setCancelable(false)
                setPositiveButton("Yes"){ dialog,which->
                    val project = Project()
                    project.createTime = Date()
                    project.deadline = DateUtil.parseToDate(deadline.year,deadline.month,deadline.dayOfMonth)
                    project.description = description.text.toString()
                    project.projectName = name.text.toString()
                    project.status = Status.ONGING
                    project.userId = UserInfo.user.uid
                    project.mangerName = UserInfo.user.firstname + " " + UserInfo.user.lastname
                    createProject(taskList,memberList,project)
                }
                setNegativeButton("Cancel"){
                        dialog,which->
                }
                show()
            }
        }

        val toolbar = rootView.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener{
            requireActivity().getSupportFragmentManager().popBackStack()
        }
        //add task
        val add_task = rootView.findViewById<Button>(R.id.project_detail_add_task_button)
        add_task.setOnClickListener {
            val taskaddFragemnt= TaskAddFragment()
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment,taskaddFragemnt).addToBackStack(null).commit()
        }
        return rootView
    }

    fun createProject(taskList : List<Task>,memberList : List<User>, project : Project){
        val projectRef = database.collection(Database.PROJECT).document()
        project.projectId = projectRef.id
        for(t in taskList){
            if(project.deadline!!.before(t.deadline)){
                Toast.makeText(requireContext(),"Task can not end later than project deadline", Toast.LENGTH_LONG).show()
                return
            }
        }

        database.runBatch{ batch ->
            batch.set(projectRef,project)
            for(user in memberList){
                val utpRef = database.collection(Database.USER_TO_PROJECT).document(user.uid)
                batch.update(utpRef,"list", FieldValue.arrayUnion(projectRef.id))
            }
            for(t in taskList){
                val taskRef = database.collection(Database.TASK).document()
                t.taskId = taskRef.id
                t.projectId = project.projectId
                t.projectName = project.projectName
                batch.set(taskRef,t)
                //add notification
                val newNotiRef = database.collection("notification").document()
                val notiData = hashMapOf(
                    "notiId" to newNotiRef.id,
                    "time" to Date(),
                    "pop" to false,
                    "read" to false,
                    "sender" to "Task Assigned Already",
                    "content" to "You have been assigned a task ${t.taskName} in Project ${t.projectName}",
                    "userId" to t.userId
                )
                newNotiRef.set(notiData)
            }
            val utpRef = database.collection(Database.USER_TO_PROJECT).document(UserInfo.user.uid)
            batch.update(utpRef,"list", FieldValue.arrayUnion(projectRef.id))
        }.addOnCompleteListener  {
            Toast.makeText(requireContext(),"successful", Toast.LENGTH_LONG).show()
            requireActivity().getSupportFragmentManager().popBackStack()
        }.addOnFailureListener{
            Toast.makeText(requireContext(),"error", Toast.LENGTH_LONG).show()
        }
    }

    fun addTask(user : User, task: Task){
        if(!memberList.contains(user)){
            memberList.add(user)
        }
        taskList.add(task)
    }

    fun updateTask(user : User, oldUserId : String){
        val iterator = taskList.iterator()
        //count how many task assigned to old user
        var count = 0
        while(iterator.hasNext()){
            val t = iterator.next()
            if(t.userId.equals(oldUserId)){
                count++
            }
        }
        if(count == 0){
            val user = User()
            user.uid = oldUserId
            memberList.remove(user)
        }
        if(!memberList.contains(user)){
            memberList.add(user)
        }
    }

}