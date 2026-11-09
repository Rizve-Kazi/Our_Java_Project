package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class StudyTask {
    private final int id;
    private final String task;
    private final LocalDate date;
    private final LocalTime startTime;
    private final Priority priority;
    private boolean done;

    public StudyTask(int id, String task, LocalDate date, LocalTime startTime,
                     Priority priority, boolean done) {
        this.id = id;
        this.task = task;
        this.date = date;
        this.startTime = startTime;
        this.priority = priority;
        this.done = done;
    }

    public int getId() { return id; }
    public String getTask() { return task; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public Priority getPriority() { return priority; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    /** One line for planner.txt: id,task,date,start_time,priority,done */
    public String toCsv() {
        return id + "," + task + "," + date + "," + startTime + ","
                + priority.name() + "," + done;
    }

    public static StudyTask fromCsv(String line) {
        String[] p = line.split(",", -1);
        return new StudyTask(Integer.parseInt(p[0].trim()), p[1],
                LocalDate.parse(p[2].trim()), LocalTime.parse(p[3].trim()),
                Priority.fromString(p[4]), Boolean.parseBoolean(p[5].trim()));
    }
}
