package prgproject.ui;

import prgproject.model.*;
import prgproject.services.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Property management panel — Houses, Apartments, and Townhouses.
 * Each type has its own sub-tab.
 * UI layer only — calls service methods exclusively.
 */
public class PropertyPanel extends JPanel {

    public PropertyPanel() {
        setLayout(new BorderLayout());
        JTabbedPane subTabs = new JTabbedPane();
        subTabs.addTab("Houses",     new HouseSubPanel());
        subTabs.addTab("Apartments", new ApartmentSubPanel());
        subTabs.addTab("Townhouses", new TownhouseSubPanel());
        add(subTabs, BorderLayout.CENTER);
    }

    // ════════════════════════════════════════════════════════════════════════
    // HOUSE SUB-PANEL
    // ════════════════════════════════════════════════════════════════════════
    static class HouseSubPanel extends JPanel {

        private final HouseService service = new HouseService();
        private JTable table;
        private DefaultTableModel model;
        private JTextField idField, floorSizeField, addressField,
                           locationField, marketField, rentalField, plotField;
        private JCheckBox availBox;

        HouseSubPanel() {
            setLayout(new BorderLayout(8, 8));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            initTable();
            initForm();
            loadTable();
        }

        private void initTable() {
            String[] cols = {"ID","Floor Size","Address","Location",
                             "Market Value","Rental","Plot Size","Available"};
            model = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(
                e -> { if (!e.getValueIsAdjusting()) populateForm(); });
            add(new JScrollPane(table), BorderLayout.CENTER);
        }

        private void loadTable() {
            model.setRowCount(0);
            try {
                List<House> list = service.getAllHouses();
                for (House h : list) {
                    model.addRow(new Object[]{
                        h.getID(), h.getFloorSize(), h.getFullAddress(),
                        h.getLocation(), h.getMarketValue(),
                        h.getRentalCost(), h.getPlotSize(), h.isAvailability()
                    });
                }
            } catch (RuntimeException ignored) {}
        }

        private void populateForm() {
            int row = table.getSelectedRow();
            if (row < 0) return;
            idField.setText(model.getValueAt(row, 0).toString());
            floorSizeField.setText(model.getValueAt(row, 1).toString());
            addressField.setText(model.getValueAt(row, 2).toString());
            locationField.setText(model.getValueAt(row, 3).toString());
            marketField.setText(model.getValueAt(row, 4).toString());
            rentalField.setText(model.getValueAt(row, 5).toString());
            plotField.setText(model.getValueAt(row, 6).toString());
            availBox.setSelected(Boolean.parseBoolean(model.getValueAt(row, 7).toString()));
        }

