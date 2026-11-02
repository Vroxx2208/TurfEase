package turfease;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminFacilitiesScreen extends JDialog {

    private static final int ID_COLUMN = 0;

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Area", "Type", "Price per hour"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public AdminFacilitiesScreen(JFrame parent) {
        super(parent, "Manage Facilities", true);
        setSize(900, 520);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);

        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.removeColumn(table.getColumnModel().getColumn(ID_COLUMN));

        JButton add = Theme.button("Add Facility", true);
        JButton price = Theme.button("Change Price", false);
        JButton close = Theme.button("Close", false);

        add.addActionListener(e -> addFacility());
        price.addActionListener(e -> changePrice());
        close.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(Color.WHITE);
        buttons.add(add);
        buttons.add(price);
        buttons.add(close);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        body.add(new JScrollPane(table), BorderLayout.CENTER);
        body.add(buttons, BorderLayout.SOUTH);

        add(Theme.header("Manage Facilities", "Add places and change prices"), BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);

        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        for (SportsFacility f : AppData.manager.listAllFacilities()) {
            model.addRow(new Object[]{
                    f.getFacilityId(),
                    f.getName(),
                    f.getLocation(),
                    f.getType().name().replace('_', ' '),
                    String.format("\u20B9%.2f", f.getBaseRatePerHour())
            });
        }
    }

    private void addFacility() {
        JTextField name = new JTextField();
        JTextField area = new JTextField();
        JTextField rate = new JTextField();
        JComboBox<FacilityType> type = new JComboBox<>(FacilityType.values());

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.add(new JLabel("Name"));
        form.add(name);
        form.add(new JLabel("Area"));
        form.add(area);
        form.add(new JLabel("Type"));
        form.add(type);
        form.add(new JLabel("Price per hour"));
        form.add(rate);

        while (true) {
            int answer = JOptionPane.showConfirmDialog(this, form, "Add Facility",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (answer != JOptionPane.OK_OPTION) {
                return;
            }
            String problem = check(name.getText(), area.getText(), rate.getText());
            if (problem == null) {
                break;
            }
            JOptionPane.showMessageDialog(this, problem, "Check the details",
                    JOptionPane.WARNING_MESSAGE);
        }

        SportsFacility f = FacilityStore.create((FacilityType) type.getSelectedItem(),
                name.getText().trim(), area.getText().trim(),
                Double.parseDouble(rate.getText().trim()));
        AppData.addFacility(f);
        refresh();
    }

    private String check(String name, String area, String rate) {
        if (name.trim().isEmpty() || area.trim().isEmpty()) {
            return "Name and area cannot be empty.";
        }
        if (name.contains("|") || area.contains("|")) {
            return "Please do not use the | character.";
        }
        for (SportsFacility f : AppData.manager.listAllFacilities()) {
            if (f.getName().equalsIgnoreCase(name.trim())) {
                return "A facility with this name already exists.";
            }
        }
        try {
            if (Double.parseDouble(rate.trim()) <= 0) {
                return "Price must be more than 0.";
            }
        } catch (NumberFormatException e) {
            return "Price must be a number.";
        }
        return null;
    }

    private void changePrice() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a facility first.");
            return;
        }

        String id = String.valueOf(model.getValueAt(row, ID_COLUMN));
        SportsFacility f = AppData.manager.findFacilityById(id);
        if (f == null) {
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "New price per hour for " + f.getName() + ":",
                String.valueOf(f.getBaseRatePerHour()));
        if (input == null) {
            return;
        }

        try {
            double newRate = Double.parseDouble(input.trim());
            if (newRate <= 0) {
                throw new NumberFormatException();
            }
            AppData.manager.updateFacilityPrice(id, newRate);
            AppData.saveFacilities();
            refresh();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}