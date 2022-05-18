package com.southampton.comp6239

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.southampton.comp6239.base.UserInfo
import com.southampton.comp6239.bean.User
import com.southampton.comp6239.utils.NotificationWorker
import java.util.concurrent.TimeUnit


class ProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        val extras = intent.extras
        val user = extras!!.get("user") as User
        UserInfo.user = user

        //Listen to the new messages
        val myWorkerRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
            .setInputData(
                workDataOf(
                    "userId" to UserInfo.user.uid
                )
            )
            .build()
        WorkManager.getInstance(this).enqueue(myWorkerRequest)

        val nvView = findViewById<BottomNavigationView>(R.id.nav_view)
        //navigate to the different fragment
        nvView.setOnItemSelectedListener { item ->
            //remove all fragment
            supportFragmentManager.popBackStackImmediate(R.id.nav_main_fragment,1)
            when(item.itemId){
                R.id.nav_project ->{
                    supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment,ProjectListFragment()).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.nav_task ->{
                    supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment,TaskListFragment()).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.nav_notifations ->{
                    supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment,NotificationListFragment()).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.nav_home ->{
                    supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment,HomeFragment()).commit()
                    return@setOnItemSelectedListener true
                }

            }
            false
        }
//        nvView.setOnItemReselectedListener {  }

        supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment,ProjectListFragment()).commit()
    }

    override fun onKeyDown(keycode :Int,event : KeyEvent) : Boolean{
        if(keycode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true
        }
        return super.onKeyDown(keycode, event);
    }

}