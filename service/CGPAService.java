package service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.Course;

/** GPA / CGPA maths. Uses the common 4.00 scale (A+ = 4.00 ... F = 0.00). */
public class CGPAService {
    private static final String[] LETTERS = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F"};
    private static final double[] POINTS = {4.00, 3.75, 3.50, 3.25, 3.00, 2.75, 2.50, 2.25, 2.00, 0.00};

    private CGPAService() { }

    public static String[] letters() {
        return LETTERS.clone();
    }

    public static double pointFor(String letter) {
        for (int i = 0; i < LETTERS.length; i++) {
            if (LETTERS[i].equalsIgnoreCase(letter.trim())) {
                return POINTS[i];
            }
        }
        throw new IllegalArgumentException("Unknown grade: " + letter);
    }

    public static String letterFor(double gradePoint) {
        for (int i = 0; i < POINTS.length; i++) {
            if (Math.abs(POINTS[i] - gradePoint) < 0.001) {
                return LETTERS[i];
            }
        }
        return "-";
    }

    /** Grade point for marks out of 100 (80+ A+, 75 A, 70 A-, 65 B+, 60 B, 55 B-, 50 C+, 45 C, 40 D, else F). */
    public static double pointForMarks(double marks) {
        if (marks >= 80) return 4.00;
        if (marks >= 75) return 3.75;
        if (marks >= 70) return 3.50;
        if (marks >= 65) return 3.25;
        if (marks >= 60) return 3.00;
        if (marks >= 55) return 2.75;
        if (marks >= 50) return 2.50;
        if (marks >= 45) return 2.25;
        if (marks >= 40) return 2.00;
        return 0.00;
    }

    public static double totalCredits(List<Course> courses) {
        double sum = 0;
        for (Course c : courses) {
            sum += c.getCredit();
        }
        return sum;
    }

    /** CGPA = sum(credit x grade point) / sum(credit). Returns 0 when there are no courses. */
    public static double cgpa(List<Course> courses) {
        double credits = 0;
        double points = 0;
        for (Course c : courses) {
            credits += c.getCredit();
            points += c.getCredit() * c.getGradePoint();
        }
        return credits == 0 ? 0 : points / credits;
    }

    /** GPA of each semester, in alphabetical order of the semester name. */
    public static Map<String, Double> semesterWise(List<Course> courses) {
        Map<String, Double> credits = new TreeMap<String, Double>();
        Map<String, Double> points = new TreeMap<String, Double>();
        for (Course c : courses) {
            String s = c.getSemester();
            credits.put(s, credits.containsKey(s) ? credits.get(s) + c.getCredit() : c.getCredit());
            double p = c.getCredit() * c.getGradePoint();
            points.put(s, points.containsKey(s) ? points.get(s) + p : p);
        }
        Map<String, Double> result = new TreeMap<String, Double>();
        for (String s : credits.keySet()) {
            result.put(s, points.get(s) / credits.get(s));
        }
        return result;
    }
}
