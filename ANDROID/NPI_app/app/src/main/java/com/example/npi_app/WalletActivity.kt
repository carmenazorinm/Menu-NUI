package com.example.npi_app

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
//import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.npi_app.RedeemItem
import kotlinx.coroutines.launch

class WalletActivity : BaseActivity() {
    // Wallet state
    private var walletBalance by mutableStateOf(100.0)
    private var fidelityPoints by mutableStateOf(20)
    private val vouchers = mutableStateListOf<String>()
    private val paymentHistory = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalletApp()
        }
    }

    @Composable
    fun WalletApp() {
        var currentScreen by remember { mutableStateOf("home") }
        var qrData by remember { mutableStateOf("") }

        when (currentScreen) {
            "home" -> WalletHomeScreen(
                walletBalance = walletBalance,
                fidelityPoints = fidelityPoints,
                onScanQR = { currentScreen = "scanQR" },
                onRedeemPoints = { currentScreen = "redeem" },
                onVouchers = { currentScreen = "vouchers" },
                onPaymentHistory = { currentScreen = "history" }
            )
            "scanQR" -> QRScanScreen(
                onScanComplete = { scannedData ->
                    qrData = scannedData
                    currentScreen = "payment"
                }
            )
            "payment" -> PaymentScreen(
                qrData = qrData,
                walletBalance = walletBalance,
                fidelityPoints = fidelityPoints,
                onPay = { amount ->
                    lifecycleScope.launch {
                        confirmPayment(amount)
                        currentScreen = "home"
                    }
                }
            )
            "redeem" -> RedeemPointsScreen(
                fidelityPoints = fidelityPoints,
                onRedeem = { item, cost ->
                    confirmRedeem(item, cost)
                    currentScreen = "home"
                }
            )
            "vouchers" -> VouchersScreen(vouchers)
            "history" -> PaymentHistoryScreen(paymentHistory)
        }
    }

    private fun confirmPayment(amount: Double) {
        if (walletBalance >= amount) {
            walletBalance -= amount
            fidelityPoints += (amount / 10).toInt()
            paymentHistory.add("Paid $amount")
            showMessage("Payment Successful")
        } else {
            showMessage("Insufficient Balance")
        }
    }

    private fun confirmRedeem(item: String, cost: Int) {
        if (fidelityPoints >= cost) {
            fidelityPoints -= cost
            vouchers.add(item)
            showMessage("Redeemed $item")
        } else {
            showMessage("Insufficient Points")
        }
    }

    private fun showMessage(message: String) {
        // Snackbar or Toast
    }
}

@Composable
fun WalletHomeScreen(
    walletBalance: Double,
    fidelityPoints: Int,
    onScanQR: () -> Unit,
    onRedeemPoints: () -> Unit,
    onVouchers: () -> Unit,
    onPaymentHistory: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Wallet Balance: $walletBalance",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Fidelity Points: $fidelityPoints",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            ImageButton(imageRes = R.drawable.ic_scan_qr, label = "Scan QR", onClick = onScanQR)
            ImageButton(imageRes = R.drawable.ic_redeem, label = "Redeem Points", onClick = onRedeemPoints)
            ImageButton(imageRes = R.drawable.ic_voucher, label = "Vouchers", onClick = onVouchers)
            ImageButton(imageRes = R.drawable.ic_history, label = "History", onClick = onPaymentHistory)
        }
    }
}

@Composable
fun ImageButton(imageRes: Int, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(8.dp)
    ) {
        Image(painter = painterResource(id = imageRes), contentDescription = null, modifier = Modifier.size(48.dp))
        Text(text = label, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun QRScanScreen(onScanComplete: (String) -> Unit) {
    // CameraX setup to scan QR
    // Use BarcodeScanner to detect QR and return result
    onScanComplete("dummy-qr-data")
}

@Composable
fun PaymentScreen(qrData: String, walletBalance: Double, fidelityPoints: Int, onPay: (Double) -> Unit) {
    var amount by remember { mutableStateOf(0.0) }
    Column {
        Text(text = "Payment Info: $qrData")
        Text(text = "Wallet Balance: $walletBalance")
        Text(text = "Fidelity Points: $fidelityPoints")
        Button(onClick = { onPay(amount) }) {
            Text("Confirm Payment")
        }
    }
}

@Composable
fun RedeemPointsScreen(fidelityPoints: Int, onRedeem: (String, Int) -> Unit) {
    LazyColumn {
        items(listOf("Voucher 1" to 10, "Discount 2" to 15)) { item ->
            Row {
                Text(text = item.first)
                Button(onClick = { onRedeem(item.first, item.second) }) {
                    Text("Redeem for ${item.second}")
                }
            }
        }
    }
}

@Composable
fun VouchersScreen(vouchers: List<String>) {
    LazyColumn {
        items(vouchers) { voucher ->
            Text(text = "Voucher: $voucher")
        }
    }
}

@Composable
fun PaymentHistoryScreen(history: List<String>) {
    LazyColumn {
        items(history) { entry ->
            Text(text = entry)
        }
    }
}

class RedeemItemsAdapter(
    private val items: List<RedeemItem>,
    private val onItemClick: (RedeemItem) -> Unit
) : RecyclerView.Adapter<RedeemItemsAdapter.RedeemItemViewHolder>() {

    class RedeemItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvItemTitle)
        val tvCost: TextView = view.findViewById(R.id.tvItemCost)
        val ivImage: ImageView = view.findViewById(R.id.ivItemImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedeemItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_redeem, parent, false)
        return RedeemItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RedeemItemViewHolder, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.title
        holder.tvCost.text = holder.itemView.context.getString(R.string.item_cost, item.cost)

        // Cargar imagen si existe
        if (item.image != null) {
            val context = holder.itemView.context
            val resId = context.resources.getIdentifier(item.image, "drawable", context.packageName)

            if (resId != 0) {
                holder.ivImage.setImageResource(resId)
            } else {
                holder.ivImage.visibility = View.GONE // Si no se encuentra, ocultar
            }
        } else {
            holder.ivImage.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }


    override fun getItemCount(): Int = items.size
}