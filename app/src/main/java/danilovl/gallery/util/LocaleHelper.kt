package danilovl.gallery.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

data class AppLanguage(val code: String, val label: String)

object LocaleHelper {

    val languages = listOf(
        AppLanguage("en", "English"),
        AppLanguage("de", "Deutsch"),
        AppLanguage("fr", "Français"),
        AppLanguage("es", "Español"),
        AppLanguage("it", "Italiano"),
        AppLanguage("pt", "Português"),
        AppLanguage("nl", "Nederlands"),
        AppLanguage("cs", "Čeština"),
        AppLanguage("pl", "Polski"),
        AppLanguage("ru", "Русский"),
        AppLanguage("zh", "中文")
    )

    private const val PREFS = "gallery_settings"
    private const val KEY_LANGUAGE = "language"

    fun savedLanguage(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_LANGUAGE, "").orEmpty()

    fun saveLanguage(context: Context, code: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, code)
            .apply()
    }

    fun wrap(context: Context): Context {
        val code = savedLanguage(context)
        if (code.isEmpty()) return context
        val locale = Locale(code)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
