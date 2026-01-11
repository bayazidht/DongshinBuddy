import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsPrefs(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("SETTINGS_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        const val THEME_LIGHT = 1
        const val THEME_DARK = 2
        const val THEME_SYSTEM = 0
    }

    var themeMode: Int
        get() = preferences.getInt(KEY_THEME_MODE, THEME_SYSTEM)
        set(value) = preferences.edit { putInt(KEY_THEME_MODE, value) }
}