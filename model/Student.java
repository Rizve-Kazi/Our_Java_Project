package model;

/** The student's name and student ID. */
public class Student {
    private final String name;
    private final String studentId;

    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
    }

    public String getName() { return name; }
    public String getStudentId() { return studentId; }

    /** One line for student.txt: name,student_id */
    public String toCsv() {
        return name + "," + studentId;
    }
}
