package com.southampton.comp6239

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.southampton.comp6239.base.Database
import com.southampton.comp6239.base.Role
import com.southampton.comp6239.base.Status
import com.southampton.comp6239.base.UserInfo
import com.southampton.comp6239.bean.Project
import com.southampton.comp6239.bean.Task
import com.southampton.comp6239.bean.User
import com.southampton.comp6239.utils.DateUtil
import com.southampton.comp6239.utils.ManagerRecylerAdapter
import com.southampton.comp6239.utils.RecyclerAdapter

class ProjectDetailFragment : Fragment() {

    private lateinit var database: FirebaseFirestore
    lateinit var taskRecyclerView : RecyclerView
    lateinit var managerTaskAdapter : ManagerRecylerAdapter
    lateinit var taskAdapter : RecyclerAdapter
    var taskList : ArrayList<Task> = ArrayList()
    lateinit var memberRecyclerView : RecyclerView
    lateinit var memberAdapter : RecyclerAdapter
    var memberList : ArrayList<User> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = Firebase.firestore
        val rootView: View = inflater.inflate(R.layout.fragment_project_detail, container, false)
        //get project Id
        val projectId = requireArguments().getString("projectId")
        val managerNameView = rootView.findViewById<TextView>(R.id.project_detail_manager_name)
        val managerEmailView = rootView.findViewById<TextView>(R.id.manager_email)
        val projectNameView = rootView.findViewById<TextView>(R.id.project_detail_name)
        val projectDescriptionView = rootView.findViewById<TextView>(R.id.project_detail_description)
        val projectDeadlineView = rootView.findViewById<TextView>(R.id.project_detail_deadline)
        val projectCreateTimeView = rootView.findViewById<TextView>(R.id.project_detail_createtime)
        val status = rootView.findViewById<TextView>(R.id.project_detail_status)
        val searchView = rootView.findViewById<SearchView>(R.id.project_detail_search)
        taskRecyclerView = rootView.findViewById(R.id.project_detail_task_list)
        memberRecyclerView = rootView.findViewById(R.id.project_detail_member_list)

//        managerNameView.isEnabled = false
//        projectNameView.isEnabled = false
//        projectDescriptionView.isEnabled = false
//        projectDeadlineView.isEnabled = false

        if(UserInfo.user.role == Role.MEMBER){
            searchView.visibility = View.INVISIBLE
        }

        //add searchView submit listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                database.collection(Database.TASK).whereEqualTo("projectId",projectId).get()
                    .addOnSuccessListener{
                        taskList.clear()
                        if(!query.isNullOrBlank()){
                            for(document in it.documents){
                                val task = document.toObject(Task::class.java)
                                if(task!!.userName.contains(query)){
                                    taskList.add(task)
                                }
                            }
                        }
                        if(UserInfo.user.role.equals(Role.MEMBER)){
                            taskAdapter.notifyDataSetChanged()
                        }else{
                            managerTaskAdapter.notifyDataSetChanged()
                        }
                    }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if(p0?.length == 0){
                    if(UserInfo.user.role.equals(Role.MEMBER)){
                        taskAdapter = RecyclerAdapter(requireContext(), taskList)
                        database.collection(Database.TASK).whereEqualTo("projectId",projectId)
                            .whereEqualTo("userId",UserInfo.user.uid).whereEqualTo("status",Status.ONGING)
                            .get()
                            .addOnSuccessListener{
                                taskList.clear()
                                for(document in it.documents){
                                    val task = document.toObject(Task::class.java)
                                    taskList.add(task!!)
                                }
                                taskAdapter.notifyDataSetChanged()
                            }
                        taskRecyclerView.adapter= taskAdapter
                    }else{
                        managerTaskAdapter = ManagerRecylerAdapter(requireContext(),taskList)
                        database.collection(Database.TASK).whereEqualTo("projectId",projectId).get()
                            .addOnSuccessListener{
                                taskList.clear()
                                for(document in it.documents){
                                    val task = document.toObject(Task::class.java)
                                    taskList.add(task!!)
                                }
                                managerTaskAdapter.notifyDataSetChanged()
                            }
                        taskRecyclerView.adapter= managerTaskAdapter
                    }
                }
                return false
            }

        })

        memberAdapter = RecyclerAdapter(requireContext(),memberList)
        memberRecyclerView.adapter = memberAdapter
        //member information
        database.collection(Database.TASK).whereEqualTo("projectId",projectId).get()
            .addOnSuccessListener {
                memberList.clear()
                val tem = ArrayList<String>()
                for(document in it.documents){
                    val id = document.get("userId") as String
                    tem.add(id)
                }
                database.collection(Database.USER).whereIn("uid",tem).get().addOnSuccessListener {
                    for(document in it.documents){
                        val user = document.toObject(User::class.java)
                        memberList.add(user!!)
                    }
                    memberAdapter.notifyDataSetChanged()
                }

            }

        //tasks information
        database.collection(Database.PROJECT).document(projectId!!).get().addOnSuccessListener {
            val project = it.toObject(Project::class.java)
            projectNameView.setText(project?.projectName)
            //set manager info
            database.collection(Database.USER).document(project!!.userId).get().addOnSuccessListener {
                val mail = it.get("email") as String
                managerEmailView.setText(mail)
            }
            managerNameView.setText(project?.mangerName)
            projectDescriptionView.setText(project?.description)
            projectDeadlineView.setText(DateUtil.getStringDate(project?.deadline!!,DateUtil.YEAR_MONTH_DAY))
            projectCreateTimeView.setText(DateUtil.getStringDate(project?.createTime!!,DateUtil.YEAR_MONTH_DAY))
            if(project.status == Status.COMPLETED){
                status.setText(Status.PROJECT_COMPLETED)
            }else{
                status.setText(Status.PROJECT_ONGING)
            }
            //show different tasks list base on the roles
            if(UserInfo.user.role.equals(Role.MEMBER)){
                taskAdapter = RecyclerAdapter(requireContext(), taskList)
                database.collection(Database.TASK).whereEqualTo("projectId",projectId)
                    .whereEqualTo("userId",UserInfo.user.uid).whereEqualTo("status",Status.ASSIGNED)
                    .get()
                    .addOnSuccessListener{
                        taskList.clear()
                        for(document in it.documents){
                            val task = document.toObject(Task::class.java)
                            taskList.add(task!!)
                        }
                        taskAdapter.notifyDataSetChanged()
                    }
                taskRecyclerView.adapter= taskAdapter
            }else{
                managerTaskAdapter = ManagerRecylerAdapter(requireContext(),taskList)
                database.collection(Database.TASK).whereEqualTo("projectId",projectId).get()
                    .addOnSuccessListener{
                        taskList.clear()
                        for(document in it.documents){
                            val task = document.toObject(Task::class.java)
                            taskList.add(task!!)
                        }
                        managerTaskAdapter.notifyDataSetChanged()
                    }
                taskRecyclerView.adapter= managerTaskAdapter
            }
        }

        val toolbar = rootView.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener{
            requireActivity().getSupportFragmentManager().popBackStack()
        }

        return rootView
    }
}


