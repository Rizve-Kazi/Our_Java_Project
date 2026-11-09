package model;

import java.time.LocalDate;

public class Exam {
    private final int id;
    private final String subject;
    private final LocalDate examDate;
    private final String room;
    private final String syllabus;

    public Exam(int id, String subject, LocalDate examDate, String room, String syllabus) {
        this.id = id;
        this.subject = subject;
        this.examDate = examDate;
        this.room = room;
        this.syllabus = syllabus;
    }

    public int getId() { return id; }
    public String getSubject() { return subject; }
    public LocalDate getExamDate() { return examDate; }
    public String getRoom() { return room; }
    public String getSyllabus() { return syllabus; }

    /** One line for exams.txt: id,subject,exam_date,room,syllabus */
    public String toCsv() {
        return id + "," + subject + "," + examDate + "," + room + "," + syllabus;
    }

    public static Exam fromCsv(String line) {
        String[] p = line.split(",", -1);
        return new Exam(Integer.parseInt(p[0].trim()), p[1],
                LocalDate.parse(p[2].trim()), p[3], p[4]);
    }
}
