package com.southampton.comp6239

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.southampton.comp6239.base.Database
import com.southampton.comp6239.bean.User
import com.southampton.comp6239.databinding.FragmentLoginBinding
import com.southampton.comp6239.viewModel.LoginViewModel

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,container,false)
        auth = Firebase.auth
        database = Firebase.firestore
        binding.lifecycleOwner = this
        binding.loginViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // [START initialize_auth]
        // Initialize Firebase Auth
        binding.signIn.setOnClickListener {
            val emailInput = viewModel.user.value!!.email
            val passwordInput = viewModel.user.value!!.password
            if (emailInput.isEmpty() ||
                passwordInput.isEmpty()) {
                Toast.makeText(context, "Please fill all fields first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            signIn()
        }
        binding.signUp.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,RegisterFragment()).addToBackStack(null).commit()
        }
        binding.forgetPassword.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,PassswordResetFragment()).addToBackStack(null).commit()
        }
    }



    private fun signIn() {
        // [START sign in with email]
        val email = viewModel.user.value!!.email
        val password = viewModel.user.value!!.password
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                database.collection(Database.USER).document(user!!.uid!!).get()
                    .addOnSuccessListener{ documentSnapshot  ->
                        if(documentSnapshot  != null){
                            val user = documentSnapshot.toObject<User>()
                            //jump to ProjectActivity
                            val sharedPreferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
                            val edit = sharedPreferences.edit()
                            edit.putString("email",email)
                            edit.putString("password",password)
                            edit.commit()
                            val intent = Intent(context, ProjectActivity::class.java)
                            intent.putExtra("user",user)
                            startActivity(intent)
                            requireActivity().finish()
                        }

                    }.addOnFailureListener {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(context, "Email or Password Incorrect", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // If sign in fails, display a message to the suer
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(context, "Email or Password Incorrect", Toast.LENGTH_SHORT).show()
            }
        }
        // [END sign in with email]
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

}