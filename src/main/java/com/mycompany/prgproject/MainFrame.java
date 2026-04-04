package main.java.com.mycompany.prgproject;

import main.java.com.mycompany.prgproject.TenantDAO;
import main.java.com.mycompany.prgproject.TenantManage;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private TenantDAO dao = new TenantDAO();

    public MainFrame() {
        setTitle("James Property Rental System - Tenant Management");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton btnManageTenants = new JButton("Manage Tenants");
        btnManageTenants.addActionListener(e -> showTenants());

        JPanel panel = new JPanel();
        panel.add(btnManageTenants);
        add(panel, BorderLayout.CENTER);
    }

    private void showTenants() {
        List<TenantManage> tenants = dao.getAllTenants();
        StringBuilder sb = new StringBuilder("Tenants in Database:\n\n");
        for (TenantManage t : tenants) {
            sb.append(t).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}