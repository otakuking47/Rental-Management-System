package prgproject.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window after login.
 * Hosts all management panels in a JTabbedPane.
 * UI layer only — no SQL, no business logic.
 */
public class DashboardForm extends JFrame {

    public DashboardForm() {
        initComponents();
    }

    private void initComponents() {
        setTitle("James Property Holdings – Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);

        // ── menu bar ────────────────────────────────────────────────────────
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> handleLogout());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // ── header ──────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 40, 55));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel titleLabel = new JLabel("James Property Holdings – Rental Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        logoutButton.setToolTipText("Log out and return to the login screen (Ctrl+L)");
        logoutButton.addActionListener(e -> handleLogout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Keyboard shortcut: Ctrl+L
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L,
                java.awt.event.InputEvent.CTRL_DOWN_MASK), "logout");
        getRootPane().getActionMap().put("logout", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { handleLogout(); }
        });

        // ── tabbed pane ──────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabs.addTab("🏠  Properties",  new PropertyPanel());
        tabs.addTab("👤  Tenants",     new TenantPanel());
        tabs.addTab("📋  Leases",      new LeasePanel());
        tabs.addTab("💳  Payments",    new PaymentPanel());
        tabs.addTab("📊  Reports",     new ReportPanel());

        // ── layout ──────────────────────────────────────────────────────────
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrom().setVisible(true);
            dispose();
        }
    }
}
