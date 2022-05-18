package com.southampton.comp6239

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.southampton.comp6239.base.OnClickEvent
import com.southampton.comp6239.databinding.FragmentLoginBinding
import com.southampton.comp6239.databinding.FragmentRegisterBinding
import com.southampton.comp6239.databinding.FragmentResetpasswordBinding
import com.southampton.comp6239.viewModel.LoginViewModel

class PassswordResetFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentResetpasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_resetpassword,container,false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.resetSend.setOnClickListener(object : OnClickEvent() {
            override fun singleClick(v: View?) {
                sendPasswordReset()
            }
        })
    }

    private fun sendPasswordReset() {
        // [START send_password_reset]
        val emailAddress = binding.resetEmail.text.toString()

        auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Email sent", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Email sent.")
                }else{
                    Toast.makeText(context, "Email is not right", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, task.exception.toString())
                }
            }
    }


//    private fun sendEmailVerification() {
//        // [START send_email_verification]
//        val user = Firebase.auth.currentUser
//
//        user!!.sendEmailVerification()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "Email sent.")
//                }
//            }
//        // [END send_email_verification]
//    }
//
//    private fun reauthenticate() {
//        // [START reauthenticate]
//        val user = Firebase.auth.currentUser!!
//
//        // Get auth credentials from the user for re-authentication. The example below shows
//        // email and password credentials but there are multiple possible providers,
//        // such as GoogleAuthProvider or FacebookAuthProvider.
//        val credential = EmailAuthProvider
//            .getCredential(binding.resetEmail.text.toString(), "password1234")
//
//        // Prompt the user to re-provide their sign-in credentials
//        user.reauthenticate(credential)
//            .addOnCompleteListener { Log.d(TAG, "User re-authenticated.") }
//        // [END reauthenticate]
//    }

    companion object {
        private const val TAG = "ResetPassword"
    }
}