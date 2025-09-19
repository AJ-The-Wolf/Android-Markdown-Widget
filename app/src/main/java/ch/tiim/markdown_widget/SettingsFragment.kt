package ch.tiim.markdown_widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val prefs = preferenceManager.sharedPreferences

        // Listener per aggiornare widget al cambiamento delle impostazioni
        prefs?.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == "corner_radius" || key == "bg_color" || key == "text_color") {
                forceUpdateAllWidgets()
            }
            if (key == "enable_logging") {
                // nulla di più da fare qui
            }
        }

        // Pulsante "Cancella log"
        findPreference<Preference>("clear_logs")?.setOnPreferenceClickListener {
            LogUtil.clearLogs(requireContext())
            true
        }
    }

    private fun forceUpdateAllWidgets() {
        val context = requireContext()
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, MarkdownFileWidget::class.java)
        val ids = appWidgetManager.getAppWidgetIds(componentName)
        if (ids.isNotEmpty()) {
            val updateIntent = Intent(context, MarkdownFileWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            context.sendBroadcast(updateIntent)
        }
    }
}
