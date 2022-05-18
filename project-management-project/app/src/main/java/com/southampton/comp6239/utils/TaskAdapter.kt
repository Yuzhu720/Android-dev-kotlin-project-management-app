package com.southampton.comp6239.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.southampton.comp6239.ProjectListFragment
import com.southampton.comp6239.R
import com.southampton.comp6239.TaskDetailFragment
import com.southampton.comp6239.TaskListFragment
import com.southampton.comp6239.bean.Task
import android.content.Context
import android.os.Bundle
import android.util.Log

class TaskAdapter(private val context: Context,val taskList: List<Task>):
    RecyclerView.Adapter<TaskAdapter.ViewHolder>(){

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val taskName : TextView = view.findViewById(R.id.taskName)
        val taskStatus : TextView = view.findViewById(R.id.taskStatus)
        val projectName: TextView = view.findViewById(R.id.projectName)
        val creatTime : TextView  = view.findViewById(R.id.task_creat_time)
        val deadline : TextView = view.findViewById(R.id.task_dealine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_item,parent,false)
        val viewHolder = ViewHolder(view)
        //set click event
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val taskDetailFragment = TaskDetailFragment()
            val task = taskList[position]
            //Toast.makeText(parent.context,"${task.taskId}",Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            //transfer the data from tasklisifragment to taskdetailfragemnt
            bundle.putString("taskId",task.taskId)
            taskDetailFragment.setArguments(bundle)
            (context as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment, taskDetailFragment).addToBackStack(null).commit()
        }
        return  viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskName.text = task.taskName
        holder.taskStatus.text = task.status
        holder.projectName.text = task.projectName
        holder.creatTime.text = DateUtil.getStringDate(task?.createTime!!,DateUtil.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
        holder.deadline.text = DateUtil.getStringDate(task?.deadline!!,DateUtil.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    }


    override fun getItemCount() = taskList.size
}