        private void initForm() {
            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(BorderFactory.createTitledBorder("House Details"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 8, 3, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            idField        = new JTextField(8);
            floorSizeField = new JTextField(10);
            addressField   = new JTextField(20);
            locationField  = new JTextField(14);
            marketField    = new JTextField(10);
            rentalField    = new JTextField(10);
            plotField      = new JTextField(10);
            availBox       = new JCheckBox("Available");

            String[] labels = {"ID:","Floor Size:","Full Address:","Location:",
                               "Market Value (N$):","Rental Cost (N$):","Plot Size (m²):"};
            JComponent[] fields = {idField, floorSizeField, addressField, locationField,
                                   marketField, rentalField, plotField};
            for (int i = 0; i < labels.length; i++) {
                gbc.gridx = (i % 2) * 2;
                gbc.gridy = i / 2;
                gbc.weightx = 0.2;
                form.add(new JLabel(labels[i]), gbc);
                gbc.gridx++;
                gbc.weightx = 0.8;
                form.add(fields[i], gbc);
            }
            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
            form.add(availBox, gbc);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
            btns.add(makeBtn("Add",    new Color(39,174,96),  e -> handleAdd()));
            btns.add(makeBtn("Update", new Color(52,152,219), e -> handleUpdate()));
            btns.add(makeBtn("Delete", new Color(231,76,60),  e -> handleDelete()));
            btns.add(makeBtn("Clear",  new Color(127,140,141),e -> clearForm()));
            btns.add(makeBtn("Refresh",new Color(243,156,18), e -> loadTable()));

            gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
            form.add(btns, gbc);
            add(form, BorderLayout.SOUTH);
        }

        private void handleAdd() {
            try {
                service.saveHouse(buildHouse());
                JOptionPane.showMessageDialog(this, "House saved.");
                clearForm(); loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        private void handleUpdate() {
            if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this,"Select a house."); return; }
            try {
                service.updateHouse(buildHouse());
                JOptionPane.showMessageDialog(this, "House updated.");
                clearForm(); loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        private void handleDelete() {
            if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this,"Select a house."); return; }
            if (JOptionPane.showConfirmDialog(this,"Delete house?","Confirm",JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try {
                service.deleteHouse(Integer.parseInt(idField.getText().trim()));
                JOptionPane.showMessageDialog(this, "House deleted.");
                clearForm(); loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        private House buildHouse() {
            return new House(
                Integer.parseInt(idField.getText().trim()),
                floorSizeField.getText().trim(),
                addressField.getText().trim(),
                locationField.getText().trim(),
                Double.parseDouble(marketField.getText().trim()),
                Double.parseDouble(rentalField.getText().trim()),
                availBox.isSelected(),
                Double.parseDouble(plotField.getText().trim())
            );
        }
        private void clearForm() {
            for (JTextField f : new JTextField[]{idField,floorSizeField,addressField,
                    locationField,marketField,rentalField,plotField}) f.setText("");
            availBox.setSelected(false);
            table.clearSelection();
        }
        private JButton makeBtn(String t, Color bg, java.awt.event.ActionListener a) {
            JButton b = new JButton(t);
            b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFocusPainted(false); b.addActionListener(a); return b;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // APARTMENT SUB-PANEL
    // ════════════════════════════════════════════════════════════════════════
    static class ApartmentSubPanel extends JPanel {

        private final ApartmentService service = new ApartmentService();
        private JTable table;
        private DefaultTableModel model;
        private JTextField idField, floorSizeField, addressField, locationField,
                           marketField, rentalField, unitField, floorLevelField;
        private JCheckBox elevatorBox, backyardBox, availBox;

        ApartmentSubPanel() {
            setLayout(new BorderLayout(8, 8));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            initTable();
            initForm();
            loadTable();
        }

        private void initTable() {
            String[] cols = {"ID","Floor Size","Address","Location","Market Value",
                             "Rental","Unit No","Floor Level","Elevator","Backyard","Available"};
            model = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(
                e -> { if (!e.getValueIsAdjusting()) populateForm(); });
            add(new JScrollPane(table), BorderLayout.CENTER);
        }

        private void loadTable() {
            model.setRowCount(0);
            try {
                List<Apartment> list = service.getAllApartments();
                for (Apartment a : list) {
                    model.addRow(new Object[]{
                        a.getID(), a.getFloorSize(), a.getFullAddress(), a.getLocation(),
                        a.getMarketValue(), a.getRentalCost(), a.getUnitNo(),
                        a.getFloorLevel(), a.isHasElevator(), a.isHasBackyard(), a.isAvailability()
                    });
                }
            } catch (RuntimeException ignored) {}
        }

        private void populateForm() {
            int row = table.getSelectedRow();
            if (row < 0) return;
            idField.setText(model.getValueAt(row, 0).toString());
            floorSizeField.setText(model.getValueAt(row, 1).toString());
            addressField.setText(model.getValueAt(row, 2).toString());
            locationField.setText(model.getValueAt(row, 3).toString());
            marketField.setText(model.getValueAt(row, 4).toString());
            rentalField.setText(model.getValueAt(row, 5).toString());
            unitField.setText(model.getValueAt(row, 6).toString());
            floorLevelField.setText(model.getValueAt(row, 7).toString());
            elevatorBox.setSelected(Boolean.parseBoolean(model.getValueAt(row, 8).toString()));
            backyardBox.setSelected(Boolean.parseBoolean(model.getValueAt(row, 9).toString()));
            availBox.setSelected(Boolean.parseBoolean(model.getValueAt(row, 10).toString()));
        }

        private void initForm() {
            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(BorderFactory.createTitledBorder("Apartment Details"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 8, 3, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            idField         = new JTextField(8);
            floorSizeField  = new JTextField(10);
            addressField    = new JTextField(20);
            locationField   = new JTextField(14);
            marketField     = new JTextField(10);
            rentalField     = new JTextField(10);
            unitField       = new JTextField(6);
            floorLevelField = new JTextField(6);
            elevatorBox     = new JCheckBox("Elevator Access");
            backyardBox     = new JCheckBox("Has Backyard");
            availBox        = new JCheckBox("Available");

            Object[][] rows = {
                {"ID:", idField},          {"Floor Size:", floorSizeField},
                {"Full Address:", addressField}, {"Location:", locationField},
                {"Market Value (N$):", marketField}, {"Rental Cost (N$):", rentalField},
                {"Unit No:", unitField},   {"Floor Level:", floorLevelField}
            };

            int r = 0;
            for (Object[] row : rows) {
                gbc.gridx = (r % 2) * 2; gbc.gridy = r / 2;
                gbc.weightx = 0.2; form.add(new JLabel((String) row[0]), gbc);
                gbc.gridx++; gbc.weightx = 0.8; form.add((JComponent) row[1], gbc);
                r++;
            }
            JPanel checks = new JPanel(new FlowLayout(FlowLayout.LEFT));
            checks.add(elevatorBox); checks.add(backyardBox); checks.add(availBox);
            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
            form.add(checks, gbc);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
            btns.add(makeBtn("Add",    new Color(39,174,96),  e -> handleAdd()));
            btns.add(makeBtn("Update", new Color(52,152,219), e -> handleUpdate()));
            btns.add(makeBtn("Delete", new Color(231,76,60),  e -> handleDelete()));
            btns.add(makeBtn("Clear",  new Color(127,140,141),e -> clearForm()));
            btns.add(makeBtn("Refresh",new Color(243,156,18), e -> loadTable()));

            gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
            form.add(btns, gbc);
            add(form, BorderLayout.SOUTH);
        }

        private void handleAdd() {
            try {
                service.addApartment(buildApartment());
                JOptionPane.showMessageDialog(this, "Apartment saved.");
                clearForm(); loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        private void handleUpdate() {
            if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this,"Select an apartment."); return; }
            try {
                service.updateApartment(buildApartment());
                JOptionPane.showMessageDialog(this, "Apartment updated.");
                clearForm(); loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        private void handleDelete() {
            if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this,"Select an apartment."); return; }
            if (JOptionPane.showConfirmDialog(this,"Delete apartment?","Confirm",JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try {
                service.deleteApartment(Integer.parseInt(idField.getText().trim()));
                JOptionPane.showMessageDialog(this, "Apartment deleted.");
                clearForm(); loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        private Apartment buildApartment() {
            return new Apartment(
                Integer.parseInt(idField.getText().trim()),
                floorSizeField.getText().trim(),
                addressField.getText().trim(),
                locationField.getText().trim(),
                Double.parseDouble(marketField.getText().trim()),
                Double.parseDouble(rentalField.getText().trim()),
                availBox.isSelected(),
                Integer.parseInt(unitField.getText().trim()),
                Integer.parseInt(floorLevelField.getText().trim()),
                elevatorBox.isSelected(),
                backyardBox.isSelected()
            );
        }
        private void clearForm() {
            for (JTextField f : new JTextField[]{idField, floorSizeField, addressField,
                    locationField, marketField, rentalField, unitField, floorLevelField})
                f.setText("");
            elevatorBox.setSelected(false); backyardBox.setSelected(false); availBox.setSelected(false);
            table.clearSelection();
        }
        private JButton makeBtn(String t, Color bg, java.awt.event.ActionListener a) {
            JButton b = new JButton(t);
            b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFocusPainted(false); b.addActionListener(a); return b;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOWNHOUSE SUB-PANEL
    // ════════════════════════════════════════════════════════════════════════
    static class TownhouseSubPanel extends JPanel {

        private final TownHouseService service = new TownHouseService();
        private JTable table;
        private DefaultTableModel model;
        private JTextField idField, floorSizeField, addressField,
                           locationField, marketField, rentalField, unitField;
        private JCheckBox backyardBox, availBox;

        TownhouseSubPanel() {
            setLayout(new BorderLayout(8, 8));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            initTable();
            initForm();
            loadTable();
        }

        private void initTable() {
            String[] cols = {"ID","Floor Size","Address","Location",
                             "Market Value","Rental","Unit No","Backyard","Available"};
            model = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(
                e -> { if (!e.getValueIsAdjusting()) populateForm(); });
            add(new JScrollPane(table), BorderLayout.CENTER);
        }

        private void loadTable() {
            model.setRowCount(0);
            try {
                List<TownHouse> list = service.getAllTownHouses();
                for (TownHouse t : list) {
                    model.addRow(new Object[]{
                        t.getID(), t.getFloorSize(), t.getFullAddress(), t.getLocation(),
                        t.getMarketValue(), t.getRentalCost(), t.getUnitNo(),
                        t.isBackyard(), t.isAvailability()
                    });
                }
            } catch (RuntimeException ignored) {}
        }

        private void populateForm() {
            int row = table.getSelectedRow();
            if (row < 0) return;
            idField.setText(model.getValueAt(row, 0).toString());
            floorSizeField.setText(model.getValueAt(row, 1).toString());
            addressField.setText(model.getValueAt(row, 2).toString());
            locationField.setText(model.getValueAt(row, 3).toString());
            marketField.setText(model.getValueAt(row, 4).toString());
            rentalField.setText(model.getValueAt(row, 5).toString());
            unitField.setText(model.getValueAt(row, 6).toString());
            backyardBox.setSelected(Boolean.parseBoolean(model.getValueAt(row, 7).toString()));
            availBox.setSelected(Boolean.parseBoolean(model.getValueAt(row, 8).toString()));
        }

        private void initForm() {
            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(BorderFactory.createTitledBorder("Townhouse Details"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 8, 3, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            idField        = new JTextField(8);
            floorSizeField = new JTextField(10);
            addressField   = new JTextField(20);
            locationField  = new JTextField(14);
            marketField    = new JTextField(10);
            rentalField    = new JTextField(10);
            unitField      = new JTextField(6);
            backyardBox    = new JCheckBox("Has Backyard");
            availBox       = new JCheckBox("Available");

            Object[][] rows = {
                {"ID:", idField},                {"Floor Size:", floorSizeField},
                {"Full Address:", addressField},  {"Location:", locationField},
                {"Market Value (N$):", marketField}, {"Rental Cost (N$):", rentalField},
                {"Unit No:", unitField}
            };

            int r = 0;
            for (Object[] row : rows) {
                gbc.gridx = (r % 2) * 2; gbc.gridy = r / 2;
                gbc.weightx = 0.2; form.add(new JLabel((String) row[0]), gbc);
                gbc.gridx++; gbc.weightx = 0.8; form.add((JComponent) row[1], gbc);
                r++;
            }
            JPanel checks = new JPanel(new FlowLayout(FlowLayout.LEFT));
            checks.add(backyardBox); checks.add(availBox);
            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
            form.add(checks, gbc);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
            btns.add(makeBtn("Add",    new Color(39,174,96),  e -> handleAdd()));
            btns.add(makeBtn("Update", new Color(52,152,219), e -> handleUpdate()));
            btns.add(makeBtn("Delete", new Color(231,76,60),  e -> handleDelete()));
            btns.add(makeBtn("Clear",  new Color(127,140,141),e -> clearForm()));
            btns.add(makeBtn("Refresh",new Color(243,156,18), e -> loadTable()));

            gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
            form.add(btns, gbc);
            add(form, BorderLayout.SOUTH);
        }

        private void handleAdd() {
            try {
                service.saveTownHouse(buildTownHouse());
                JOptionPane.showMessageDialog(this, "Townhouse saved.");
                clearForm(); loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        private void handleUpdate() {
            if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this,"Select a townhouse."); return; }
            try {
                service.updateTownHouse(buildTownHouse());
                JOptionPane.showMessageDialog(this, "Townhouse updated.");
                clearForm(); loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        private void handleDelete() {
            if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this,"Select a townhouse."); return; }
            if (JOptionPane.showConfirmDialog(this,"Delete townhouse?","Confirm",JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try {
                service.deleteTownHouse(Integer.parseInt(idField.getText().trim()));
                JOptionPane.showMessageDialog(this, "Townhouse deleted.");
                clearForm(); loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        private TownHouse buildTownHouse() {
            return new TownHouse(
                Integer.parseInt(idField.getText().trim()),
                floorSizeField.getText().trim(),
                addressField.getText().trim(),
                locationField.getText().trim(),
                Double.parseDouble(marketField.getText().trim()),
                Double.parseDouble(rentalField.getText().trim()),
                availBox.isSelected(),
                Integer.parseInt(unitField.getText().trim()),
                backyardBox.isSelected()
            );
        }
        private void clearForm() {
            for (JTextField f : new JTextField[]{idField, floorSizeField, addressField,
                    locationField, marketField, rentalField, unitField}) f.setText("");
            backyardBox.setSelected(false); availBox.setSelected(false);
            table.clearSelection();
        }
        private JButton makeBtn(String t, Color bg, java.awt.event.ActionListener a) {
            JButton b = new JButton(t);
            b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFocusPainted(false); b.addActionListener(a); return b;
        }
    }
}
