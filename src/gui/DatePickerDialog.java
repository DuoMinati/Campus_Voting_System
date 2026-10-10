package gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Interactive Calendar Date Picker Dialog.
 * Allows users to choose a birth date with Month and Year drop-downs
 * and an interactive calendar day grid.
 */
public class DatePickerDialog extends JDialog {

    private int selectedYear;
    private int selectedMonth; // 1 to 12
    private int selectedDay = -1;
    private String pickedDate = null;

    private JComboBox<String> monthBox;
    private JComboBox<Integer> yearBox;
    private JPanel daysGridPanel;
    private JButton[] dayButtons = new JButton[42];

    private static final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    public DatePickerDialog(Frame parent, String initialDate) {
        super(parent, "Choose Date of Birth", true);
        setSize(380, 390);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Determine starting date (default to 18 years ago or provided initial date)
        LocalDate start = LocalDate.now().minusYears(18);
        if (initialDate != null && !initialDate.trim().isEmpty()) {
            try {
                String cleaned = initialDate.trim().replace('/', '-');
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                start = LocalDate.parse(cleaned, fmt);
            } catch (Exception ignored) {
            }
        }

        selectedYear = start.getYear();
        selectedMonth = start.getMonthValue();
        selectedDay = start.getDayOfMonth();

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        // ---------- Top Controls: Month & Year Dropdowns ----------
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        navPanel.setBackground(Color.WHITE);

        monthBox = new JComboBox<>(MONTHS);
        monthBox.setSelectedIndex(selectedMonth - 1);
        monthBox.setFont(UITheme.FONT_BOLD);
        monthBox.setBackground(Color.WHITE);
        monthBox.addActionListener(e -> {
            selectedMonth = monthBox.getSelectedIndex() + 1;
            updateCalendarGrid();
        });
        navPanel.add(monthBox);

        // Populate years from 1950 up to current year
        int currentYear = LocalDate.now().getYear();
        Integer[] years = new Integer[currentYear - 1950 + 1];
        for (int y = currentYear, idx = 0; y >= 1950; y--, idx++) {
            years[idx] = y;
        }
        yearBox = new JComboBox<>(years);
        yearBox.setSelectedItem(selectedYear);
        yearBox.setFont(UITheme.FONT_BOLD);
        yearBox.setBackground(Color.WHITE);
        yearBox.addActionListener(e -> {
            Integer yr = (Integer) yearBox.getSelectedItem();
            if (yr != null) {
                selectedYear = yr;
                updateCalendarGrid();
            }
        });
        navPanel.add(yearBox);

        contentPanel.add(navPanel, BorderLayout.NORTH);

        // ---------- Center Calendar Grid ----------
        JPanel calendarContainer = new JPanel(new BorderLayout());
        calendarContainer.setBackground(Color.WHITE);

        // Day names header (Sun, Mon, Tue, ...)
        JPanel dayNamesPanel = new JPanel(new GridLayout(1, 7, 2, 2));
        dayNamesPanel.setBackground(new Color(241, 245, 249));
        dayNamesPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        String[] dayNames = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String dn : dayNames) {
            JLabel lbl = new JLabel(dn, SwingConstants.CENTER);
            lbl.setFont(UITheme.FONT_BOLD);
            lbl.setForeground(dn.equals("Su") ? UITheme.DANGER_RED : UITheme.TEXT_DARK);
            dayNamesPanel.add(lbl);
        }
        calendarContainer.add(dayNamesPanel, BorderLayout.NORTH);

        // Days buttons (6 rows x 7 columns)
        daysGridPanel = new JPanel(new GridLayout(6, 7, 2, 2));
        daysGridPanel.setBackground(Color.WHITE);

        for (int i = 0; i < 42; i++) {
            JButton btn = new JButton("");
            btn.setFont(UITheme.FONT_REGULAR);
            btn.setFocusPainted(false);
            btn.setMargin(new Insets(2, 2, 2, 2));
            btn.setBackground(Color.WHITE);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            final int btnIdx = i;
            btn.addActionListener(e -> {
                String text = btn.getText();
                if (!text.isEmpty()) {
                    selectedDay = Integer.parseInt(text);
                    confirmAndClose();
                }
            });

            dayButtons[i] = btn;
            daysGridPanel.add(btn);
        }

        calendarContainer.add(daysGridPanel, BorderLayout.CENTER);
        contentPanel.add(calendarContainer, BorderLayout.CENTER);

        // ---------- Bottom Bar ----------
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomBar.setBackground(Color.WHITE);

        UITheme.StyledButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(85, 30));
        cancelBtn.addActionListener(e -> {
            pickedDate = null;
            dispose();
        });
        bottomBar.add(cancelBtn);

        contentPanel.add(bottomBar, BorderLayout.SOUTH);

        add(contentPanel);
        updateCalendarGrid();
    }

    private void updateCalendarGrid() {
        YearMonth ym = YearMonth.of(selectedYear, selectedMonth);
        LocalDate firstOfMonth = ym.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0, Monday = 1, ...
        int daysInMonth = ym.lengthOfMonth();

        LocalDate today = LocalDate.now();

        for (int i = 0; i < 42; i++) {
            JButton btn = dayButtons[i];
            int dayNumber = i - dayOfWeek + 1;

            if (dayNumber >= 1 && dayNumber <= daysInMonth) {
                btn.setText(String.valueOf(dayNumber));
                btn.setEnabled(true);
                btn.setVisible(true);

                // Highlight selected day
                if (dayNumber == selectedDay) {
                    btn.setBackground(UITheme.ROYAL_BLUE);
                    btn.setForeground(Color.WHITE);
                } else if (selectedYear == today.getYear() && selectedMonth == today.getMonthValue() && dayNumber == today.getDayOfMonth()) {
                    btn.setBackground(new Color(224, 242, 254)); // Light blue for today
                    btn.setForeground(UITheme.ROYAL_BLUE);
                } else {
                    btn.setBackground(Color.WHITE);
                    btn.setForeground(UITheme.TEXT_DARK);
                }
            } else {
                btn.setText("");
                btn.setEnabled(false);
                btn.setBackground(new Color(248, 250, 252));
                btn.setForeground(Color.LIGHT_GRAY);
            }
        }
    }

    private void confirmAndClose() {
        if (selectedDay > 0) {
            pickedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth, selectedYear);
            dispose();
        }
    }

    /**
     * Static method to display the dialog and return the formatted date string (DD-MM-YYYY).
     */
    public static String pickDate(Frame parent, String currentDate) {
        DatePickerDialog dialog = new DatePickerDialog(parent, currentDate);
        dialog.setVisible(true);
        return dialog.pickedDate;
    }
}
