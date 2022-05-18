package com.southampton.comp6239


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
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
import com.southampton.comp6239.bean.Project
import com.southampton.comp6239.utils.NotificationWorker
import com.southampton.comp6239.utils.RecyclerAdapter
import java.util.concurrent.TimeUnit


class ProjectListFragment : Fragment(){
    //database connection
    private lateinit var database: FirebaseFirestore
    lateinit var recyclerView : RecyclerView
    lateinit var adapter : RecyclerAdapter
    lateinit var projectList : ArrayList<Project>
    lateinit var alert : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_project_list, container, false)
        //database init
        database = Firebase.firestore
        recyclerView= rootView.findViewById(R.id.project_list)
        alert = rootView.findViewById(R.id.alert_label)
        projectList = ArrayList()

        database.collection(Database.USER_TO_PROJECT).document(UserInfo.user.uid).get()
            .addOnSuccessListener { documentSnapshot  ->
                //get project Id based on user id
                val projectIdList  = documentSnapshot.get("list")
                //if projectIdList not null
                if(projectIdList!= null && (projectIdList!! as ArrayList<String>).size != 0 ){
                    database.collection(Database.PROJECT).whereIn("projectId",projectIdList as ArrayList<String>).get()
                        .addOnSuccessListener {
                            projectList.clear()
                            //get project information based on project id
                            for(document in it.documents){
                                val project = document.toObject(Project::class.java)
                                projectList.add(project!!)
                            }
                            //
                            adapter.notifyDataSetChanged()
                        }
                }
                //set adapter
                adapter = RecyclerAdapter(requireContext(), projectList)
                recyclerView.adapter = adapter
            }

        val toolbar = rootView.findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.projectmenu)
        val searchView = toolbar.menu.findItem(R.id.search).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                getProjectList(UserInfo.user.uid,searchView.query,Status.ALL,"")
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if(p0!!.length == 0){
                    getProjectList(UserInfo.user.uid,searchView.query,Status.ALL,"")
                }
                return false
            }
        })

        toolbar.setOnMenuItemClickListener{ menu->
            when(menu.itemId){
                R.id.add_project ->  {
                    if(UserInfo.user.role.equals(Role.MEMBER)){
                        Toast.makeText(requireContext(), "you are not manager", Toast.LENGTH_SHORT).show()
                    }else{
                        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment,ProjectAddFragment(),"addProject").addToBackStack(null).commit()
                        return@setOnMenuItemClickListener false
                    }
                }
                R.id.sort_deadline ->{
                    getProjectList(UserInfo.user.uid,searchView.query,Status.ALL,"deadline")
                }
                R.id.completed_project->{
                    getProjectList(UserInfo.user.uid,searchView.query,Status.COMPLETED,"")
                }
                R.id.ongoing_project->{
                    getProjectList(UserInfo.user.uid,searchView.query,Status.ONGING,"")
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

    fun getProjectList(userId : String, search : CharSequence, status : String, sort: String){
        database.collection(Database.USER_TO_PROJECT).document(userId).get().addOnSuccessListener { documentSnapshot  ->
            val projectIdList  = documentSnapshot.get("list")
            if(projectIdList!= null && (projectIdList!! as ArrayList<String>).size != 0 ){
                var refer = database.collection(Database.PROJECT).whereIn("projectId",projectIdList as ArrayList<String>)
                if(!status.equals(Status.ALL)) {
                    refer = refer.whereEqualTo("status", status)
                }
                refer.get().addOnSuccessListener {
                    projectList.clear()
                    for(document in it.documents){
                        val project = document.toObject(Project::class.java)
                        if(!search.isNullOrBlank()){
                            if(project!!.projectName.contains(search)){
                                projectList.add(project!!)
                            }
                        }else{
                            projectList.add(project!!)
                        }
                    }
                    if(!sort.isNullOrBlank()){
                        projectList.sortBy { it.deadline }
                    }
                    if(projectList.isEmpty()) alert.visibility = View.VISIBLE
                    else alert.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

}