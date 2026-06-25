package danilovl.gallery.util

import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

object DateUtils {

    private fun calendarOf(millis: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = millis }

    fun dayOfWeek(millis: Long): Int = calendarOf(millis).get(Calendar.DAY_OF_WEEK)

    fun month(millis: Long): Int = calendarOf(millis).get(Calendar.MONTH)

    fun dayOfMonth(millis: Long): Int = calendarOf(millis).get(Calendar.DAY_OF_MONTH)

    fun year(millis: Long): Int = calendarOf(millis).get(Calendar.YEAR)

    fun currentDayOfWeek(): Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    fun currentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH)

    val weekdayOrder = listOf(
        Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
        Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
    )

    private fun symbols(locale: Locale) = DateFormatSymbols(locale)

    private fun String.capitalizeFirst(): String =
        replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    fun weekdayShort(dayOfWeek: Int, locale: Locale = Locale.getDefault()): String =
        symbols(locale).shortWeekdays[dayOfWeek].capitalizeFirst()

    fun weekdayFull(dayOfWeek: Int, locale: Locale = Locale.getDefault()): String =
        symbols(locale).weekdays[dayOfWeek].capitalizeFirst()

    fun monthShort(month: Int, locale: Locale = Locale.getDefault()): String =
        symbols(locale).shortMonths[month].capitalizeFirst()

    fun monthFull(month: Int, locale: Locale = Locale.getDefault()): String =
        symbols(locale).months[month].capitalizeFirst()

    fun formatDayMonth(day: Int, month: Int, locale: Locale = Locale.getDefault()): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.MONTH, month)
        }
        val sdf = java.text.SimpleDateFormat("d MMMM", locale)
        return sdf.format(cal.time)
    }

    fun formatDateTime(millis: Long, locale: Locale = Locale.getDefault()): String {
        val sdf = java.text.SimpleDateFormat("d MMMM yyyy, HH:mm", locale)
        return sdf.format(java.util.Date(millis))
    }
}
