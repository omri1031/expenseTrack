package com.example.expensetrack

import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrack.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    //View Binding
    private lateinit var binding: ActivityMainBinding

    //Toolbar
    private lateinit var toolbar: Toolbar

    //RecyclerView
    private lateinit var transRV: RecyclerView
    private lateinit var dbref: DatabaseReference
    private lateinit var transactionsList: ArrayList<Transaction>
    private lateinit var tAdapter: TransactionAdapter


    //FloatingActionButton
    private lateinit var fab: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //Config View Binding
//        private lateinit var binding: ActivityMainBinding

        //Config Toolbar
        toolbar = binding.toolbarTB
        toolbar.setTitle(("Today's Message"))
//        binding.totalSpentTV.text =

        //Config RecyclerView
        transactionsList = ArrayList()
        tAdapter = TransactionAdapter(transactionsList)
        transRV = binding.transactionsRV
        transRV.layoutManager = LinearLayoutManager(this)
        transRV.setHasFixedSize(true)
        transRV.adapter = tAdapter
        //get data from firebase
        getTransactionsData()


        //Config FloatingActionButton
        binding.FAB.setOnClickListener {

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

