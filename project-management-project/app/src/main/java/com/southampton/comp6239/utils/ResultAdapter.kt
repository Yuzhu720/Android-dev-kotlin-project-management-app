package com.southampton.comp6239.utils

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.southampton.comp6239.R
import com.southampton.comp6239.TaskAddFragment
import com.southampton.comp6239.bean.User

class ResultAdapter(private val context: Context,val memberList: List<User>):
    RecyclerView.Adapter<ResultAdapter.ViewHolder>(){
    lateinit var clickListener: ClickListener
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val memberName : TextView = view.findViewById(R.id.member_name_item)
        val memberEmail : TextView = view.findViewById(R.id.member_email_item)
        val memberSkill: TextView = view.findViewById(R.id.member_skill_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result_list_item,parent,false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val member = memberList[position]
            clickListener.onClick(member)
            parent.visibility = View.GONE
        }
        return  viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = memberList[position]

        holder.memberName.text = member.firstname +"  "+ member.lastname
        holder.memberEmail.text = member.email
        holder.memberSkill.text = member.skill
    }
    override fun getItemCount() = memberList.size

    interface ClickListener{
        fun onClick(user : User)
    }

    fun setOnClickListener(listener: ClickListener) {
        this.clickListener = listener
    }

}
