package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controller.CGPAController;
import controller.StudentController;
import model.Course;
import model.Student;
import service.CGPAService;

/** Marks and CGPA calculator: enter marks out of 100, the grade and GPA are worked out. */
public class CGPAPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final CGPAController controller;
    private final StudentController student;
    private final Runnable onChange;
    private final DefaultTableModel model =
            Ui.model("No.", "Course", "Credit", "Marks", "Grade", "Grade Point", "Semester");
    private final JTable table = Ui.table(model);
    private final JTextField nameField = new JTextField(14);
    private final JTextField creditField = new JTextField("3", 4);
    private final JTextField marksField = new JTextField(5);
    private final JTextField semesterField = new JTextField("Sem 1", 7);
    private final JLabel result = new JLabel(" ");

    public CGPAPanel(CGPAController controller, StudentController student, Runnable onChange) {
        this.controller = controller;
        this.student = student;
        this.onChange = onChange;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Ui.weights(table, 0.5, 3, 1, 1, 1, 1.2, 1.2);

        JPanel form = Ui.form("Add course marks");
        form.add(new JLabel("Course"));
        form.add(nameField);
        form.add(new JLabel("Credit"));
        form.add(creditField);
        form.add(new JLabel("Marks (0-100)"));
        form.add(marksField);
        form.add(new JLabel("Semester"));
        form.add(semesterField);
        JButton addBtn = Ui.button("Add", Ui.BLUE);
        addBtn.addActionListener(e -> addCourse());
        form.add(addBtn);

        result.setFont(result.getFont().deriveFont(Font.PLAIN, 14f));
        result.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        JPanel south = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JButton delBtn = Ui.button("Delete", Ui.RED);
        delBtn.addActionListener(e -> delete());
        buttons.add(delBtn);
        buttons.add(new JLabel("80+ A+ (4.00), 75 A, 70 A-, 65 B+, 60 B, 55 B-, 50 C+, 45 C, 40 D (2.00), below 40 F"));
        south.add(result, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.SOUTH);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        refresh();
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public void refresh() {
        model.setRowCount(0);
        List<Course> courses = controller.getAll();
        for (Course c : courses) {
            model.addRow(new Object[] {
                c.getId(), c.getName(), c.getCredit(),
                c.getMarks() < 0 ? "-" : String.format(Locale.US, "%.1f", c.getMarks()),
                CGPAService.letterFor(c.getGradePoint()),
                String.format(Locale.US, "%.2f", c.getGradePoint()), c.getSemester()
            });
        }
        StringBuilder sb = new StringBuilder("<html>");
        if (student.hasProfile()) {
            Student s = student.get();
            sb.append("<b>").append(esc(s.getName())).append("</b>");
            if (!s.getStudentId().isEmpty()) {
                sb.append(" (ID: ").append(esc(s.getStudentId())).append(")");
            }
            sb.append(" &nbsp;|&nbsp; ");
        }
        if (courses.isEmpty()) {
            sb.append("Add your courses and marks to see your GPA.</html>");
            result.setText(sb.toString());
            return;
        }
        sb.append("<b>CGPA: ").append(String.format(Locale.US, "%.2f", controller.cgpa()));
        sb.append("</b> &nbsp;|&nbsp; Total credits: ")
          .append(String.format(Locale.US, "%.1f", controller.totalCredits()));
        for (Map.Entry<String, Double> e : controller.semesterWise().entrySet()) {
            sb.append(" &nbsp;|&nbsp; ").append(esc(e.getKey())).append(" GPA: ")
              .append(String.format(Locale.US, "%.2f", e.getValue()));
        }
        sb.append("</html>");
        result.setText(sb.toString());
    }

    private void addCourse() {
        double credit;
        double marks;
        try {
            credit = Double.parseDouble(creditField.getText().trim());
        } catch (NumberFormatException ex) {
            Ui.error(this, "Credit must be a number, for example 3 or 1.5.");
            return;
        }
        try {
            marks = Double.parseDouble(marksField.getText().trim());
        } catch (NumberFormatException ex) {
            Ui.error(this, "Marks must be a number between 0 and 100, for example 85.");
            return;
        }
        try {
            controller.addByMarks(nameField.getText(), credit, marks, semesterField.getText());
        } catch (IllegalArgumentException ex) {
            Ui.error(this, ex.getMessage());
            return;
        }
        nameField.setText("");
        marksField.setText("");
        refresh();
        onChange.run();
    }

    private void delete() {
        int id = Ui.selectedId(table);
        if (id < 0) {
            Ui.error(this, "Select a course in the table first.");
            return;
        }
        controller.delete(id);
        refresh();
        onChange.run();
    }
}
