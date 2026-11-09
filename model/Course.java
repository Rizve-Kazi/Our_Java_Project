package model;

public class Course {
    private final int id;
    private final String name;
    private final double credit;
    private final double marks;      // 0-100, or -1 when only a grade was entered
    private final double gradePoint;
    private final String semester;

    public Course(int id, String name, double credit, double marks, double gradePoint, String semester) {
        this.id = id;
        this.name = name;
        this.credit = credit;
        this.marks = marks;
        this.gradePoint = gradePoint;
        this.semester = semester;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getCredit() { return credit; }
    public double getMarks() { return marks; }
    public double getGradePoint() { return gradePoint; }
    public String getSemester() { return semester; }

    /** One line for courses.txt: id,course_name,credit,marks,grade_point,semester */
    public String toCsv() {
        return id + "," + name + "," + credit + "," + marks + "," + gradePoint + "," + semester;
    }

    public static Course fromCsv(String line) {
        String[] p = line.split(",", -1);
        int id = Integer.parseInt(p[0].trim());
        double credit = Double.parseDouble(p[2].trim());
        if (p.length >= 6) {
            return new Course(id, p[1], credit, Double.parseDouble(p[3].trim()),
                    Double.parseDouble(p[4].trim()), p[5]);
        }
        // older 5-field format: id,name,credit,grade_point,semester
        return new Course(id, p[1], credit, -1, Double.parseDouble(p[3].trim()), p[4]);
    }
}
