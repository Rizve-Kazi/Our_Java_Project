package controller;

import java.util.Collections;
import java.util.List;

import model.Student;
import util.FileHandler;

/** Saves the student's name and ID to student.txt. */
public class StudentController {
    private static final String FILE = "student.txt";
    private Student student = new Student("", "");

    public StudentController() {
        List<String> lines = FileHandler.readLines(FILE);
        if (!lines.isEmpty()) {
            String[] p = lines.get(0).split(",", -1);
            student = new Student(p[0], p.length > 1 ? p[1] : "");
        }
    }

    public Student get() {
        return student;
    }

    public boolean hasProfile() {
        return !student.getName().isEmpty();
    }

    public void save(String name, String studentId) {
        String n = FileHandler.clean(name);
        if (n.isEmpty()) {
            throw new IllegalArgumentException("Please enter your name.");
        }
        student = new Student(n, FileHandler.clean(studentId));
        FileHandler.writeLines(FILE, Collections.singletonList(student.toCsv()));
    }
}
