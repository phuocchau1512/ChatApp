@file:Suppress("DEPRECATION")

package com.example.chatapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.databinding.ActivitySignInBinding
import com.example.chatapp.mvvm.SignInViewModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class SignInActivity : AppCompatActivity() {


    private lateinit var email : String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialogSignIn: ProgressDialog
    private lateinit var signInBinding: ActivitySignInBinding
    private lateinit var signInViewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signInBinding = DataBindingUtil.setContentView(this,R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        signInViewModel = ViewModelProvider(this)[SignInViewModel::class.java]

        // if user has logged in
        // user has to log in only one

        if ( auth.currentUser != null ) {

            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        progressDialogSignIn = ProgressDialog(this)

        signInBinding.signInTextToSignUp.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        // xử lý ẩn/hiện cho thanh mật khẩu

        signInBinding.editTextPasswordInputLayout.setEndIconOnClickListener {
            val isPasswordVisible =
                signInBinding.loginetpassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            signInBinding.loginetpassword.inputType = if (isPasswordVisible) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }

            signInBinding.loginetpassword.setSelection(signInBinding.loginetpassword.text.length)

        }

        signInBinding.loginButton.setOnClickListener{

            email = signInBinding.loginetemail.text.toString()
            password = signInBinding.loginetpassword.text.toString()

            if ( email.isEmpty() || password.isEmpty()  ){
                if ( email.isEmpty()  ) signInBinding.editTextEmailInputLayout.error = "Required"
                if ( password.isEmpty() ) signInBinding.editTextPasswordInputLayout.error="Required"
            }
            else {
                signInBinding.editTextEmailInputLayout.error = null
                signInBinding.editTextPasswordInputLayout.error= null
                signIn(email,password)
            }

        }

    }

    private fun signIn(email: String, password:String ){
        progressDialogSignIn.show()
        progressDialogSignIn.setMessage("Signing In")
        signInBinding.loginButton.isEnabled = false

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {

            if ( it.isSuccessful ){
                progressDialogSignIn.dismiss()
                startActivity(Intent(this,MainActivity::class.java))
            }
            else {
                progressDialogSignIn.dismiss()
                Toast.makeText(this,"Email or password is incorrect",Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener { exception ->

            when( exception ){

                is FirebaseAuthInvalidCredentialsException ->{
                    Toast.makeText(this,"Email or password is incorrect",Toast.LENGTH_SHORT).show()
                }
                is FirebaseNetworkException ->{
                    Toast.makeText(this,"Email or password is incorrect",Toast.LENGTH_SHORT).show()
                }
            }
        }

        signInBinding.loginButton.isEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialogSignIn.dismiss()
    }
}