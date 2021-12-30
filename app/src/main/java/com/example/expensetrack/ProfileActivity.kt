package com.example.expensetrack

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetrack.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    //View Binding
    private lateinit var binding: ActivityProfileBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //FireBase Authentication
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Config action bar
        actionBar = supportActionBar!!
        actionBar.title = "Profile"

        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle click, logout
        binding.logoutBTN.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }
    }

    private fun checkUser() {
        //If user is logged in -> go to profile
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //user is logged in
            val email = firebaseUser.email
            binding.emailTV.text = email
        } else {
            //not logged in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}