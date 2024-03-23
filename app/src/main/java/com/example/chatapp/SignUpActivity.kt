@file:Suppress("DEPRECATION")

package com.example.chatapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.chatapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var signUpPd: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)

        fireStore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        signUpPd = ProgressDialog(this)

        binding.signUpTextToSignIn.setOnClickListener{
            val intent = Intent(this,SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.signUpBtn.setOnClickListener{

            val name = binding.signUpEtName.text.toString()
            val email = binding.signUpEmail.text.toString()
            val pass = binding.signUpPassword.text.toString()


            if ( validateInput(name, email, pass) ){
                signUpUser(name, email, pass)
            }

        }

    }

    private fun signUpUser( name:String,email:String,pass:String ) {

        signUpPd.show()
        signUpPd.setMessage("Signing Up")

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    val defaultImageUrl = "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png"

                    if (userId != null) {
                        val userMap = hashMapOf(
                            "userid" to userId,
                            "username" to name,
                            "status" to "default",
                            "imageUrl" to defaultImageUrl
                        )

                        fireStore.collection("User").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                signUpPd.dismiss()
                                Toast.makeText(this, "Sign Up Success", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                signUpPd.dismiss()
                                Toast.makeText(this, "Sign Up Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("d", {e.message}.toString())
                            }
                    } else {
                        signUpPd.dismiss()
                        Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    signUpPd.dismiss()
                    Toast.makeText(this, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    Log.e("d", {task.exception?.message}.toString())
                }
            }

    }

    private fun validateInput(name: String, email: String, pass: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.signUpEtNameContainer.error = "Required"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.signUpEmailContainer.error = "Required"
            isValid = false
        }

        if (pass.isEmpty()) {
            binding.signUpPasswordContainer.error = "Required"
            isValid = false
        }

        return isValid
    }
}