package com.example.expensetrack

data class Transaction(
    var type: String? = null,
    var date: String? = null,
    var note: String? = null,
    var category: String? = null,
    var amount: Double? = null
) {

}