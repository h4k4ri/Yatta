package projeto.lib.yattalibsdk2

import android.content.Context
import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.NetworkInterface

public class Yattalib {

    public fun ehDispositivoRootado(): Boolean {
        // Verifica a existência de binários de root em locais conhecidos
        val caminhos = arrayOf(
            "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su",
            "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su"
        )
        if (caminhos.any { File(it).exists() }) {
            return true
        }

        // Verifica a execução do comando "su"
        if (executaComando("su")) {
            return true
        }

        // Verifica permissões de root
        return try {
            val process = Runtime.getRuntime().exec("ls /data/")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readLine()
            output != null
        } catch (e: Exception) {
            false
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

    public fun estaUsandoVPN(context: Context): Boolean {
        val interfacesDeRede = NetworkInterface.getNetworkInterfaces()
        return interfacesDeRede.toList().any { it.isUp && it.interfaceAddresses.isNotEmpty() && it.name.contains("tun") }
    }

    public fun ehEmulador(): Boolean {
        return listOf(
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
        ).any { it }
    }

    public fun estaEmModoDebug(context: Context): Boolean {
        return (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
}

// Simple test class to verify inclusion in classes.jar
class HelloWorld {
    fun sayHello(): String {
        return "Hello, World!"
    }
}