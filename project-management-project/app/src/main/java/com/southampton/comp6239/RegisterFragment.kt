package com.southampton.comp6239

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.southampton.comp6239.base.Database
import com.southampton.comp6239.base.OnClickEvent
import com.southampton.comp6239.databinding.FragmentRegisterBinding
import com.southampton.comp6239.viewModel.LoginViewModel
import java.io.ByteArrayOutputStream


class RegisterFragment : Fragment(){

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var storageRef: StorageReference

//    private val permissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()) { isGranted ->
//        if (isGranted) {
//            selectImageFromGallery()
//        }else{
//            Toast.makeText(context, "can not get access to camera", Toast.LENGTH_SHORT).show()
//        }
//    }
    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.photo.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register,container,false)
        auth = Firebase.auth
        storageRef = Firebase.storage.getReference("user")
        binding.lifecycleOwner = this
        binding.loginViewModel = viewModel
        database = Firebase.firestore
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.register.setOnClickListener(object : OnClickEvent() {
            override fun singleClick(v: View?) {
                createAccount()
            }
        })
        binding.photo.setOnClickListener {
            selectImageFromGallery()
        }

        binding.role.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0){
                    binding.skills.visibility = View.INVISIBLE
                    binding.skill.visibility = View.INVISIBLE
                }else{
                    binding.skills.visibility = View.VISIBLE
                    binding.skill.visibility = View.VISIBLE
                }
            }

        }


    }

    private fun selectImageFromGallery(){
        selectImageFromGalleryResult.launch("image/*")
//        if(ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
//            selectImageFromGalleryResult.launch("image/*")
//        }else{
//            permissionLauncher.launch(Manifest.permission.CAMERA)
//        }
    }

    private fun createAccount(){
        val email = viewModel.user.value!!.email
        val password = viewModel.user.value!!.password
        val skill = binding.skills.selectedItem.toString()
        val role = binding.role.selectedItemPosition
        val confirm_password = binding.confirmPassword.text.toString()
        Log.d(TAG, "createAccount:$email")
        Log.d(TAG, confirm_password)

        // [START create user with email
        if (email.isEmpty() ||
            password.isEmpty() || viewModel.user.value!!.firstname.isEmpty()
            || viewModel.user.value!!.lastname.isEmpty() || viewModel.user.value!!.phone.isEmpty() ||confirm_password.isEmpty()) {
            Toast.makeText(context, "Please fill all fields first", Toast.LENGTH_SHORT).show()
            return
        }else if(!password.equals(confirm_password)){
            Toast.makeText(context, "Please check password", Toast.LENGTH_SHORT).show()
            return
        }else if(!isValid(password)){
            Toast.makeText(context, "The password requires at least one uppercase letter, one special character and one number, and the length is not less than 8", Toast.LENGTH_LONG).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                // Create user information
                val user = auth.currentUser
                //insert user information and init userToproject table
                viewModel.user.value!!.uid = user!!.uid
                viewModel.user.value!!.skill = skill
                viewModel.user.value!!.role = role
                database.runBatch{ batch ->
                    val userToproject = hashMapOf(
                        "list" to ArrayList<String>()
                    )
                    batch.set(database.collection(Database.USER).document(user!!.uid),viewModel.user.value!!)
                    batch.set(database.collection(Database.USER_TO_PROJECT).document(user!!.uid),userToproject)
                }.addOnCompleteListener  {
                    // Get the data from an ImageView as bytes
                    val bitmap = ( binding.photo.drawable as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    // Save to firebase
                    var uploadTask = storageRef.child(user.uid).putBytes(data)
                    uploadTask.addOnSuccessListener {
                        Log.d(TAG, "createUserWithEmail:success")
                        Toast.makeText(requireActivity(), "Account Created", Toast.LENGTH_SHORT).show()
                        requireActivity().getSupportFragmentManager().popBackStack()
                    }.addOnFailureListener{
                        Toast.makeText(requireContext(),"error",Toast.LENGTH_LONG)
                        user.delete()
                    }
                }.addOnFailureListener{
                    Toast.makeText(requireContext(),"error",Toast.LENGTH_LONG)
                    user.delete()
                }
            }
            else {
                // If sign in fails, display a message to the user
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(requireActivity(), task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "EmailRegister"
    }

    fun isValid(password : String):Boolean{
        if(password.length < 8) return false
        if(password.filter { it.isDigit() }.firstOrNull() == null) return false
        if(password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if(password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

        return true
    }
}