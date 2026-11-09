package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import controller.AssignmentController;
import controller.CGPAController;
import controller.ExamController;
import controller.PlannerController;
import controller.StudentController;
import model.Assignment;
import model.Exam;
import model.Student;
import model.StudyTask;
import service.CountdownService;
import util.DateUtil;
import util.FileHandler;

/** Main window: dark sidebar on the left, card-style pages on the right. */
public class DashboardFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    static {
        Ui.installTheme();
    }

    private static final String[] TITLES = {"Home", "Assignments", "Exam Countdown", "Study Planner", "CGPA Calculator"};
    private static final String[] SUBS = {"Let's get ready for your classes", "Track deadlines and priorities",
        "Days left for every exam", "Plan your study day", "Marks give grade, GPA and CGPA"};

    private final StudentController student = new StudentController();
    private final AssignmentController assignments = new AssignmentController();
    private final ExamController exams = new ExamController();
    private final PlannerController planner = new PlannerController();
    private final CGPAController cgpa = new CGPAController();
    private final CardLayout cards = new CardLayout();
    private final JPanel pages = new JPanel(cards);
    private final NavButton[] nav = new NavButton[TITLES.length];
    private final JLabel pageTitle = new JLabel();
    private final JLabel pageSub = new JLabel();
    private final HomePanel home = new HomePanel();
    private final CGPAPanel cgpaPanel;

    public DashboardFrame() {
        super("Smart Student Survival System");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        cgpaPanel = new CGPAPanel(cgpa, student, home::refresh);

        pages.setOpaque(false);
        pages.add(home, TITLES[0]);
        pages.add(new AssignmentPanel(assignments, home::refresh), TITLES[1]);
        pages.add(new ExamPanel(exams, home::refresh), TITLES[2]);
        pages.add(new PlannerPanel(planner, home::refresh), TITLES[3]);
        pages.add(cgpaPanel, TITLES[4]);
        pages.setBorder(BorderFactory.createEmptyBorder(4, 22, 18, 22));

        pageTitle.setFont(pageTitle.getFont().deriveFont(Font.BOLD, 26f));
        pageTitle.setForeground(Ui.NAVY);
        pageSub.setForeground(Color.GRAY);
        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        titles.add(pageTitle);
        titles.add(pageSub);
        JLabel date = new JLabel("Today: " + LocalDate.now());
        date.setForeground(Color.GRAY);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 28, 8, 28));
        header.add(titles, BorderLayout.WEST);
        header.add(date, BorderLayout.EAST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Ui.BG);
        main.add(header, BorderLayout.NORTH);
        main.add(pages, BorderLayout.CENTER);

        add(buildSidebar(), BorderLayout.WEST);
        add(main, BorderLayout.CENTER);
        setSize(new Dimension(1180, 740));
        setLocationRelativeTo(null);
        showPage(0);
    }

    public void showPage(int index) {
        for (int i = 0; i < nav.length; i++) {
            nav[i].setActive(i == index);
        }
        home.refresh();
        cgpaPanel.refresh();
        pageTitle.setText(TITLES[index]);
        String name = student.hasProfile() ? student.get().getName() : "";
        pageSub.setText(index == 0 && !name.isEmpty() ? "Hi, " + name + "! " + SUBS[0] : SUBS[index]);
        cards.show(pages, TITLES[index]);
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(Ui.SIDEBAR);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(22, 16, 20, 16));
        side.setPreferredSize(new Dimension(236, 100));

        JPanel logo = new JPanel(new BorderLayout(10, 0));
        logo.setOpaque(false);
        logo.setMaximumSize(new Dimension(220, 48));
        logo.setAlignmentX(0f);
        logo.add(new Ui.Badge("S", Ui.INDIGO, 40), BorderLayout.WEST);
        JLabel app = new JLabel("<html><b>Smart Student</b><br>Survival System</html>");
        app.setForeground(Color.WHITE);
        logo.add(app, BorderLayout.CENTER);
        side.add(logo);
        side.add(Box.createVerticalStrut(30));

        for (int i = 0; i < TITLES.length; i++) {
            final int idx = i;
            nav[i] = new NavButton(TITLES[i]);
            nav[i].setAlignmentX(0f);
            nav[i].addActionListener(e -> showPage(idx));
            side.add(nav[i]);
            side.add(Box.createVerticalStrut(6));
        }
        side.add(Box.createVerticalGlue());

        Student s = student.get();
        JLabel who = new JLabel(s.getName().isEmpty() ? "Student" : s.getName());
        who.setForeground(Color.WHITE);
        who.setFont(who.getFont().deriveFont(Font.BOLD, 14f));
        who.setAlignmentX(0f);
        JLabel id = new JLabel(s.getStudentId().isEmpty() ? " " : "ID: " + s.getStudentId());
        id.setForeground(new Color(0xB4, 0xBC, 0xDE));
        id.setAlignmentX(0f);
        JButton logout = Ui.button("Log out", Ui.RED);
        logout.setAlignmentX(0f);
        logout.setMaximumSize(new Dimension(204, 38));
        logout.addActionListener(e -> {
            dispose();
            FileHandler.setDataDir("data");
            new LoginFrame().setVisible(true);
        });
        side.add(who);
        side.add(id);
        side.add(Box.createVerticalStrut(10));
        side.add(logout);
        return side;
    }

    /** Sidebar menu button with a rounded highlight. */
    private static class NavButton extends JButton {
        private static final long serialVersionUID = 1L;
        private boolean active;

        NavButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 10));
            setFont(getFont().deriveFont(Font.BOLD, 13.5f));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(204, 42));
            setPreferredSize(new Dimension(204, 42));
            setActive(false);
        }

        void setActive(boolean a) {
            active = a;
            setForeground(a ? Color.WHITE : new Color(0xB4, 0xBC, 0xDE));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Ui.aa(g2);
            if (active) {
                g2.setColor(Ui.INDIGO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(255, 255, 255, 26));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Home page: four stat cards with rings and two list cards. */
    private class HomePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private final JLabel pendingValue = new JLabel("0");
        private final JLabel pendingSub = new JLabel(" ");
        private final JLabel examValue = new JLabel("None");
        private final JLabel examSub = new JLabel(" ");
        private final JLabel tasksValue = new JLabel("0 / 0");
        private final JLabel tasksSub = new JLabel("tasks done");
        private final JLabel cgpaValue = new JLabel("0.00");
        private final JLabel cgpaSub = new JLabel("out of 4.00");
        private final Ui.Ring pendingRing = new Ui.Ring(Ui.INDIGO);
        private final Ui.Ring tasksRing = new Ui.Ring(Ui.GREEN);
        private final Ui.Ring cgpaRing = new Ui.Ring(Ui.PINK);
        private final DefaultListModel<String> deadlineModel = new DefaultListModel<String>();
        private final DefaultListModel<String> planModel = new DefaultListModel<String>();

        HomePanel() {
            setOpaque(false);
            setLayout(new BorderLayout(0, 16));
            JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
            stats.setOpaque(false);
            stats.add(stat("Pending", Ui.INDIGO, pendingValue, pendingSub, pendingRing));
            stats.add(stat("Next exam", Ui.ORANGE, examValue, examSub, null));
            stats.add(stat("Study today", Ui.GREEN, tasksValue, tasksSub, tasksRing));
            stats.add(stat("CGPA", Ui.PINK, cgpaValue, cgpaSub, cgpaRing));
            stats.setPreferredSize(new Dimension(100, 130));

            JPanel lists = new JPanel(new GridLayout(1, 2, 16, 0));
            lists.setOpaque(false);
            lists.add(listCard("Upcoming deadlines", deadlineModel));
            lists.add(listCard("Today's study plan", planModel));
            add(stats, BorderLayout.NORTH);
            add(lists, BorderLayout.CENTER);
        }

        private Ui.Card stat(String title, Color color, JLabel value, JLabel sub, Ui.Ring ring) {
            Ui.Card c = new Ui.Card(new BorderLayout(8, 0)).accent(color);
            JLabel t = new JLabel(title);
            t.setForeground(Color.GRAY);
            value.setFont(value.getFont().deriveFont(Font.BOLD, 24f));
            value.setForeground(color);
            sub.setForeground(Color.GRAY);
            JPanel text = new JPanel(new GridLayout(3, 1, 0, 2));
            text.setOpaque(false);
            text.add(t);
            text.add(value);
            text.add(sub);
            c.add(text, BorderLayout.CENTER);
            if (ring != null) {
                c.add(ring, BorderLayout.EAST);
            }
            return c;
        }

        private Ui.Card listCard(String title, DefaultListModel<String> model) {
            Ui.Card c = new Ui.Card(new BorderLayout(0, 8));
            JLabel t = new JLabel(title);
            t.setFont(t.getFont().deriveFont(Font.BOLD, 16f));
            t.setForeground(Ui.NAVY);
            JList<String> list = new JList<String>(model);
            list.setOpaque(false);
            list.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
            JScrollPane sp = new JScrollPane(list);
            sp.setBorder(null);
            sp.setOpaque(false);
            sp.getViewport().setOpaque(false);
            c.add(t, BorderLayout.NORTH);
            c.add(sp, BorderLayout.CENTER);
            return c;
        }

        void refresh() {
            int total = assignments.getSorted().size();
            int pending = assignments.pendingCount();
            pendingValue.setText(String.valueOf(pending));
            pendingSub.setText("of " + total + " tasks");
            int doneA = total - pending;
            pendingRing.set(total == 0 ? 0 : (double) doneA / total, total == 0 ? "-" : (doneA * 100 / total) + "%");

            Exam next = CountdownService.nextExam(exams.getSorted());
            if (next == null) {
                examValue.setText("None");
                examSub.setText("no upcoming exam");
            } else {
                examValue.setText(next.getSubject());
                examSub.setText(CountdownService.describe(DateUtil.daysLeft(next.getExamDate())));
            }

            List<StudyTask> today = planner.getForDate(LocalDate.now());
            int done = 0;
            planModel.clear();
            for (StudyTask t : today) {
                if (t.isDone()) {
                    done++;
                }
                planModel.addElement((t.isDone() ? "[x]  " : "[ ]  ") + t.getStartTime() + "   "
                        + t.getTask() + "   (" + t.getPriority() + ")");
            }
            if (today.isEmpty()) {
                planModel.addElement("Nothing planned for today. Add a task in Study Planner.");
            }
            tasksValue.setText(done + " / " + today.size());
            tasksRing.set(today.isEmpty() ? 0 : (double) done / today.size(),
                    today.isEmpty() ? "-" : (done * 100 / today.size()) + "%");

            double g = cgpa.cgpa();
            cgpaValue.setText(String.format(Locale.US, "%.2f", g));
            cgpaRing.set(g / 4.0, (int) Math.round(g / 4.0 * 100) + "%");

            deadlineModel.clear();
            int shown = 0;
            for (Assignment a : assignments.getSorted()) {
                if (a.isCompleted() || shown >= 6) {
                    continue;
                }
                deadlineModel.addElement(a.getTitle() + "  [" + a.getCourse() + "]  -  "
                        + CountdownService.describe(DateUtil.daysLeft(a.getDeadline()))
                        + "  (" + a.getPriority() + ")");
                shown++;
            }
            if (shown == 0) {
                deadlineModel.addElement("No pending assignments. Nice!");
            }
        }
    }
}
