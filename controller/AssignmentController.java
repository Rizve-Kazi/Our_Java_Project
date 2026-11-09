package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.Assignment;
import model.Priority;
import util.FileHandler;

/** Keeps all assignments in an ArrayList and saves them to assignments.txt. */
public class AssignmentController {
    private static final String FILE = "assignments.txt";
    private final List<Assignment> items = new ArrayList<Assignment>();

    public AssignmentController() {
        load();
    }

    private void load() {
        items.clear();
        for (String line : FileHandler.readLines(FILE)) {
            try {
                items.add(Assignment.fromCsv(line));
            } catch (RuntimeException e) {
                System.err.println("Skipping bad line in " + FILE + ": " + line);
            }
        }
    }

    private void save() {
        List<String> lines = new ArrayList<String>();
        for (Assignment a : items) {
            lines.add(a.toCsv());
        }
        FileHandler.writeLines(FILE, lines);
    }

    private int nextId() {
        int max = 0;
        for (Assignment a : items) {
            max = Math.max(max, a.getId());
        }
        return max + 1;
    }

    public Assignment add(String title, String course, LocalDate deadline, Priority priority) {
        String t = FileHandler.clean(title);
        if (t.isEmpty()) {
            throw new IllegalArgumentException("Please enter a title.");
        }
        Assignment a = new Assignment(nextId(), t, FileHandler.clean(course), deadline, priority, false);
        items.add(a);
        save();
        return a;
    }

    public boolean toggleComplete(int id) {
        for (Assignment a : items) {
            if (a.getId() == id) {
                a.setCompleted(!a.isCompleted());
                save();
                return true;
            }
        }
        return false;
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

    /** Pending first, then earliest deadline, then most important priority. */
    public List<Assignment> getSorted() {
        List<Assignment> copy = new ArrayList<Assignment>(items);
        Collections.sort(copy, new Comparator<Assignment>() {
            @Override
            public int compare(Assignment a, Assignment b) {
                if (a.isCompleted() != b.isCompleted()) {
                    return a.isCompleted() ? 1 : -1;
                }
                int d = a.getDeadline().compareTo(b.getDeadline());
                if (d != 0) {
                    return d;
                }
                return Integer.compare(a.getPriority().getRank(), b.getPriority().getRank());
            }
        });
        return copy;
    }

    public int pendingCount() {
        int n = 0;
        for (Assignment a : items) {
            if (!a.isCompleted()) {
                n++;
            }
        }
        return n;
    }
}
