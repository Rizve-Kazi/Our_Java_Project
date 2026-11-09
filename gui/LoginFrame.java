package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import controller.AuthController;
import controller.StudentController;
import util.FileHandler;

/** Login / create-account screen. */
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    static {
        Ui.installTheme();
    }

    private final AuthController auth = new AuthController();
    private final JTextField nameField = new JTextField();
    private final JTextField idField = new JTextField();
    private final JTextField userField = new JTextField();
    private final JPasswordField passField = new JPasswordField();
    private final JPasswordField confirmField = new JPasswordField();
    private final JCheckBox showPass = new JCheckBox("Show password");
    private final JLabel title = new JLabel();
    private final JLabel subtitle = new JLabel();
    private final JLabel error = new JLabel(" ");
    private final JButton mainBtn = Ui.button("Log in", Ui.INDIGO);
    private final JButton switchBtn = new JButton();
    private final List<JComponent> registerOnly = new ArrayList<JComponent>();
    private final Ui.Card cardBox = new Ui.Card(new BorderLayout());
    private boolean registerMode;

    public LoginFrame() {
        super("Smart Student Survival System - Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(560, 800));
        setLocationRelativeTo(null);

        JPanel backdrop = new JPanel(new GridBagLayout()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(0xE6, 0xEA, 0xFF), getWidth(), getHeight(), new Color(0xFC, 0xE8, 0xF4)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        Ui.Card card = cardBox;
        card.setBorder(BorderFactory.createEmptyBorder(26, 34, 30, 38));
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        JComponent lock = new LockBadge();
        lock.setAlignmentX(0.5f);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setForeground(Ui.NAVY);
        title.setAlignmentX(0.5f);
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(0.5f);
        col.add(lock);
        col.add(Box.createVerticalStrut(10));
        col.add(title);
        col.add(Box.createVerticalStrut(4));
        col.add(subtitle);
        col.add(Box.createVerticalStrut(14));

        registerOnly.add(row("Full name", nameField));
        registerOnly.add(row("Student ID", idField));
        col.add(registerOnly.get(0));
        col.add(registerOnly.get(1));
        col.add(row("Username", userField));
        col.add(row("Password", passField));
        JComponent confirmRow = row("Confirm password", confirmField);
        registerOnly.add(confirmRow);
        col.add(confirmRow);

        showPass.setOpaque(false);
        showPass.setAlignmentX(0f);
        char echo = passField.getEchoChar();
        showPass.addActionListener(e -> {
            char c = showPass.isSelected() ? (char) 0 : echo;
            passField.setEchoChar(c);
            confirmField.setEchoChar(c);
        });
        JPanel showRow = new JPanel(new BorderLayout());
        showRow.setOpaque(false);
        showRow.add(showPass, BorderLayout.WEST);
        showRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        col.add(showRow);

        error.setForeground(Ui.RED);
        error.setAlignmentX(0.5f);
        col.add(Box.createVerticalStrut(6));
        col.add(error);
        col.add(Box.createVerticalStrut(6));

        mainBtn.setAlignmentX(0.5f);
        mainBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        mainBtn.setFont(mainBtn.getFont().deriveFont(Font.BOLD, 14f));
        mainBtn.addActionListener(e -> submit());
        col.add(mainBtn);

        switchBtn.setContentAreaFilled(false);
        switchBtn.setBorderPainted(false);
        switchBtn.setFocusPainted(false);
        switchBtn.setForeground(Ui.INDIGO);
        switchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchBtn.setAlignmentX(0.5f);
        switchBtn.addActionListener(e -> showRegister(!registerMode));
        col.add(Box.createVerticalStrut(8));
        col.add(switchBtn);

        card.add(col, BorderLayout.CENTER);
        backdrop.add(card);
        setContentPane(backdrop);
        getRootPane().setDefaultButton(mainBtn);
        showRegister(false);
    }

    private JComponent row(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(Ui.NAVY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD5, 0xD9, 0xEA), 1, true),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        return p;
    }

    /** Switch between the Log in form and the Create account form. */
    public void showRegister(boolean register) {
        registerMode = register;
        for (JComponent c : registerOnly) {
            c.setVisible(register);
        }
        title.setText(register ? "Create your account" : "Welcome back");
        subtitle.setText(register ? "Enter your details to get started" : "Log in to your student dashboard");
        mainBtn.setText(register ? "Create account" : "Log in");
        switchBtn.setText(register ? "Already have an account? Log in" : "New here? Create an account");
        error.setText(" ");
        cardBox.setPreferredSize(new Dimension(430, register ? 700 : 500));
        revalidate();
        repaint();
    }

    private void submit() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());
        try {
            if (registerMode) {
                if (nameField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("Please enter your full name.");
                }
                auth.register(user, pass, new String(confirmField.getPassword()));
                FileHandler.setDataDir(AuthController.userDir(user));
                new StudentController().save(nameField.getText(), idField.getText());
            } else {
                if (!auth.login(user, pass)) {
                    throw new IllegalArgumentException("Wrong username or password.");
                }
                FileHandler.setDataDir(AuthController.userDir(user));
            }
        } catch (IllegalArgumentException ex) {
            error.setText("<html><center>" + ex.getMessage() + "</center></html>");
            return;
        }
        dispose();
        new DashboardFrame().setVisible(true);
    }

    /** The round lock icon at the top of the card. */
    private static class LockBadge extends JComponent {
        private static final long serialVersionUID = 1L;

        LockBadge() {
            setPreferredSize(new Dimension(76, 76));
            setMaximumSize(new Dimension(76, 76));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Ui.aa(g2);
            g2.setPaint(new GradientPaint(0, 0, new Color(0x6D, 0x66, 0xF2), 76, 76, new Color(0xA8, 0x6B, 0xF0)));
            g2.fillOval(0, 0, 75, 75);
            g2.setColor(Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(4f));
            g2.draw(new Arc2D.Double(27, 17, 21, 24, 0, 180, Arc2D.OPEN));
            g2.fill(new RoundRectangle2D.Float(23, 36, 29, 22, 8, 8));
            g2.setColor(new Color(0x6D, 0x66, 0xF2));
            g2.fillOval(35, 43, 6, 6);
            g2.dispose();
        }
    }
}
