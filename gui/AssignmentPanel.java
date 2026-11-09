package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controller.AssignmentController;
import model.Assignment;
import model.Priority;
import service.CountdownService;
import util.DateUtil;

/** Assignment deadline tracker with priority levels. */
public class AssignmentPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AssignmentController controller;
    private final Runnable onChange;
    private final DefaultTableModel model =
            Ui.model("No.", "Title", "Course", "Deadline", "Days Left", "Priority", "Status");
    private final JTable table = Ui.table(model);
    private final JTextField titleField = new JTextField(16);
    private final JTextField courseField = new JTextField(8);
    private final JTextField deadlineField = new JTextField(10);
    private final JComboBox<Priority> priorityBox = new JComboBox<Priority>(Priority.values());
    private List<Assignment> shown = new ArrayList<Assignment>();

    public AssignmentPanel(AssignmentController controller, Runnable onChange) {
        this.controller = controller;
        this.onChange = onChange;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Ui.weights(table, 0.5, 3, 1.3, 1.4, 1.4, 1, 1);

        deadlineField.setText(LocalDate.now().plusDays(7).toString());
        priorityBox.setSelectedItem(Priority.MEDIUM);

        JPanel form = Ui.form("Add assignment");
        form.add(new JLabel("Title"));
        form.add(titleField);
        form.add(new JLabel("Course"));
        form.add(courseField);
        form.add(new JLabel("Deadline (yyyy-MM-dd)"));
        form.add(deadlineField);
        form.add(new JLabel("Priority"));
        form.add(priorityBox);
        JButton addBtn = Ui.button("Add", Ui.BLUE);
        addBtn.addActionListener(e -> addAssignment());
        form.add(addBtn);

        Ui.useRenderer(table, new Ui.RowRenderer(5,
                r -> r < shown.size() && shown.get(r).isCompleted(),
                r -> r < shown.size() && !shown.get(r).isCompleted()
                        && DateUtil.daysLeft(shown.get(r).getDeadline()) <= 2));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JButton doneBtn = Ui.button("Mark done / undo", Ui.GREEN);
        doneBtn.addActionListener(e -> toggle());
        JButton delBtn = Ui.button("Delete", Ui.RED);
        delBtn.addActionListener(e -> delete());
        buttons.add(doneBtn);
        buttons.add(delBtn);
        buttons.add(new JLabel("Red text = due within 2 days or overdue"));

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        refresh();
    }

    public void refresh() {
        model.setRowCount(0);
        shown = controller.getSorted();
        for (Assignment a : shown) {
            long days = DateUtil.daysLeft(a.getDeadline());
            model.addRow(new Object[] {
                a.getId(), a.getTitle(), a.getCourse(), a.getDeadline().toString(),
                a.isCompleted() ? "-" : CountdownService.describe(days),
                a.getPriority().toString(), a.isCompleted() ? "Done" : "Pending"
            });
        }
    }

    private void addAssignment() {
        LocalDate deadline = DateUtil.parseDate(deadlineField.getText());
        if (deadline == null) {
            Ui.error(this, "The deadline must look like 2026-12-31.");
            return;
        }
        try {
            controller.add(titleField.getText(), courseField.getText(), deadline,
                    (Priority) priorityBox.getSelectedItem());
        } catch (IllegalArgumentException ex) {
            Ui.error(this, ex.getMessage());
            return;
        }
        titleField.setText("");
        courseField.setText("");
        refresh();
        onChange.run();
    }

    private void toggle() {
        int id = Ui.selectedId(table);
        if (id < 0) {
            Ui.error(this, "Select an assignment in the table first.");
            return;
        }
        controller.toggleComplete(id);
        refresh();
        onChange.run();
    }

    private void delete() {
        int id = Ui.selectedId(table);
        if (id < 0) {
            Ui.error(this, "Select an assignment in the table first.");
            return;
        }
        controller.delete(id);
        refresh();
        onChange.run();
    }
}
