package com.southampton.comp6239

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.work.WorkManager
import com.southampton.comp6239.base.UserInfo

class HomeFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.home, container, false)
        rootView.findViewById<TextView>(R.id.user_last_name).text = UserInfo.user.lastname
        rootView.findViewById<TextView>(R.id.user_first_name).text =  UserInfo.user.firstname
        rootView.findViewById<TextView>(R.id.user_email).text = UserInfo.user.email
        rootView.findViewById<TextView>(R.id.user_skill).text = UserInfo.user.skill
        rootView.findViewById<TextView>(R.id.user_phone).text = UserInfo.user.phone
        if(UserInfo.user.role == 0){
            rootView.findViewById<TextView>(R.id.user_role).text = "Manager"
        }else{
            rootView.findViewById<TextView>(R.id.user_role).text = "Member"
        }

        val button = rootView.findViewById<Button>(R.id.log_out)
        button.setOnClickListener{
            val sharedPreferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
            val edit = sharedPreferences.edit()
            edit.clear().commit()
            WorkManager.getInstance(requireContext()).cancelAllWork()
            requireActivity().finish()
        }

        return rootView
    }
}