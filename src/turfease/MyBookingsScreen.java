package turfease;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MyBookingsScreen extends JDialog {

    // Column positions in the table model (the ID column is hidden from view)
    private static final int ID_COLUMN = 0;
    private static final int FACILITY_COLUMN = 1;
    private static final int DATE_COLUMN = 2;
    private static final int TIME_COLUMN = 3;
    private static final int STATUS_COLUMN = 5;

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Booking ID", "Facility", "Date", "Time", "Price", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public MyBookingsScreen(JFrame parent) {
        super(parent, "My Bookings", true);
        setSize(860, 480);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);

        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(FACILITY_COLUMN).setPreferredWidth(240);

        // Hide the ID column from the screen (the data is still kept in the model)
        table.removeColumn(table.getColumnModel().getColumn(ID_COLUMN));

        JScrollPane scroll = new JScrollPane(table);

        JButton cancel = Theme.button("Cancel Selected", false);
        JButton delete = Theme.button("Delete Selected", false);
        delete.setForeground(Color.decode("#DC2626"));
        JButton close = Theme.button("Close", true);

        cancel.addActionListener(e -> cancelSelected());
        delete.addActionListener(e -> deleteSelected());
        close.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(Color.WHITE);
        buttons.add(cancel);
        buttons.add(delete);
        buttons.add(close);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        body.add(scroll, BorderLayout.CENTER);
        body.add(buttons, BorderLayout.SOUTH);

        add(Theme.header("My Bookings",
                "Bookings for " + AppData.getCurrentUser().getName()), BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);

        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        for (Booking b : AppData.getCurrentUser().getBookingHistory()) {
            TimeSlot s = b.getTimeSlot();
            model.addRow(new Object[]{
                    b.getBookingId(),
                    b.getFacility().getName(),
                    s.getDate(),
                    s.getStartTime() + " - " + s.getEndTime(),
                    String.format("\u20B9%.2f", b.getPrice()),
                    b.getStatus()
            });
        }
    }

    // e.g. "Kickathlon on 2026-10-02 (16:00 - 17:00)"
    private String describe(int row) {
        return model.getValueAt(row, FACILITY_COLUMN) + " on "
                + model.getValueAt(row, DATE_COLUMN) + " ("
                + model.getValueAt(row, TIME_COLUMN) + ")";
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking first.");
            return;
        }

        String id = String.valueOf(model.getValueAt(row, ID_COLUMN));
        String status = String.valueOf(model.getValueAt(row, STATUS_COLUMN));

        if (BookingStatus.CANCELLED.toString().equals(status)) {
            JOptionPane.showMessageDialog(this, "That booking is already cancelled.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this,
                "Cancel this booking?\n" + describe(row), "Confirm cancellation",
                JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        if (AppData.manager.cancelBooking(id)) {
            AppData.save();
            JOptionPane.showMessageDialog(this, "Booking cancelled.");
        } else {
            JOptionPane.showMessageDialog(this, "Could not cancel that booking.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        refresh();
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking first.");
            return;
        }

        String id = String.valueOf(model.getValueAt(row, ID_COLUMN));
        String status = String.valueOf(model.getValueAt(row, STATUS_COLUMN));

        String message = "Delete this booking permanently?\n" + describe(row);
        if (!BookingStatus.CANCELLED.toString().equals(status)) {
            message += "\n\nIt is still active, so its time slot will be freed.";
        }

        int answer = JOptionPane.showConfirmDialog(this, message, "Confirm delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        if (AppData.manager.deleteBooking(id)) {
            AppData.save();
        } else {
            JOptionPane.showMessageDialog(this, "Could not delete that booking.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        refresh();
    }
}