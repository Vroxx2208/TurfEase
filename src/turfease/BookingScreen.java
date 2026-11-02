package turfease;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingScreen extends JDialog {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yy");

    private final JComboBox<SportsFacility> facilityBox = new JComboBox<>();
    private final JComboBox<TimeSlot> slotBox = new JComboBox<>();
    private final JLabel priceLabel = new JLabel(" ");
    private final JButton confirm = Theme.button("Confirm Booking", true);

    public BookingScreen(JFrame parent) {
        super(parent, "Book a Facility", true);
        setSize(460, 480);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);

        for (SportsFacility f : AppData.manager.listAllFacilities()) {
            facilityBox.addItem(f);
        }

        facilityBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SportsFacility) {
                    SportsFacility f = (SportsFacility) value;
                    setText(f.getName() + " - " + f.getLocation());
                }
                return this;
            }
        });

        slotBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TimeSlot) {
                    TimeSlot s = (TimeSlot) value;
                    setText(s.getDate().format(DATE) + "   "
                            + s.getStartTime() + " - " + s.getEndTime());
                }
                return this;
            }
        });

        priceLabel.setFont(priceLabel.getFont().deriveFont(Font.BOLD, 16f));
        priceLabel.setForeground(Theme.GREEN);

        JPanel body = new JPanel(new GridLayout(0, 1, 0, 6));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        body.add(label("Facility"));
        body.add(facilityBox);
        body.add(label("Time slot"));
        body.add(slotBox);
        body.add(priceLabel);
        body.add(confirm);

        add(Theme.header("Book a Facility", "Pick a facility and a free slot"), BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);

        facilityBox.addActionListener(e -> loadSlots());
        slotBox.addActionListener(e -> updatePrice());
        confirm.addActionListener(e -> confirmBooking());

        loadSlots();
    }

    private static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.GRAY);
        return l;
    }

    // A slot can only be booked if it has not started yet
    private static boolean isUpcoming(TimeSlot s) {
        return LocalDateTime.of(s.getDate(), s.getStartTime()).isAfter(LocalDateTime.now());
    }

    private void loadSlots() {
        slotBox.removeAllItems();
        SportsFacility f = (SportsFacility) facilityBox.getSelectedItem();
        if (f != null) {
            for (TimeSlot s : f.getAvailableTimeSlots()) {
                if (isUpcoming(s)) {
                    slotBox.addItem(s);
                }
            }
        }
        confirm.setEnabled(slotBox.getItemCount() > 0);
        updatePrice();
    }

    private void updatePrice() {
        SportsFacility f = (SportsFacility) facilityBox.getSelectedItem();
        TimeSlot s = (TimeSlot) slotBox.getSelectedItem();
        if (f == null || s == null) {
            priceLabel.setText("No free slots for this facility");
            return;
        }
        priceLabel.setText(String.format("Price: \u20B9%.2f", f.calculatePrice(s)));
    }

    private void confirmBooking() {
        SportsFacility f = (SportsFacility) facilityBox.getSelectedItem();
        TimeSlot s = (TimeSlot) slotBox.getSelectedItem();
        if (f == null || s == null) return;

        // the window may have been open for a while, so check again
        if (!isUpcoming(s)) {
            JOptionPane.showMessageDialog(this, "That slot has already started. Please pick another.",
                    "Slot not available", JOptionPane.WARNING_MESSAGE);
            loadSlots();
            return;
        }

        Booking b = AppData.manager.bookFacility(
                AppData.getCurrentUser(), f.getFacilityId(), s.getSlotId());

        if (b == null) {
            JOptionPane.showMessageDialog(this, "Sorry, that slot could not be booked.",
                    "Booking failed", JOptionPane.ERROR_MESSAGE);
            loadSlots();
        } else {
            AppData.save();
            JOptionPane.showMessageDialog(this,
                    "Booking confirmed!\n" + f.getName()
                            + "\n" + s.getDate().format(DATE) + "   "
                            + s.getStartTime() + " - " + s.getEndTime()
                            + "\nPrice: \u20B9" + String.format("%.2f", b.getPrice()),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
}