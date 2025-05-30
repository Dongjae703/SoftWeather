import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun DateConverter(
    date: String,
    useYear: Boolean = false,
    useMonthDay: Boolean = false,
    useHour: Boolean = false,
    useMin: Boolean = false
): String {
    return try {
        val parsed = LocalDateTime.parse(date)
        val pattern = StringBuilder()

        if (useYear) pattern.append("yyyy년")
        if (useMonthDay) {
            if (pattern.isNotEmpty()) pattern.append(" ")
            pattern.append("MM월 dd일")
        }
        if (useHour) {
            if (pattern.isNotEmpty()) pattern.append(" ")
            pattern.append("HH시")
        }
        if (useMin) {
            if (pattern.isNotEmpty()) pattern.append(" ")
            pattern.append("mm분")
        }

        if (pattern.isNotEmpty()) {
            val formatter = DateTimeFormatter.ofPattern(pattern.toString())
            parsed.format(formatter)
        } else {
            "날짜 변환 오류"
        }
    } catch (e: Exception) {
        "날짜 파싱 실패: ${e.message}"
    }
}