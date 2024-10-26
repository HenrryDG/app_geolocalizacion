package com.example.app_geolocalizacion
import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SMSReceiver"
        private const val SMS_PERMISSION_CODE = 1
    }

    private lateinit var textViewMessage: TextView
    private lateinit var smsReceiver: SmsReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "MainActivity onCreate")

        textViewMessage = findViewById(R.id.textViewMessage)
        textViewMessage.text = "Iniciando app..."

        smsReceiver = SmsReceiver()

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, SMS_PERMISSION_CODE)
        } else {
            setupSmsReceiver()
        }
    }

    private fun setupSmsReceiver() {
        Log.d(TAG, "Setting up SMS receiver")

        // Configurar el listener para actualizar el TextView
        SmsReceiver.messageListener = { message ->
            runOnUiThread {
                textViewMessage.text = message
            }
        }

        // Registrar el receiver con alta prioridad
        val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        intentFilter.priority = 999
        registerReceiver(smsReceiver, intentFilter)

        textViewMessage.text = "Esperando mensajes..."
        Log.d(TAG, "SMS receiver setup completed")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                setupSmsReceiver()
            } else {
                textViewMessage.text = "No se pueden recibir mensajes: Permisos denegados"
                Toast.makeText(this, "Se necesitan permisos para recibir SMS", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(smsReceiver)
            Log.d(TAG, "SMS receiver unregistered")
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver", e)
        }
    }
}