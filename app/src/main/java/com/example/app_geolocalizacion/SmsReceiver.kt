package com.example.app_geolocalizacion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "SMSReceiver"
        var messageListener: ((String) -> Unit)? = null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "SmsReceiver - onReceive called")

        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            Log.d(TAG, "SMS received in SmsReceiver")

            // Importante: No abortar el broadcast para que otras apps también lo reciban
            clearAbortBroadcast()

            val bundle = intent.extras
            if (bundle != null) {
                try {
                    val pdus = bundle["pdus"] as Array<*>
                    val messages = pdus.map { pdu ->
                        val format = bundle.getString("format")
                        if (format != null) {
                            SmsMessage.createFromPdu(pdu as ByteArray, format)
                        } else {
                            @Suppress("DEPRECATION")
                            SmsMessage.createFromPdu(pdu as ByteArray)
                        }
                    }

                    val fullMessage = buildString {
                        append("Mensajes recibidos:\n")
                        messages.forEach { sms ->
                            append("\nDe: ${sms.originatingAddress}")
                            append("\nMensaje: ${sms.messageBody}\n")
                            Log.d(TAG, "Mensaje de: ${sms.originatingAddress}")
                            Log.d(TAG, "Contenido: ${sms.messageBody}")
                        }
                    }

                    Log.d(TAG, "Message built: $fullMessage")
                    messageListener?.invoke(fullMessage)

                    context?.let {
                        Toast.makeText(it, "Nuevo SMS recibido", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing SMS", e)
                }
            }
        }
    }
}