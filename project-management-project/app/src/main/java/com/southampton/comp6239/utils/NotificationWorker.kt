package com.southampton.comp6239.utils

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class NotificationWorker(val context: Context, val params: WorkerParameters):Worker(context, params) {
    private var database: FirebaseFirestore = Firebase.firestore

    override fun doWork(): Result {
        var counter = 1
        database.collection("notification")
            .whereEqualTo("pop", false)
            .whereEqualTo("userId",inputData.getString("userId"))
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    NotificationHelper(context, counter++).createNotification(document.get("sender") as String
                        , document.get("content") as String)
                    database.collection("notification").document(document.get("notiId") as String)
                        .update("pop",true)
                        .addOnSuccessListener {
                            Log.d("success","You succeed on updating!")
                        }
                        .addOnFailureListener {
                            Log.d("failure","There is no new messages")
                        }
                }
            }
            .addOnFailureListener {
                Log.d("failure", "Error getting documents: ")
            }

        return Result.success()
    }
}