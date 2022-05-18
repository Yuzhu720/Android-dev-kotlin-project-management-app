package com.southampton.comp6239.utils

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.southampton.comp6239.R
import com.southampton.comp6239.TaskDetailFragment
import com.southampton.comp6239.bean.Task

class ManagerRecylerAdapter(private val context: Context, private val dataList:List<Any>) :  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class taskViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        //绑定适配器ID获取适配器视图
        var taskNameView: TextView? = null
        var taskUserNameView: TextView? = null
        var taskStatusView: TextView? = null
        var taskButton: TextView? = null

        init {
            taskNameView = itemView.findViewById(R.id.item_task_name_manager)
            taskUserNameView = itemView.findViewById(R.id.item_task_username_manager)
            taskStatusView = itemView.findViewById(R.id.item_task_status_manager)
            taskButton = itemView.findViewById(R.id.item_task_button_manager)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_task_layout_manager,parent,false)
        return taskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as taskViewHolder
        val task = dataList.get(position) as Task
        holder.taskNameView!!.text = task.taskName
        holder.taskUserNameView!!.text = task.userName
        holder.taskStatusView!!.text = task.status
        holder.taskButton!!.setOnClickListener {
            val taskDetailFragment = TaskDetailFragment()
            val bundle = Bundle()
            bundle.putString("taskId", task.taskId)
            taskDetailFragment.setArguments(bundle)
            (context as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment, taskDetailFragment).addToBackStack(null).commit()
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}