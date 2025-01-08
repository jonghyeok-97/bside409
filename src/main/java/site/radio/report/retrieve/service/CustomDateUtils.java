package site.radio.report.retrieve.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;

public class CustomDateUtils {

    private static final WeekFields KOREA = WeekFields.of(DayOfWeek.MONDAY, 4);
    private static final String WEEK_OF_MONTH_FORMAT = "%d년 %d월 %d주차";

    /**
     * 주어진 날짜를 `%d년 %d월 %d주차`로 변환하는 함수
     *
     * @param localDate 주어진 날짜
     * @return `%d년 %d월 %d주차`로 변환된 문자열
     */
    public static String getWeekOfMonth(LocalDate localDate) {
        int weekOfMonth = localDate.get(KOREA.weekOfMonth());

        // 2개 달에 껴있는 주일 때 => 이전 달의 마지막 주차로 계산
        if (weekOfMonth == 0) {
            LocalDate lastDayOfLastMonth = localDate.with(TemporalAdjusters.firstDayOfMonth()).minusDays(1);
            return getWeekOfMonth(lastDayOfLastMonth);
        }

        // 주어진 날짜가 속한 달의 마지막 날
        LocalDate lastDayOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());

        // 주어진 날짜가 마지막 주차이면서, 해당 달의 마지막 요일이 목요일 이전이라면 => 다음달 1주차로 계산
        if (isLastWeekOfMonth(localDate) && isBeforeThursDay(lastDayOfMonth)) {
            LocalDate firstDayOfNextMonth = lastDayOfMonth.plusDays(1); // 다음 달 1주차로 넘기기
            return getWeekOfMonth(firstDayOfNextMonth);
        }

        return String.format(WEEK_OF_MONTH_FORMAT, localDate.getYear(), localDate.getMonthValue(), weekOfMonth);
    }

    /**
     * 주어진 날짜의 요일이 목요일 이전(exclusive)인지 여부를 판단하는 함수
     *
     * @param localDate 주어진 날짜
     * @return 목요일 이전(exclusive)이면 true, 목요일 포함 이후이면 false
     */
    private static boolean isBeforeThursDay(LocalDate localDate) {
        return localDate.getDayOfWeek().compareTo(DayOfWeek.THURSDAY) < 0;
    }

    /**
     * 주어진 날짜가 해당 월의 마지막 주차에 해당하는지 여부를 판단하는 함수
     *
     * @param localDate 주어진 날짜
     * @return 예를 들어, 12월 마지막 주차가 12월 4주차이며, 주어진 날짜가 해당 월의 마지막 주차라면 true, 마지막 주차가 아니라면 false
     */
    private static boolean isLastWeekOfMonth(LocalDate localDate) {
        int weekOfMonth = localDate.get(KOREA.weekOfMonth());
        LocalDate lastDayOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());

        return weekOfMonth == lastDayOfMonth.get(KOREA.weekOfMonth());
    }
}
