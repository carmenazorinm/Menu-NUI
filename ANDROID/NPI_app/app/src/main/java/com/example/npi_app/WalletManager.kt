package com.example.npi_app

import android.content.Context
import android.widget.Toast
import org.json.JSONException

import org.json.JSONObject

object WalletManager {
    var paymentDb: MutableMap<String, Map<String, Any>> = mutableMapOf()

    fun loadPaymentDb(context: Context) {
        if (paymentDb.isEmpty()) { // Solo cargar si no está ya inicializado
            val inputStream = context.resources.openRawResource(R.raw.payment_db)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            jsonObject.keys().forEach { key ->
                val payment = jsonObject.getJSONObject(key)
                val paymentMap = mutableMapOf<String, Any>()
                payment.keys().forEach { subKey ->
                    paymentMap[subKey] = payment[subKey]
                }
                paymentDb[key] = paymentMap
            }
        }
    }

    private fun updatePaymentStatus(token: String, status: String, user_id: Int) {
        paymentDb[token]?.let { payment ->
            if (payment["multipay"] == false) {
                val updatedPayment = payment.toMutableMap()
                updatedPayment["status"] = status
                updatedPayment["payed_by"] = user_id
                paymentDb[token] = updatedPayment
            }
        }
    }

    fun processPayment(token: String, user_id: Int) {
        if (paymentDb.containsKey(token)) {
            updatePaymentStatus(token, "PAID", user_id)
        } else {
            //Toast.makeText(context, "QR no válido: el pago no existe", Toast.LENGTH_SHORT).show()
        }
    }

    fun isPayable(token: String): Boolean {
        return paymentDb[token]?.get("status") == "AVAILABLE"
    }

    fun getPaymentData(token: String): Map<String, Any>? {
        return paymentDb[token]
    }

    fun existsPayment(token: String): Boolean {
        return paymentDb.containsKey(token)
    }
}
