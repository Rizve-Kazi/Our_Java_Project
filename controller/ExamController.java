package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.Exam;
import util.FileHandler;

/** Keeps all exams in an ArrayList and saves them to exams.txt. */
public class ExamController {
    private static final String FILE = "exams.txt";
    private final List<Exam> items = new ArrayList<Exam>();

    public ExamController() {
        load();
    }

    private void load() {
        items.clear();
        for (String line : FileHandler.readLines(FILE)) {
            try {
                items.add(Exam.fromCsv(line));
            } catch (RuntimeException e) {
                System.err.println("Skipping bad line in " + FILE + ": " + line);
            }
        }
    }

    private void save() {
        List<String> lines = new ArrayList<String>();
        for (Exam e : items) {
            lines.add(e.toCsv());
        }
        FileHandler.writeLines(FILE, lines);
    }

    private int nextId() {
        int max = 0;
        for (Exam e : items) {
            max = Math.max(max, e.getId());
        }
        return max + 1;
    }

    public Exam add(String subject, LocalDate date, String room, String syllabus) {
        String s = FileHandler.clean(subject);
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Please enter the subject name.");
        }
        Exam e = new Exam(nextId(), s, date, FileHandler.clean(room), FileHandler.clean(syllabus));
        items.add(e);
        save();
        return e;
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

    /** Earliest exam first. */
    public List<Exam> getSorted() {
        List<Exam> copy = new ArrayList<Exam>(items);
        Collections.sort(copy, new Comparator<Exam>() {
            @Override
            public int compare(Exam a, Exam b) {
                return a.getExamDate().compareTo(b.getExamDate());
            }
        });
        return copy;
    }
}
