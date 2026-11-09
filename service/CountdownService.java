package service;

import java.util.List;

import model.Exam;
import util.DateUtil;

public class CountdownService {
    private CountdownService() { }

    /** Friendly text for a day count, e.g. "5 days left". */
    public static String describe(long days) {
        if (days < 0) {
            return "Passed (" + (-days) + "d ago)";
        }
        if (days == 0) {
            return "Today!";
        }
        if (days == 1) {
            return "Tomorrow";
        }
        return days + " days left";
    }

    /** True when the date is within the next 7 days. */
    public static boolean isNear(long days) {
        return days >= 0 && days <= 7;
    }

    /** The first exam that has not passed yet, or null. Expects a list sorted by date. */
    public static Exam nextExam(List<Exam> sortedExams) {
        for (Exam e : sortedExams) {
            if (DateUtil.daysLeft(e.getExamDate()) >= 0) {
                return e;
            }
        }
        return null;
    }
}
