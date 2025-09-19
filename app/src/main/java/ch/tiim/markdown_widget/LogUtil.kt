package ch.tiim.markdown_widget

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogUtil {
    private const val DIR = "MarkdownWidget"
    private const val FILE = "app.log"

    private fun getTargetFile(context: Context): File {
        return try {
            val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val dir = File(downloads, DIR)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, FILE)
            if (!file.exists()) file.createNewFile()
            file
        } catch (e: Exception) {
            // fallback in app-specific external files dir
            val dir = File(context.getExternalFilesDir(null), "logs")
            if (!dir.exists()) dir.mkdirs()
            File(dir, FILE)
        }
    }

    fun append(context: Context, level: String, message: String) {
        try {
            val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            val enabled = prefs.getBoolean("enable_logging", false)
            if (!enabled) return

            val file = getTargetFile(context)
            val ts = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY).format(Date())
            val line = "[$ts] $level: $message\n"
            FileWriter(file, true).use { it.append(line) }
        } catch (e: Exception) {
            // ignore silently
        }
    }

    fun clearLogs(context: Context) {
        try {
            val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val dir = File(downloads, DIR)
            val file = File(dir, FILE)
            if (file.exists()) file.delete()
            // also try app-specific
            val appDir = File(context.getExternalFilesDir(null), "logs")
            val appFile = File(appDir, FILE)
            if (appFile.exists()) appFile.delete()
        } catch (_: Exception) {}
    }
}
