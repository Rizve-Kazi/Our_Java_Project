package controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.Priority;
import model.StudyTask;
import util.FileHandler;

/** Keeps the daily study tasks in an ArrayList and saves them to planner.txt. */
public class PlannerController {
    private static final String FILE = "planner.txt";
    private final List<StudyTask> items = new ArrayList<StudyTask>();

    public PlannerController() {
        load();
    }

    private void load() {
        items.clear();
        for (String line : FileHandler.readLines(FILE)) {
            try {
                items.add(StudyTask.fromCsv(line));
            } catch (RuntimeException e) {
                System.err.println("Skipping bad line in " + FILE + ": " + line);
            }
        }
    }

    private void save() {
        List<String> lines = new ArrayList<String>();
        for (StudyTask t : items) {
            lines.add(t.toCsv());
        }
        FileHandler.writeLines(FILE, lines);
    }

    private int nextId() {
        int max = 0;
        for (StudyTask t : items) {
            max = Math.max(max, t.getId());
        }
        return max + 1;
    }

    public StudyTask add(String task, LocalDate date, LocalTime time, Priority priority) {
        String t = FileHandler.clean(task);
        if (t.isEmpty()) {
            throw new IllegalArgumentException("Please enter the study task.");
        }
        StudyTask st = new StudyTask(nextId(), t, date, time, priority, false);
        items.add(st);
        save();
        return st;
    }

    public boolean toggleDone(int id) {
        for (StudyTask t : items) {
            if (t.getId() == id) {
                t.setDone(!t.isDone());
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

    /** By date, then start time, then priority. */
    public List<StudyTask> getSorted() {
        List<StudyTask> copy = new ArrayList<StudyTask>(items);
        Collections.sort(copy, new Comparator<StudyTask>() {
            @Override
            public int compare(StudyTask a, StudyTask b) {
                int d = a.getDate().compareTo(b.getDate());
                if (d != 0) {
                    return d;
                }
                int t = a.getStartTime().compareTo(b.getStartTime());
                if (t != 0) {
                    return t;
                }
                return Integer.compare(a.getPriority().getRank(), b.getPriority().getRank());
            }
        });
        return copy;
    }

    public List<StudyTask> getForDate(LocalDate date) {
        List<StudyTask> result = new ArrayList<StudyTask>();
        for (StudyTask t : getSorted()) {
            if (t.getDate().equals(date)) {
                result.add(t);
            }
        }
        return result;
    }
}
