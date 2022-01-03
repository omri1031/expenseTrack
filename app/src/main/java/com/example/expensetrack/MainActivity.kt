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


class MainActivity : AppCompatActivity() {
    var totalSum: Double = 0.0

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
//        toolbar = findViewById<Toolbar>(R.id.toolbarTB)
//        toolbar.setTitle(("Today's Message"))

        //Config RecyclerView
        transactionsList = arrayListOf<Transaction>()
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
            //Modify email for DB use
            email = email.replace(".", "")
            email = email.replace("#", "")
            email = email.replace("$", "")
            email = email.replace("[", "")
            email = email.replace("]", "")
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

            dbref = FirebaseDatabase.getInstance().reference.child("$email")


            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val action =
                Transaction(type, sdf.format(Date()), note, category, amount.toDouble())
            dbref.push().setValue(action).addOnSuccessListener {
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
        dbref = FirebaseDatabase.getInstance().getReference("$email")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                transactionsList.clear()
                totalSum = 0.0
                if (snapshot.exists()) {
                    for (transSnapshot in snapshot.children) {
                        Log.d("getTransactionsData", "onDataChange: $transSnapshot")
                        val trans = transSnapshot.getValue(Transaction::class.java)
                        transactionsList.add(0, trans!!)
                        //update Total amount
                        if (trans.type == "in")
                            totalSum += trans.amount!!
                        else
                            totalSum -= trans.amount!!
                    }

                    transRV.adapter = TransactionAdapter(transactionsList)
                    findViewById<TextView>(R.id.totalSpentTV)
                        .setText("Total Spendings: $" + totalSum.toString())

                    //Create Foreground Service with Total Amount
                    Log.d("Services", "getTransactionsData: Got Data now to service")
                    val myIntent = Intent(this@MainActivity, MyForegroundService::class.java)
                    myIntent.putExtra("totalSum", totalSum)
                    startService(myIntent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })

    }
}

