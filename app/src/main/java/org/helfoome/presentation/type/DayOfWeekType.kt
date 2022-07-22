package org.helfoome.presentation.type

import org.helfoome.R

/** 요일별 문자열과 인덱스를 갖는 enum class, 서버에서 전달되는 월~일 순서에 따라 index를 부여함 */
enum class DayOfWeekType(val strRes: Int, val index: Int) {
    MON(R.string.monday, 0), TUE(R.string.tuesday, 1), WED(R.string.wednesday, 2), THU(R.string.thursday, 3), FRI(R.string.friday, 4), SAT(R.string.saturday, 5), SUN(
        R.string.sunday, 6)
}
