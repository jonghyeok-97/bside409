package bsise.server.report;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;

public class WeeklyDataManager {

    private static final int MID_OF_WEEK = 3;

    private final LocalDate target;

    public WeeklyDataManager(LocalDate target) {
        this.target = target;
    }

    public LocalDate getMondayOfWeek() {
        return target.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public LocalDate getSundayOfWeek() {
        return target.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    public int getWeekOfWeekBasedYear() {
        return target.get(WeekFields.ISO.weekOfWeekBasedYear());
    }

    public int getMonthValue() {
        return getMiddleOfWeek().getMonthValue();
    }

    public int getWeekOfMonth() {
        return getMiddleOfWeek().get(WeekFields.ISO.weekOfMonth());
    }

    private LocalDate getMiddleOfWeek() {
        LocalDate mondayOfWeek = getMondayOfWeek();
        return mondayOfWeek.plusDays(MID_OF_WEEK);
    }
}
