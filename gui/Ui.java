package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.util.function.IntPredicate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Colours, rounded cards, progress rings and small helpers shared by all screens. */
final class Ui {
    static final Color BG = new Color(0xF4, 0xF6, 0xFB);
    static final Color SIDEBAR = new Color(0x14, 0x1A, 0x3A);
    static final Color NAVY = new Color(0x1B, 0x22, 0x4B);
    static final Color INDIGO = new Color(0x4F, 0x46, 0xE5);
    static final Color BLUE = INDIGO;
    static final Color GREEN = new Color(0x22, 0xA0, 0x6B);
    static final Color RED = new Color(0xE0, 0x4F, 0x5F);
    static final Color ORANGE = new Color(0xF0, 0x8A, 0x24);
    static final Color PINK = new Color(0xE9, 0x5F, 0xA5);

    private static boolean themed;

    private Ui() { }

    static void installTheme() {
        if (themed) {
            return;
        }
        themed = true;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // keep the default look
        }
        FontUIResource f = new FontUIResource(new Font("Segoe UI", Font.PLAIN, 13));
        for (String k : new String[] {"Label.font", "Button.font", "TextField.font", "PasswordField.font",
            "ComboBox.font", "Table.font", "TableHeader.font", "CheckBox.font", "List.font", "TitledBorder.font"}) {
            UIManager.put(k, f);
        }
        UIManager.put("Panel.background", BG);
        UIManager.put("CheckBox.background", BG);
        UIManager.put("OptionPane.background", BG);
        UIManager.put("TitledBorder.titleColor", NAVY);
        UIManager.put("Table.selectionBackground", new Color(0xE0, 0xE3, 0xFF));
        UIManager.put("Table.selectionForeground", NAVY);
    }

    static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
    }

    static JTable table(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(28);
        t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setGridColor(new Color(0xE6, 0xE8, 0xF0));
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setFont(t.getTableHeader().getFont().deriveFont(Font.BOLD));
        return t;
    }

    static JButton button(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return b;
    }

    static JPanel form(String title) {
        JPanel p = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 6));
        p.setBorder(BorderFactory.createTitledBorder(title));
        return p;
    }

    /** Relative column widths, for example weights(table, 0.5, 3, 1). */
    static void weights(JTable t, double... w) {
        for (int i = 0; i < w.length && i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth((int) (w[i] * 100));
        }
    }

    static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Please check", JOptionPane.WARNING_MESSAGE);
    }

    static int selectedId(JTable t) {
        int row = t.getSelectedRow();
        if (row < 0) {
            return -1;
        }
        return Integer.parseInt(String.valueOf(t.getValueAt(row, 0)));
    }

    static void useRenderer(JTable t, RowRenderer r) {
        t.setDefaultRenderer(Object.class, r);
    }

    static void aa(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /** White rounded card with a soft shadow and an optional pastel gradient on top. */
    static class Card extends JPanel {
        private static final long serialVersionUID = 1L;
        private boolean hover;
        private Color accent;

        Card(LayoutManager lm) {
            super(lm);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 24));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        Card accent(Color c) {
            accent = c;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aa(g2);
            int w = getWidth() - 5;
            int h = getHeight() - 6;
            for (int i = 0; i < 4; i++) {
                g2.setColor(new Color(30, 40, 90, (hover ? 18 : 10) - i * 2));
                g2.fill(new RoundRectangle2D.Float(i, i + 2, w + 4 - i, h + 4 - i, 26, 26));
            }
            RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w, h, 24, 24);
            g2.setColor(Color.WHITE);
            g2.fill(shape);
            if (accent != null) {
                g2.setClip(shape);
                g2.setPaint(new GradientPaint(0, 0, new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 70),
                        0, h * 0.75f, new Color(255, 255, 255, 0)));
                g2.fillRect(0, 0, w, h);
                g2.setClip(null);
            }
            if (hover) {
                g2.setColor(new Color(0x4F, 0x46, 0xE5, 110));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0.75f, 0.75f, w - 1.5f, h - 1.5f, 24, 24));
            }
            g2.dispose();
        }
    }

    /** Circular progress ring with a percentage in the middle. */
    static class Ring extends JComponent {
        private static final long serialVersionUID = 1L;
        private final Color color;
        private double value;
        private String text = "";

        Ring(Color color) {
            this.color = color;
            setPreferredSize(new Dimension(72, 72));
        }

        void set(double v, String t) {
            value = Math.max(0, Math.min(1, v));
            text = t;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aa(g2);
            int s = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - s) / 2;
            int y = (getHeight() - s) / 2;
            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(0xE8, 0xEA, 0xF3));
            g2.drawOval(x, y, s, s);
            g2.setColor(color);
            g2.draw(new Arc2D.Double(x, y, s, s, 90, -360 * value, Arc2D.OPEN));
            g2.setColor(NAVY);
            g2.setFont(getFont().deriveFont(Font.BOLD, 12f));
            int tw = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, (getWidth() - tw) / 2, getHeight() / 2 + 4);
            g2.dispose();
        }
    }

    /** A FlowLayout that moves items to a new row when the window is too narrow. */
    static class WrapLayout extends FlowLayout {
        private static final long serialVersionUID = 1L;

        WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return size(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension d = size(target, false);
            d.width -= getHgap() + 1;
            return d;
        }

        private Dimension size(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                Container c = target;
                while (c.getSize().width == 0 && c.getParent() != null) {
                    c = c.getParent();
                }
                int targetWidth = c.getSize().width == 0 ? Integer.MAX_VALUE : c.getSize().width;
                Insets in = target.getInsets();
                int extra = in.left + in.right + getHgap() * 2;
                int maxWidth = targetWidth - extra;
                Dimension dim = new Dimension(0, 0);
                int rowW = 0;
                int rowH = 0;
                for (int i = 0; i < target.getComponentCount(); i++) {
                    Component m = target.getComponent(i);
                    if (!m.isVisible()) {
                        continue;
                    }
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if (rowW + d.width > maxWidth && rowW > 0) {
                        addRow(dim, rowW, rowH);
                        rowW = 0;
                        rowH = 0;
                    }
                    if (rowW != 0) {
                        rowW += getHgap();
                    }
                    rowW += d.width;
                    rowH = Math.max(rowH, d.height);
                }
                addRow(dim, rowW, rowH);
                dim.width += extra;
                dim.height += in.top + in.bottom + getVgap() * 2;
                if (SwingUtilities.getAncestorOfClass(JScrollPane.class, target) != null && target.isValid()) {
                    dim.width -= getHgap() + 1;
                }
                return dim;
            }
        }

        private void addRow(Dimension dim, int rowW, int rowH) {
            dim.width = Math.max(dim.width, rowW);
            if (dim.height > 0) {
                dim.height += getVgap();
            }
            dim.height += rowH;
        }
    }

    /** Round coloured badge with a letter inside. */
    static class Badge extends JComponent {
        private static final long serialVersionUID = 1L;
        private final String letter;
        private final Color color;

        Badge(String letter, Color color, int size) {
            this.letter = letter;
            this.color = color;
            setPreferredSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aa(g2);
            g2.setColor(color);
            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.setColor(Color.WHITE);
            g2.setFont(getFont().deriveFont(Font.BOLD, getHeight() * 0.45f));
            int tw = g2.getFontMetrics().stringWidth(letter);
            g2.drawString(letter, (getWidth() - tw) / 2, getHeight() / 2 + (int) (getHeight() * 0.16));
            g2.dispose();
        }
    }

    /** Colours the priority column and greys out finished rows / reddens urgent rows. */
    static class RowRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final int priorityCol;
        private final IntPredicate dim;
        private final IntPredicate alert;

        RowRenderer(int priorityCol, IntPredicate dim, IntPredicate alert) {
            this.priorityCol = priorityCol;
            this.dim = dim;
            this.alert = alert;
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                       boolean focus, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, value, selected, focus, row, col);
            if (selected) {
                return c;
            }
            c.setBackground(Color.WHITE);
            c.setForeground(Color.BLACK);
            if (dim.test(row)) {
                c.setForeground(Color.GRAY);
            } else if (alert.test(row)) {
                c.setForeground(new Color(0xB0, 0x20, 0x20));
            }
            if (col == priorityCol) {
                String s = String.valueOf(value);
                if ("High".equals(s)) {
                    c.setBackground(new Color(0xFF, 0xD6, 0xD6));
                } else if ("Medium".equals(s)) {
                    c.setBackground(new Color(0xFF, 0xF1, 0xC9));
                } else if ("Low".equals(s)) {
                    c.setBackground(new Color(0xD8, 0xF2, 0xD8));
                }
            }
            return c;
        }
    }
}
