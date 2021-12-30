package com.example.expensetrack

data class Transaction(
    var item: String,
    var date: String,
    var note: String,
    var amount: Double
) {

}