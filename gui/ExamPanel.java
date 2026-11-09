package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controller.ExamController;
import model.Exam;
import service.CountdownService;
import util.DateUtil;

/** Exam countdown: shows how many days are left for every exam. */
public class ExamPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final ExamController controller;
    private final Runnable onChange;
    private final DefaultTableModel model =
            Ui.model("No.", "Subject", "Exam Date", "Countdown", "Room", "Syllabus");
    private final JTable table = Ui.table(model);
    private final JTextField subjectField = new JTextField(12);
    private final JTextField dateField = new JTextField(10);
    private final JTextField roomField = new JTextField(6);
    private final JTextField syllabusField = new JTextField(16);
    private final JLabel banner = new JLabel(" ");
    private List<Exam> shown = new ArrayList<Exam>();

    public ExamPanel(ExamController controller, Runnable onChange) {
        this.controller = controller;
        this.onChange = onChange;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Ui.weights(table, 0.5, 2, 1.4, 1.5, 1, 3);

        dateField.setText(LocalDate.now().plusDays(14).toString());

        JPanel form = Ui.form("Add exam");
        form.add(new JLabel("Subject"));
        form.add(subjectField);
        form.add(new JLabel("Date (yyyy-MM-dd)"));
        form.add(dateField);
        form.add(new JLabel("Room"));
        form.add(roomField);
        form.add(new JLabel("Syllabus"));
        form.add(syllabusField);
        JButton addBtn = Ui.button("Add", Ui.BLUE);
        addBtn.addActionListener(e -> addExam());
        form.add(addBtn);

        banner.setFont(banner.getFont().deriveFont(Font.BOLD, 15f));
        banner.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        JPanel north = new JPanel(new BorderLayout());
        north.add(form, BorderLayout.NORTH);
        north.add(banner, BorderLayout.SOUTH);

        Ui.useRenderer(table, new Ui.RowRenderer(-1,
                r -> r < shown.size() && DateUtil.daysLeft(shown.get(r).getExamDate()) < 0,
                r -> r < shown.size()
                        && CountdownService.isNear(DateUtil.daysLeft(shown.get(r).getExamDate()))));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JButton delBtn = Ui.button("Delete", Ui.RED);
        delBtn.addActionListener(e -> delete());
        buttons.add(delBtn);
        buttons.add(new JLabel("Red text = exam within 7 days, grey = already passed"));

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        refresh();
    }

    public void refresh() {
        model.setRowCount(0);
        shown = controller.getSorted();
        for (Exam e : shown) {
            model.addRow(new Object[] {
                e.getId(), e.getSubject(), e.getExamDate().toString(),
                CountdownService.describe(DateUtil.daysLeft(e.getExamDate())),
                e.getRoom(), e.getSyllabus()
            });
        }
        Exam next = CountdownService.nextExam(shown);
        if (next == null) {
            banner.setText("No upcoming exams.");
            banner.setForeground(Color.DARK_GRAY);
        } else {
            long days = DateUtil.daysLeft(next.getExamDate());
            banner.setText("Next exam: " + next.getSubject() + "  -  " + CountdownService.describe(days));
            banner.setForeground(CountdownService.isNear(days) ? Ui.RED : Ui.NAVY);
        }
    }

    private void addExam() {
        LocalDate date = DateUtil.parseDate(dateField.getText());
        if (date == null) {
            Ui.error(this, "The exam date must look like 2026-12-31.");
            return;
        }
        try {
            controller.add(subjectField.getText(), date, roomField.getText(), syllabusField.getText());
        } catch (IllegalArgumentException ex) {
            Ui.error(this, ex.getMessage());
            return;
        }
        subjectField.setText("");
        roomField.setText("");
        syllabusField.setText("");
        refresh();
        onChange.run();
    }

    private void delete() {
        int id = Ui.selectedId(table);
        if (id < 0) {
            Ui.error(this, "Select an exam in the table first.");
            return;
        }
        controller.delete(id);
        refresh();
        onChange.run();
    }
}
