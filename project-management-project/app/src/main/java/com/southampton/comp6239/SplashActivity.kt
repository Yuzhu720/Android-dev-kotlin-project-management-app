package com.southampton.comp6239

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.southampton.comp6239.base.Database
import com.southampton.comp6239.bean.User

class SplashActivity : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = Firebase.firestore
        setContentView(R.layout.activity_splash)
        signIn()

    }

    private fun signIn() {
        val start = System.currentTimeMillis()
        // [START sign in with email]
        val sharedPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email","default")
        val password = sharedPreferences.getString("password","default")
        auth.signInWithEmailAndPassword(email!!, password!!).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                database.collection(Database.USER).document(user!!.uid!!).get()
                    .addOnSuccessListener{ documentSnapshot  ->
                        val end = System.currentTimeMillis()
                        if(documentSnapshot != null && end-start < 2000){
                            val user = documentSnapshot.toObject<User>()
                            //jump to ProjectActivity
                            val sharedPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE)
                            val edit = sharedPreferences.edit()
                            edit.putString("email",email)
                            edit.putString("password",password)
                            edit.commit()
                            val intent = Intent(this, ProjectActivity::class.java)
                            intent.putExtra("user",user)
                            startActivity(intent)
                            finish()
                        }else{
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }.addOnFailureListener {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
            } else {
                // If sign in fails, display a message to the suer
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        // [END sign in with email]
    }

    override fun onKeyDown(keycode :Int,event : KeyEvent) : Boolean{
        if(keycode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true
        }
        return super.onKeyDown(keycode, event);
    }

}