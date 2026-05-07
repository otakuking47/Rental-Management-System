package prgproject.ui;

import javax.swing.*;
import java.awt.*;
import prgproject.DAO.AdminDao;

/**
 * Login form — UI layer only. On success, opens DashboardForm and disposes this
 * window.
 */
public class LoginFrom extends JFrame {

    private JTextField userField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrom() {
        initComponents();
    }

    private void initComponents() {
        setTitle("James Property Rental – Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 260);
        setResizable(false);
        setLocationRelativeTo(null);

        // ── root panel ──────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(30, 40, 55));

        // ── header ──────────────────────────────────────────────────────────
        JLabel header = new JLabel("Property Rental System", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(22, 0, 18, 0));
        root.add(header, BorderLayout.NORTH);

        // ── form panel ──────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(30, 40, 55));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        form.add(userLabel, gbc);

        userField = new JTextField(16);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        form.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        form.add(passLabel, gbc);

        passwordField = new JPasswordField(16);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        form.add(passwordField, gbc);

        root.add(form, BorderLayout.CENTER);

        // ── button panel ─────────────────────────────────────────────────────
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());

        // Allow Enter key to submit
        getRootPane().setDefaultButton(loginButton);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(30, 40, 55));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 20, 0));
        btnPanel.add(loginButton);
        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void handleLogin() {
        String username = userField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter both username and password.",
                        "Missing Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean valid = true; //AdminDao.authenticate(username, password);

            if (valid) {
                new DashboardForm().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.",
                        "Access Denied", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new LoginFrom().setVisible(true));
    }
}
