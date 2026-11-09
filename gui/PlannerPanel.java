package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controller.PlannerController;
import model.Priority;
import model.StudyTask;
import util.DateUtil;

/** Daily study planner. */
public class PlannerPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final PlannerController controller;
    private final Runnable onChange;
    private final DefaultTableModel model = Ui.model("No.", "Task", "Date", "Start", "Priority", "Status");
    private final JTable table = Ui.table(model);
    private final JTextField taskField = new JTextField(18);
    private final JTextField dateField = new JTextField(10);
    private final JTextField timeField = new JTextField("18:00", 5);
    private final JComboBox<Priority> priorityBox = new JComboBox<Priority>(Priority.values());
    private final JCheckBox todayOnly = new JCheckBox("Show today only");
    private List<StudyTask> shown = new ArrayList<StudyTask>();

    public PlannerPanel(PlannerController controller, Runnable onChange) {
        this.controller = controller;
        this.onChange = onChange;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Ui.weights(table, 0.5, 3.5, 1.4, 1, 1, 1);

        dateField.setText(LocalDate.now().toString());
        priorityBox.setSelectedItem(Priority.MEDIUM);

        JPanel form = Ui.form("Add study task");
        form.add(new JLabel("Task"));
        form.add(taskField);
        form.add(new JLabel("Date (yyyy-MM-dd)"));
        form.add(dateField);
        form.add(new JLabel("Start (HH:mm)"));
        form.add(timeField);
        form.add(new JLabel("Priority"));
        form.add(priorityBox);
        JButton addBtn = Ui.button("Add", Ui.BLUE);
        addBtn.addActionListener(e -> addTask());
        form.add(addBtn);

        Ui.useRenderer(table, new Ui.RowRenderer(4,
                r -> r < shown.size() && shown.get(r).isDone(),
                r -> r < shown.size() && !shown.get(r).isDone()
                        && shown.get(r).getDate().isBefore(LocalDate.now())));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JButton doneBtn = Ui.button("Mark done / undo", Ui.GREEN);
        doneBtn.addActionListener(e -> toggle());
        JButton delBtn = Ui.button("Delete", Ui.RED);
        delBtn.addActionListener(e -> delete());
        todayOnly.addActionListener(e -> refresh());
        buttons.add(doneBtn);
        buttons.add(delBtn);
        buttons.add(todayOnly);
        buttons.add(new JLabel("Red text = not finished and the day has passed"));

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        refresh();
    }

    public void refresh() {
        model.setRowCount(0);
        shown = todayOnly.isSelected() ? controller.getForDate(LocalDate.now()) : controller.getSorted();
        for (StudyTask t : shown) {
            model.addRow(new Object[] {
                t.getId(), t.getTask(), t.getDate().toString(), t.getStartTime().toString(),
                t.getPriority().toString(), t.isDone() ? "Done" : "Pending"
            });
        }
    }

    private void addTask() {
        LocalDate date = DateUtil.parseDate(dateField.getText());
        LocalTime time = DateUtil.parseTime(timeField.getText());
        if (date == null) {
            Ui.error(this, "The date must look like 2026-12-31.");
            return;
        }
        if (time == null) {
            Ui.error(this, "The start time must look like 18:30 (24 hour).");
            return;
        }
        try {
            controller.add(taskField.getText(), date, time, (Priority) priorityBox.getSelectedItem());
        } catch (IllegalArgumentException ex) {
            Ui.error(this, ex.getMessage());
            return;
        }
        taskField.setText("");
        refresh();
        onChange.run();
    }

    private void toggle() {
        int id = Ui.selectedId(table);
        if (id < 0) {
            Ui.error(this, "Select a task in the table first.");
            return;
        }
        controller.toggleDone(id);
        refresh();
        onChange.run();
    }

    private void delete() {
        int id = Ui.selectedId(table);
        if (id < 0) {
            Ui.error(this, "Select a task in the table first.");
            return;
        }
        controller.delete(id);
        refresh();
        onChange.run();
    }
}
