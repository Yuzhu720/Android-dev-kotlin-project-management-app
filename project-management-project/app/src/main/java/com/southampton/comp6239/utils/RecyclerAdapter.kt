package com.southampton.comp6239.utils

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.southampton.comp6239.ProjectDetailFragment
import com.southampton.comp6239.R
import com.southampton.comp6239.TaskAddFragment
import com.southampton.comp6239.TaskDetailFragment
import com.southampton.comp6239.base.Status
import com.southampton.comp6239.base.UserInfo
import com.southampton.comp6239.bean.Project
import com.southampton.comp6239.bean.Task
import com.southampton.comp6239.bean.User


class RecyclerAdapter(private val context: Context, private val dataList:List<Any>) :  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var database = Firebase.firestore
    //0 is detail, 1 is add
    var taskMode = 0

    // 内部类ViewHolder
    inner class ProjectViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        //绑定适配器ID获取适配器视图
        var projectNameView: TextView? = null
        var projectStatus: TextView? = null
        var projectDeadline: TextView? = null
        var projectButton: TextView? = null

        init {
            projectNameView = itemView.findViewById(R.id.item_project_name)
            projectStatus = itemView.findViewById(R.id.item_project_status)
            projectDeadline = itemView.findViewById(R.id.item_project_deadline)
            projectButton = itemView.findViewById(R.id.item_project_button)
        }
    }

    inner class TaskViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        //绑定适配器ID获取适配器视图
        var taskNameView: TextView? = null
        var taskDeadline: TextView? = null
        var taskButton: TextView? = null

        init {
            taskNameView = itemView.findViewById(R.id.item_task_name)
            taskDeadline = itemView.findViewById(R.id.item_task_deadline)
            taskButton = itemView.findViewById(R.id.item_task_button)
        }
    }

    inner class MemberViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        //绑定适配器ID获取适配器视图
        var memberNameView: TextView? = null
        var memberEmailView: TextView? = null
        init {
            memberNameView = itemView.findViewById(R.id.item_member_name)
            memberEmailView = itemView.findViewById(R.id.item_member_email)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            PROJECT ->{
                val itemView = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false)
                return ProjectViewHolder(itemView)
            }
            TASK ->{
                val itemView = LayoutInflater.from(context).inflate(R.layout.item_task_layout,parent,false)
                return TaskViewHolder(itemView)
            }
            else ->{
                val itemView = LayoutInflater.from(context).inflate(R.layout.item_member_layout,parent,false)
                return MemberViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ProjectViewHolder){
            val project = dataList.get(position) as Project
            holder.projectNameView!!.text = project.projectName
            database.collection("task").whereEqualTo("userId",UserInfo.user.uid).whereEqualTo("projectId",project.projectId).whereEqualTo("status",Status.TASK_ASSIGNED)
                .get().addOnSuccessListener {
                    if(it.documents.size!=0){
                        holder.projectNameView!!.setTextColor(Color.RED)
                    }
                }
            holder.projectStatus!!.text = project.status
            holder.projectDeadline!!.text = DateUtil.getStringDate(project.deadline!!,DateUtil.YEAR_MONTH_DAY)
            holder.projectButton!!.setOnClickListener {
                val projectDetailFragment = ProjectDetailFragment()
                val bundle = Bundle()
                bundle.putString("projectId", project.projectId)
                projectDetailFragment.setArguments(bundle)
                (context as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment, projectDetailFragment).addToBackStack(null).commit()
            }
        }else if(holder is TaskViewHolder){
            val task = dataList.get(position) as Task
            holder.taskNameView!!.text = task.taskName
            holder.taskDeadline!!.text = DateUtil.getStringDate(task.deadline!!,DateUtil.YEAR_MONTH_DAY)
            if(taskMode == 0){
                holder.taskButton!!.setOnClickListener {
                    val taskDetailFragment = TaskDetailFragment()
                    val bundle = Bundle()
                    bundle.putString("taskId", task.taskId)
                    taskDetailFragment.setArguments(bundle)
                    (context as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment, taskDetailFragment).addToBackStack(null).commit()
                }
            }else if(taskMode == 1){
                holder.taskButton!!.setOnClickListener {
                    val taskAddFragment = TaskAddFragment()
                    val bundle = Bundle()
                    bundle.putSerializable("task", task)
                    bundle.putBoolean("updateMode", true)
                    taskAddFragment.setArguments(bundle)
                    (context as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment, taskAddFragment).addToBackStack(null).commit()
                }
            }

        }else if(holder is MemberViewHolder){
            val user = dataList.get(position) as User
            holder.memberNameView!!.text = user.firstname + " " + user.lastname
            holder.memberEmailView!!.text = user.email
        }


//        val bind = DataBindingUtil.bind<ItemLayoutBinding>(holder.itemView)
//        bind!!.project = projectsList.get(position)
//        bind.executePendingBindings()
//        //点击事件
//        holder.itemView.setOnClickListener{
//
//            //跳转页面activity
//            val intent= Intent(holder.itemView.context,DetailActivity::class.java)
//            //把数据用activity带到下一级页面
//            intent.putExtra("url=",newsList[holder.adapterPosition].url)
//            //启动活动 跳转
//            holder.itemView.context.startActivity(intent)
//        }

    }

    override fun getItemViewType(position: Int): Int {
        val data = dataList.get(position)
        if(data is Project){
            return PROJECT
        }else if(data is Task){
            return TASK
        }else{
            return OTHER
        }

    }

    companion object{
        val PROJECT = 0
        val TASK = 1
        val OTHER = 2
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}