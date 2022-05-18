package com.southampton.comp6239

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.southampton.comp6239.base.Database
import com.southampton.comp6239.bean.Task
import com.southampton.comp6239.bean.User
import com.southampton.comp6239.utils.DateUtil
import com.southampton.comp6239.utils.ResultAdapter
import java.text.SimpleDateFormat
import java.util.*

class TaskAddFragment:Fragment() {

    val memberList = ArrayList<User>()
    private lateinit var database : FirebaseFirestore
    lateinit var memberRecyclerView : RecyclerView
    lateinit var memberAdapter : ResultAdapter
    var new_task = Task()
    var select_user = User()
    // 0 only add, 1 update
    var updateMode = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.frag_add_task, container, false)
        database = Firebase.firestore

        //val uid = requireArguments().getString("userId")
        //date selection:
        val dldate = rootView.findViewById<TextView>(R.id.deadline_date)
        val dltime = rootView.findViewById<TextView>(R.id.deadline_time)
        val selectdate = rootView.findViewById<Button>(R.id.select_date)
        memberRecyclerView = rootView.findViewById(R.id.search_list_result)
        memberAdapter = ResultAdapter(requireContext(),memberList)

        selectdate.setOnClickListener {
            val cal = java.util.Calendar.getInstance()
            var mYear = cal[Calendar.YEAR]
            var mMonth = cal[Calendar.MONTH]
            var mDay = cal[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(rootView.context,DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                mYear = year
                mMonth = month
                mDay = day
                val nDate ="${year}-${month+1}-${day}"
                dldate.setText(nDate)
            },mYear,mMonth,mDay
            )
            datePickerDialog.show()
        }
        //selecttime
        val selectTime = rootView.findViewById<Button>(R.id.select_time)
        selectTime.setOnClickListener {
            val cal = java.util.Calendar.getInstance()
            var mHour = cal[Calendar.HOUR]
            var mMin = cal[Calendar.MINUTE]
            val timePickerDialog = TimePickerDialog(rootView.context,TimePickerDialog.OnTimeSetListener { timePicker, hour, min ->
                mHour = hour
                mMin = min
                val mTime = "${hour}:${min}"
                dltime.setText(mTime)
            },mHour,mMin,true
            )
            timePickerDialog.show()
        }

        //searchMember:
        val searchview = rootView.findViewById<SearchView>(R.id.search_member)
        searchview.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query : String?): Boolean {
                Log.d("input",query.toString())
                val x = database.collection(Database.USER).whereEqualTo("role",1).whereEqualTo("email",query.toString()).get()
                x.addOnSuccessListener {
                    if(it.documents.size==0){
                        Log.d("xxx","no match found")
                        database.collection(Database.USER).whereEqualTo("role",1).whereEqualTo("skill",query.toString()).get().addOnSuccessListener {
                            if (it.documents.size ==0){
                                val alertinfo ="NO MATCH FOUND"
                            }else{
                                Log.d("xxx","xxx")
                                memberList.clear()
                                for (document in it.documents){
                                    val member = document.toObject(User::class.java)
                                    //memberList.clear()
                                    memberList.add(member!!)
                                }
                                memberAdapter.notifyDataSetChanged()
                            }
                        }
                    }else{
                        memberList.clear()
                        for (document in it.documents){
                            Log.d("yyy","yyy")
                            val member = document.toObject(User::class.java)
                            memberList.add(member!!)
                        }
                        memberAdapter.notifyDataSetChanged()
                    }

                    memberRecyclerView.adapter = memberAdapter
                    memberAdapter.setOnClickListener( object : ResultAdapter.ClickListener {
                        override fun onClick(user: User) {
                            //og.d("xxx",user.uid)
                            val member_name = rootView.findViewById<TextView>(R.id.member_name)
                            val member_mail = rootView.findViewById<TextView>(R.id.member_email)
                            val member_skill = rootView.findViewById<TextView>(R.id.member_skill)
                            member_name.setText(user.firstname+" "+user.lastname)
                            member_mail.setText(user.email)
                            member_skill.setText(user.skill)
                            select_user = user
                        }
                    })
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
        //create the task
        val create_task = rootView.findViewById<Button>(R.id.create_task)
        create_task.setOnClickListener {
               val taskdescription = rootView.findViewById<EditText>(R.id.task_detail_description)
               val taskname = rootView.findViewById<EditText>(R.id.task_name_add)
               val taskdate = rootView.findViewById<TextView>(R.id.deadline_date)
               val tasktime = rootView.findViewById<TextView>(R.id.deadline_time)
               val membermail = rootView.findViewById<TextView>(R.id.member_email)
               val memberName = rootView.findViewById<TextView>(R.id.member_name)
               val des = taskdescription.text.toString()
               val tname = taskname.text.toString()
               val tdate = taskdate.getText().toString()
               val ttime = tasktime.getText().toString()
               val mmail = membermail.getText().toString()
               val mname = memberName.text.toString()
               if(des!=""&&tname!=""&&tdate!=""&&ttime!=""&&mmail!="E-mail"){
                   new_task.taskName = tname
                   new_task.description = des
                   new_task.createTime = Date()
                   new_task.userName = mname
                   // set current time as tem Id
                   new_task.taskId = System.currentTimeMillis().toString()
                   val task_deadline = tdate +" "+ ttime
                   val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
                   val date = formatter.parse(task_deadline)
                   if(date.before(Date())){
                       Toast.makeText(rootView.context," Task can not be earlier than the current time",Toast.LENGTH_SHORT).show()
                       return@setOnClickListener
                   }
                   new_task.deadline = date
                   //add dialog
                   AlertDialog.Builder(context).apply {
                       setTitle("Create the task")
                       setMessage("Are you sure you want to add this task?")
                       setCancelable(false)
                       setPositiveButton("Yes"){ dialog,which->
                           Log.d("task",new_task.deadline.toString())
                           Log.d("user",select_user.skill.toString())
                           //tranfer the data to projectaddfragemnt
                           val fragment = requireActivity().supportFragmentManager.findFragmentByTag("addProject") as ProjectAddFragment
                           if(updateMode){
                               val oldId = new_task.userId
                               new_task.userId = select_user.uid
                               fragment.updateTask(select_user,oldId)
                           }else{
                               new_task.userId = select_user.uid
                               fragment.addTask(select_user,new_task)
                           }
                           requireActivity().supportFragmentManager.popBackStack()
                       }
                       setNegativeButton("Cancel"){
                               dialog,which->
                       }
                       show()
                   }
               }else{
                   Toast.makeText(rootView.context," Task can not be added before the form is filled out!",Toast.LENGTH_SHORT).show()
               }
        }
        //back to last fragement
        val toolbar = rootView.findViewById<Toolbar>(R.id.toolbar_detail)
        toolbar.setNavigationOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        if(arguments != null && requireArguments().getBoolean("updateMode")){
            updateMode = true
            new_task = requireArguments().getSerializable("task") as Task
            rootView.findViewById<EditText>(R.id.task_name_add).setText(new_task.taskName)
            rootView.findViewById<EditText>(R.id.task_detail_description).setText(new_task.description)
            rootView.findViewById<TextView>(R.id.deadline_date).text = DateUtil.getStringDate(new_task.deadline!!,DateUtil.YEAR_MONTH_DAY)
            rootView.findViewById<TextView>(R.id.deadline_time).text = DateUtil.getStringDate(new_task.deadline!!,DateUtil.HOUR_MINUTE_SECOND)
            database.collection(Database.USER).document(new_task.userId).get().addOnSuccessListener {
                val user = it.toObject(User::class.java) as User
                val member_name = rootView.findViewById<TextView>(R.id.member_name)
                val member_mail = rootView.findViewById<TextView>(R.id.member_email)
                val member_skill = rootView.findViewById<TextView>(R.id.member_skill)
                member_name.setText(user.firstname+" "+user.lastname)
                member_mail.setText(user.email)
                member_skill.setText(user.skill)
                select_user = user
            }
        }else{
            updateMode = false
        }

        return rootView
    }

}