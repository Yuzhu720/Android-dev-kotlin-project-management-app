package com.southampton.comp6239.utils

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.southampton.comp6239.base.Database

class MailUtil {

    companion object{
        val database = Firebase.firestore.collection(Database.MAIL)

        fun sendEmail(to : String, text : String, subject: String){
            val message = hashMapOf(
                "subject" to subject,
                "text" to text
            )
            val email = hashMapOf(
                "to" to to,
                "message" to message
            )
            database.add(email)
        }

    }
}