package com.example.expensetrack

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetrack.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    //View Binding
    private lateinit var binding: ActivityLoginBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //Progress Dialog
    private lateinit var progressDialog: ProgressDialog

    //FireBase Authentication
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //config action bar
        actionBar = supportActionBar!!
        actionBar.title = "Login"

        //config progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setMessage("Logging in...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle click, login
        binding.loginBTN.setOnClickListener {
            validateData()
        }

        //open signup activity
        binding.signUpPromptTV.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()

        }

    }

    private fun validateData() {
        //get data
        email = binding.emailET.text.toString().trim()
        password = binding.passwordET.text.toString().trim()

        //validate credentials
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //Invalid email
            binding.emailET.error = "Invalid Email!"
        } else if (TextUtils.isEmpty(password)) {
            //No password entered
            binding.passwordET.error = "Please Enter Password"
        } else
            firebaseLogin()
    }

    private fun firebaseLogin() {
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            progressDialog.dismiss()
            val firebaseUser = firebaseAuth.currentUser
            val email = firebaseUser!!.email
            Toast.makeText(this, "Logged in as $email", Toast.LENGTH_SHORT).show()

            //after logging in transfer to profile
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        //If user is logged in -> go to profile
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //user is logged in
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}