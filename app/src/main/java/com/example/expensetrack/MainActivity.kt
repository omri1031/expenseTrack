package com.example.expensetrack

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    //Toolbar
    private lateinit var toolbar: Toolbar

    //FireBase Authentication
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""


    //Progress Dialog
    private lateinit var progressDialog: ProgressDialog

    //RecyclerView
    private lateinit var transRV: RecyclerView
    private lateinit var dbref: DatabaseReference
    private lateinit var transactionsList: ArrayList<Transaction>
    private lateinit var tAdapter: TransactionAdapter

    //FloatingActionButton
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        checkUser()

        //Config Toolbar
        toolbar = findViewById<Toolbar>(R.id.toolbarTB)
        toolbar.setTitle(("Today's Message"))
//        binding.totalSpentTV.text =

        //Config RecyclerView
        transactionsList = ArrayList()
        tAdapter = TransactionAdapter(transactionsList)
        transRV = findViewById<RecyclerView>(R.id.transactionsRV)
        transRV.layoutManager = LinearLayoutManager(this)
        transRV.setHasFixedSize(true)
        transRV.adapter = tAdapter
        //get data from firebase
        getTransactionsData()

        //config progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //Config FloatingActionButton
        findViewById<FloatingActionButton>(R.id.FAB).setOnClickListener {
            Log.d("FAB", "addExpense: Click ")

            addExpense()
        }
    }

    private fun checkUser() {
        //If user is logged in -> go to profile
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //user is logged in
            email = firebaseUser.email!!
        } else {
            //not logged in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun addExpense() {
        Log.d("FAB", "addExpense: addExpense")
        val layoutInputView = layoutInflater.inflate(R.layout.layout_input, null)
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setView(layoutInputView)
        alertBuilder.setTitle("Add Action")
        val ad = alertBuilder.show()

        //Add categories to spinner
        val spinner = layoutInputView.findViewById<Spinner>(R.id.spinner)
        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.categories)
        )
//Click on cancel
        layoutInputView.findViewById<Button>(R.id.cancelBTN).setOnClickListener {
            ad.cancel()
        }
        //Click on Submit
        layoutInputView.findViewById<Button>(R.id.submitBTN).setOnClickListener {
            var amount =
                layoutInputView.findViewById<EditText>(R.id.amountET).getText().toString()
            var note =
                layoutInputView.findViewById<EditText>(R.id.noteET).getText().toString().trim()
            var category = spinner.getSelectedItem().toString();
            var type = "out"
            if (category == "Income")
                type = "in"
            if ((TextUtils.isEmpty(amount)) || (amount.toDouble() <= 0)) {
                layoutInputView.findViewById<EditText>(R.id.amountET).error =
                    "Please Enter Valid Number"
                return@setOnClickListener

            }
            if (category == "Select") {
                Toast.makeText(this, "Please Select Category", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            Log.d("Add Action", "addExpense: category: ${category} | type: ${type}")

            email = email.replace(".", "")
            email = email.replace("#", "")
            email = email.replace("$", "")
            email = email.replace("[", "")
            email = email.replace("]", "")
            dbref = FirebaseDatabase.getInstance().reference.child("$email")


            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val action = Transaction(type, sdf.format(Date()), note, amount.toDouble())
            dbref.setValue(action).addOnSuccessListener {
                Log.d("database", "addExpense: GOOD")
                ad.cancel()
            }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Something Went Wrong! Please try again...",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    Log.d("database", "addExpense: BAD")
                }
        }


    }

    private fun getTransactionsData() {
        dbref = FirebaseDatabase.getInstance().getReference("actions")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (transSnapshot in snapshot.children) {
                        val trans = transSnapshot.getValue(Transaction::class.java)
                        transactionsList.add(trans!!)
                    }
                    transRV.adapter = tAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}

