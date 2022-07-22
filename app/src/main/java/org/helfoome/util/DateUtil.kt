package org.helfoome.util

import org.helfoome.presentation.type.DayOfWeekType
import java.util.*

object DateUtil {
    private val calendar = Calendar.getInstance()
    val today = calendar.get(Calendar.DAY_OF_WEEK)

    fun getTodayDayOfWeek() =
        when (today) {
            1 -> DayOfWeekType.SUN
            2 -> DayOfWeekType.MON
            3 -> DayOfWeekType.TUE
            4 -> DayOfWeekType.WED
            5 -> DayOfWeekType.THU
            6 -> DayOfWeekType.FRI
            else -> DayOfWeekType.SAT
        }

    fun convertIndexToDayOfWeek(index: Int): DayOfWeekType = when(index) {
        DayOfWeekType.MON.index -> DayOfWeekType.MON
        DayOfWeekType.TUE.index -> DayOfWeekType.TUE
        DayOfWeekType.WED.index -> DayOfWeekType.WED
        DayOfWeekType.THU.index -> DayOfWeekType.THU
        DayOfWeekType.FRI.index -> DayOfWeekType.FRI
        DayOfWeekType.SAT.index -> DayOfWeekType.SAT
        else -> DayOfWeekType.SUN
    }
}
