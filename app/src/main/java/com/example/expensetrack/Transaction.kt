package com.example.expensetrack

import java.util.*

data class Transaction(
    var item: String,
    var date: Date,
    var note: String,
    var amount: Double
) {

}