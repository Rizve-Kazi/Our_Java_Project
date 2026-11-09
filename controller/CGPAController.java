package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.Course;
import service.CGPAService;
import util.FileHandler;

/** Keeps the entered courses in an ArrayList and saves them to courses.txt. */
public class CGPAController {
    private static final String FILE = "courses.txt";
    private final List<Course> items = new ArrayList<Course>();

    public CGPAController() {
        load();
    }

    private void load() {
        items.clear();
        for (String line : FileHandler.readLines(FILE)) {
            try {
                items.add(Course.fromCsv(line));
            } catch (RuntimeException e) {
                System.err.println("Skipping bad line in " + FILE + ": " + line);
            }
        }
    }

    private void save() {
        List<String> lines = new ArrayList<String>();
        for (Course c : items) {
            lines.add(c.toCsv());
        }
        FileHandler.writeLines(FILE, lines);
    }

    private int nextId() {
        int max = 0;
        for (Course c : items) {
            max = Math.max(max, c.getId());
        }
        return max + 1;
    }

    public Course add(String name, double credit, String gradeLetter, String semester) {
        String n = FileHandler.clean(name);
        if (n.isEmpty()) {
            throw new IllegalArgumentException("Please enter the course name.");
        }
        if (credit <= 0 || credit > 10) {
            throw new IllegalArgumentException("Credit must be more than 0 and at most 10.");
        }
        String sem = FileHandler.clean(semester);
        if (sem.isEmpty()) {
            sem = "Sem 1";
        }
        Course c = new Course(nextId(), n, credit, -1, CGPAService.pointFor(gradeLetter), sem);
        items.add(c);
        save();
        return c;
    }

    /** Add a course from marks out of 100; the grade point is worked out automatically. */
    public Course addByMarks(String name, double credit, double marks, String semester) {
        String n = FileHandler.clean(name);
        if (n.isEmpty()) {
            throw new IllegalArgumentException("Please enter the course name.");
        }
        if (credit <= 0 || credit > 10) {
            throw new IllegalArgumentException("Credit must be more than 0 and at most 10.");
        }
        if (marks < 0 || marks > 100) {
            throw new IllegalArgumentException("Marks must be between 0 and 100.");
        }
        String sem = FileHandler.clean(semester);
        if (sem.isEmpty()) {
            sem = "Sem 1";
        }
        Course c = new Course(nextId(), n, credit, marks, CGPAService.pointForMarks(marks), sem);
        items.add(c);
        save();
        return c;
    }

    public boolean delete(int id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == id) {
                items.remove(i);
                save();
                return true;
            }
        }
        return false;
    }

    public List<Course> getAll() {
        return new ArrayList<Course>(items);
    }

    public double cgpa() {
        return CGPAService.cgpa(items);
    }

    public double totalCredits() {
        return CGPAService.totalCredits(items);
    }

    public Map<String, Double> semesterWise() {
        return CGPAService.semesterWise(items);
    }
}
