package com.example.app_geolocalizacion

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

//biblioteca para mandar mensajes por POST
import java.net.HttpURLConnection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //--- EXTRAER EL TEXTO DEL SMS EN VARIABLES
        val smsBody = "Lat: 12.34567, Lon: 76.54321" //ejemplo

        // Dividir el mensaje por coma y extraer los valores
        val parts = smsBody.split(", ")
        val latPart = parts[0].replace("Lat: ", "").trim()
        val lonPart = parts[1].replace("Lon: ", "").trim()

        // Convertir a double si es necesario
        val latitud = latPart.toDouble()
        val longitud = lonPart.toDouble()

        //CODIGO PARA MANDAR LAS VARIABLES AL PHP DEL SERVIDOR
        fun enviarLatLon(latitud: String, longitud: String) {
            val url = URL("http://tu-servidor.com/archivo.php")
            val postData = "latitud=$latitud&longitud=$longitud"

            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                doOutput = true
                val wr = OutputStreamWriter(outputStream)
                wr.write(postData)
                wr.flush()

                // Verificar la respuesta del servidor
                val responseCode = responseCode
                println("Response Code: $responseCode")

                inputStream.bufferedReader().use {
                    val response = it.readText()
                    println("Response: $response")
                }
            }
        }
    }
}