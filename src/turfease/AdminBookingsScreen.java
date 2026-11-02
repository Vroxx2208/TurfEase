package turfease;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminBookingsScreen extends JDialog {

    private static final int ID_COLUMN = 0;
    private static final int USER_COLUMN = 1;
    private static final int FACILITY_COLUMN = 2;
    private static final int DATE_COLUMN = 3;
    private static final int TIME_COLUMN = 4;
    private static final int STATUS_COLUMN = 6;

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Booking ID", "User", "Facility", "Date", "Time", "Price", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public AdminBookingsScreen(JFrame parent) {
        super(parent, "All Bookings", true);
        setSize(1000, 520);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);

        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(USER_COLUMN).setPreferredWidth(140);
        table.getColumnModel().getColumn(FACILITY_COLUMN).setPreferredWidth(240);
        table.removeColumn(table.getColumnModel().getColumn(ID_COLUMN));

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
        body.add(new JScrollPane(table), BorderLayout.CENTER);
        body.add(buttons, BorderLayout.SOUTH);

        add(Theme.header("All Bookings", "Every booking from every user"), BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);

        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        for (Booking b : AppData.manager.viewAllBookings()) {
            TimeSlot s = b.getTimeSlot();
            model.addRow(new Object[]{
                    b.getBookingId(),
                    b.getUser().getName(),
                    b.getFacility().getName(),
                    s.getDate(),
                    s.getStartTime() + " - " + s.getEndTime(),
                    String.format("\u20B9%.2f", b.getPrice()),
                    b.getStatus()
            });
        }
    }

    private String describe(int row) {
        return model.getValueAt(row, USER_COLUMN) + ": "
                + model.getValueAt(row, FACILITY_COLUMN) + " on "
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