package ch.tiim.markdown_widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import androidx.preference.PreferenceManager

object MarkdownWidgetHelper {

    fun createBackgroundDrawable(context: Context): GradientDrawable {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val radiusDp = prefs.getInt("corner_radius", 0)
        val bgColorString = prefs.getString("bg_color", "#FFFFFFFF") ?: "#FFFFFFFF"
        val bgColor = try { Color.parseColor(bgColorString) } catch (_: Exception) { Color.WHITE }

        val drawable = GradientDrawable()
        val density = context.resources.displayMetrics.density
        drawable.cornerRadius = radiusDp * density
        drawable.setColor(bgColor)
        return drawable
    }

    fun drawableToBitmap(drawable: GradientDrawable, width: Int, height: Int): Bitmap {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bmp
    }

    fun applyToRemoteViews(context: Context, remoteViews: android.widget.RemoteViews, layoutId: Int) {
        try {
            val drawable = createBackgroundDrawable(context)
            // pick reasonable size: 1x widget cell => approximate px
            val density = context.resources.displayMetrics.density
            val w = (200 * density).toInt().coerceAtLeast(200)
            val h = (200 * density).toInt().coerceAtLeast(200)
            val bmp = drawableToBitmap(drawable, w, h)
            remoteViews.setImageViewBitmap(R.id.widget_bg, bmp)

            // set text color
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val textColorString = prefs.getString("text_color", "#FF000000") ?: "#FF000000"
            val textColor = try { Color.parseColor(textColorString) } catch (_: Exception) { Color.BLACK }
            remoteViews.setTextColor(R.id.widget_text, textColor)

        } catch (e: Exception) {
            LogUtil.append(context, "error", "applyToRemoteViews failed: ${e.message}")
        }
    }
}
