package model;

import java.time.LocalDate;

public class Assignment {
    private final int id;
    private String title;
    private String course;
    private LocalDate deadline;
    private Priority priority;
    private boolean completed;

    public Assignment(int id, String title, String course, LocalDate deadline,
                      Priority priority, boolean completed) {
        this.id = id;
        this.title = title;
        this.course = course;
        this.deadline = deadline;
        this.priority = priority;
        this.completed = completed;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCourse() { return course; }
    public LocalDate getDeadline() { return deadline; }
    public Priority getPriority() { return priority; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    /** One line for assignments.txt: id,title,course,deadline,priority,status */
    public String toCsv() {
        return id + "," + title + "," + course + "," + deadline + ","
                + priority.name() + "," + completed;
    }

    public static Assignment fromCsv(String line) {
        String[] p = line.split(",", -1);
        return new Assignment(Integer.parseInt(p[0].trim()), p[1], p[2],
                LocalDate.parse(p[3].trim()), Priority.fromString(p[4]),
                Boolean.parseBoolean(p[5].trim()));
    }
}
