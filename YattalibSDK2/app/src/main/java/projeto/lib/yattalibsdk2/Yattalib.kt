package projeto.lib.yattalibsdk2

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.content.Context
import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.NetworkInterface

public class Yattalib {

    // Verificação de dispositivo rootado unificada
    public fun ehDispositivoRootado(): Boolean {
        val caminhos = arrayOf(
            "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su",
            "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su"
        )
        if (caminhos.any { File(it).exists() }) {
            return true
        }

        if (executaComando("su")) {
            return true
        }

        try {
            val process = Runtime.getRuntime().exec("ls /data/")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readLine()
            if (output != null) {
                return true
            }
        } catch (e: Exception) {
            // Continua para verificar as propriedades de root
        }

        val propriedades = listOf(
            "ro.build.tags" to "test-keys",
            "ro.debuggable" to "1",
            "ro.secure" to "0"
        )
        return propriedades.any { (prop, valorEsperado) ->
            val valor = try {
                Runtime.getRuntime().exec("getprop $prop").inputStream.bufferedReader().readLine()
            } catch (e: Exception) {
                null
            }
            valor == valorEsperado
        }
    }

    public fun executaComando(comando: String): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(comando)
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                val output = reader.readLine()
                process.destroy()
                output != null
            }
        } catch (e: Exception) {
            false
        }
    }

    // Verificação de uso de VPN
    public fun estaUsandoVPN(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Verifica se a API 23 (Android M) ou superior está disponível
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            // Verifica se a rede é uma VPN
            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        } else {
            // Para versões abaixo da API 23, você pode adicionar uma abordagem alternativa aqui, se necessário
            // Exemplo: usar NetworkInterface para verificar VPN
            TODO("API 23 ou superior necessária para verificar VPN")
        }
    }

    // Verificação de emulador unificada
    public fun ehEmulador(context: Context): Boolean {
        val indicadoresDeEmulador = listOf(
            Build.FINGERPRINT.startsWith("generic"),
            Build.FINGERPRINT.startsWith("unknown"),
            Build.MODEL.contains("google_sdk"),
            Build.MODEL.contains("Emulator"),
            Build.MODEL.contains("Android SDK built for x86"),
            Build.BOARD == "QC_Reference_Phone" && Build.MANUFACTURER != "Xiaomi",
            Build.MANUFACTURER.contains("Genymotion"),
            Build.HOST.startsWith("Build"),
            Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"),
            Build.PRODUCT == "google_sdk",
            Build.PRODUCT == "sdk",
            Build.PRODUCT == "sdk_x86",
            Build.PRODUCT == "vbox86p",
            Build.HARDWARE == "goldfish",
            Build.HARDWARE == "ranchu",
            Build.HARDWARE == "vbox86"
        )

        val pacotesEmuladores = listOf(
            "com.bluestacks.appplayer",
            "com.bluestacks.settings",
            "com.android.emulator"
        )

        val emuladorDetectado = indicadoresDeEmulador.any { it }

        val appEmuladorInstalado = pacotesEmuladores.any { pacote ->
            try {
                context.packageManager.getPackageInfo(pacote, 0)
                true
            } catch (e: Exception) {
                false
            }
        }

        return emuladorDetectado || appEmuladorInstalado
    }

    // Verificação de modo de depuração
    public fun estaEmModoDebug(context: Context): Boolean {
        return (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    // Verificação de overlay de tela
    public fun estaUsandoScreenOverlay(context: Context): Boolean {
        val params = android.view.WindowManager.LayoutParams()
        return (params.flags and android.view.WindowManager.LayoutParams.FLAG_SECURE) == 0
    }

    // Verificação de presença do Frida
    public fun detectaFrida(): Boolean {
        val fridaProcessos = listOf(
            "frida-server", "frida-agent", "frida-inject", "gum-js-loop", "libfrida-gadget"
        )

        val fridaArquivos = listOf(
            "/system/bin/frida-server", "/system/xbin/frida-server", "/data/local/tmp/frida-server"
        )

        // Verifica processos do Frida
        val processoDetectado = fridaProcessos.any { processo ->
            executaComando("pgrep $processo") || executaComando("pidof $processo")
        }

        // Verifica arquivos do Frida
        val arquivoDetectado = fridaArquivos.any { caminho ->
            File(caminho).exists()
        }

        return processoDetectado || arquivoDetectado
    }
}