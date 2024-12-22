package com.example.npi_app

object WalletData {
    val userId: Int = 0 // Por ejemplo
    var saldo: Double =500.0
    var puntos: Int = 5000

    var discounts: MutableList<Map<String, Any>> = mutableListOf()

    var transactions: MutableList<Map<String, Any>> = mutableListOf()

    var vouchers: MutableList<Map<String, Any>> = mutableListOf()
}