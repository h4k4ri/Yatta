package com.example.testapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import projeto.lib.yattalibsdk2.Yattalib

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val helloWorldTextView = findViewById<TextView>(R.id.helloWorldTextView)
        val yattalib = Yattalib()

        // Exibir "Hello World" no TextView
        helloWorldTextView.text = "Hello World"

        // Verificar segurança em uma thread separada
        Thread {
            val messages = mutableListOf<String>()

            // Verificação de root
            if (yattalib.ehDispositivoRootado()) {
                messages.add("O dispositivo está rooteado.")
            }

            // Verificação de VPN
            if (yattalib.estaUsandoVPN(this)) {
                messages.add("O dispositivo está utilizando uma VPN.")
            }

            // Verificação de emulador
            if (yattalib.ehEmulador(this)) {
                messages.add("O dispositivo é um emulador.")
            }

            // Verificação de modo de depuração
            if (yattalib.estaEmModoDebug(this)) {
                messages.add("Modo de depuração ativado.")
            }

            // Verificação de overlay de tela
            if (yattalib.estaUsandoScreenOverlay(this)) {
                messages.add("Sobreposição de tela ativa.")
            }

            // Verificação de Frida
            if (yattalib.detectaFrida()) {
                messages.add("Frida detectado.")
            }

            // Atualizar a UI na thread principal
            runOnUiThread {
                if (messages.isNotEmpty()) {
                    // Mensagem geral de fechamento como pop-up
                    android.widget.Toast.makeText(
                        this,
                        "Violação detectada: Fechando o aplicativo.",
                        android.widget.Toast.LENGTH_LONG
                    ).show()

                    // Exibir cada mensagem de violação como um pop-up
                    messages.forEachIndexed { index, message ->
                        android.widget.Toast.makeText(
                            this,
                            "${index + 1}° motivo - $message",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }

                    // Fechar o aplicativo após exibir as mensagens
                    finish()
                } else {
                    // Se nenhuma violação for detectada
                    android.widget.Toast.makeText(
                        this,
                        "Nenhuma violação detectada. O aplicativo está seguro para uso.",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }
}