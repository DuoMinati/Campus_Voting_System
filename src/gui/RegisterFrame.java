package gui;

import exception.InvalidInputException;
import model.Voter;
import service.UserOperations;
import service.UserService;
import validation.Validation;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

/**
 * College Voting Portal - Voter Registration Screen.
 * Features:
 * - Interactive Calendar Date Picker to choose Date of Birth
 * - Voter ID limited to exactly 6 numeric digits (blocks letters like 'a' and > 6 digits)
 * - Clean campus election portal theme
 */
public class RegisterFrame extends JFrame {

    private final UserOperations userOperations = new UserService();
    private JTextField nameField, cardField, emailField, dobField;
    private JPasswordField passField, confirmPassField;
    private JComboBox<String> genderBox;

    public RegisterFrame() {
        setTitle("Campus Voting Portal - Voter Registration");
        setSize(500, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // ---------- Top Header Banner ----------
        JPanel bannerPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        bannerPanel.setBackground(UITheme.NAVY);
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel titleLabel = new JLabel("VOTER REGISTRATION PORTAL", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        bannerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Student Enrollment • Campus Election Commission", SwingConstants.CENTER);
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(new Color(203, 213, 225));
        bannerPanel.add(subtitleLabel);

        mainPanel.add(bannerPanel, BorderLayout.NORTH);

        // ---------- Center Card Form Panel ----------
        JPanel formCard = new JPanel();
        formCard.setLayout(null);
        formCard.setBackground(Color.WHITE);

        int y = 15;
        int fieldHeight = 32;
        int gap = 54;
        int x = 45;
        int width = 395;

        // Full Name
        addLabel(formCard, "Student Full Name:", x, y);
        nameField = UITheme.createTextField();
        nameField.setBounds(x, y + 20, width, fieldHeight);
        formCard.add(nameField);

        // Voter Card No (strictly 6 digits, no letters allowed)
        y += gap;
        addLabel(formCard, "Voter ID Number (Exactly 6 numeric digits):", x, y);
        cardField = UITheme.createTextField();
        cardField.setBounds(x, y + 20, width, fieldHeight);
        restrictToSixDigits(cardField);
        formCard.add(cardField);

        // College Email
        y += gap;
        addLabel(formCard, "College Email Address:", x, y);
        emailField = UITheme.createTextField();
        emailField.setBounds(x, y + 20, width, fieldHeight);
        formCard.add(emailField);

        // Date of Birth with Calendar Date Picker Button
        y += gap;
        addLabel(formCard, "Date of Birth (DD-MM-YYYY):", x, y);
        dobField = UITheme.createTextField();
        dobField.setBounds(x, y + 20, width - 125, fieldHeight);
        dobField.setToolTipText("Enter DD-MM-YYYY or click Pick Date to select from calendar");
        formCard.add(dobField);

        UITheme.StyledButton calBtn = new UITheme.StyledButton("📅 Calendar", UITheme.ROYAL_BLUE, UITheme.ROYAL_BLUE_HOVER, Color.WHITE);
        calBtn.setBounds(x + width - 115, y + 20, 115, fieldHeight);
        calBtn.setFont(UITheme.FONT_BOLD);
        calBtn.addActionListener(e -> {
            String picked = DatePickerDialog.pickDate(this, dobField.getText());
            if (picked != null) {
                dobField.setText(picked);
            }
        });
        formCard.add(calBtn);

        // Gender
        y += gap;
        addLabel(formCard, "Gender:", x, y);
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderBox.setFont(UITheme.FONT_REGULAR);
        genderBox.setBackground(Color.WHITE);
        genderBox.setBounds(x, y + 20, width, fieldHeight);
        formCard.add(genderBox);

        // Password
        y += gap;
        addLabel(formCard, "Password (min 8 chars, at least 1 digit):", x, y);
        passField = UITheme.createPasswordField();
        passField.setBounds(x, y + 20, width, fieldHeight);
        formCard.add(passField);

        // Confirm Password
        y += gap;
        addLabel(formCard, "Confirm Password:", x, y);
        confirmPassField = UITheme.createPasswordField();
        confirmPassField.setBounds(x, y + 20, width, fieldHeight);
        formCard.add(confirmPassField);

        // Action Buttons
        y += 60;
        UITheme.StyledButton registerBtn = UITheme.createSuccessButton("Complete Registration");
        registerBtn.setBounds(x, y, 200, 40);
        registerBtn.addActionListener(e -> handleRegister());
        formCard.add(registerBtn);

        UITheme.StyledButton backBtn = UITheme.createSecondaryButton("Back to Login");
        backBtn.setBounds(x + 215, y, 180, 40);
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        formCard.add(backBtn);

        mainPanel.add(formCard, BorderLayout.CENTER);

        // ---------- Bottom Footer ----------
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(UITheme.BG_LIGHT);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 10, 0));
        JLabel footerLabel = new JLabel("Campus Election Commission • One Vote per Student");
        footerLabel.setFont(UITheme.FONT_SMALL);
        footerLabel.setForeground(UITheme.TEXT_MUTED);
        footerPanel.add(footerLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * DocumentFilter on Voter Card text field:
     * - Allows only numeric digits (0-9).
     * - Rejects alphabets like 'a' or symbols.
     * - Strictly limits total length to 6 characters.
     */
    private void restrictToSixDigits(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String next = current.substring(0, offset) + string + current.substring(offset);
                if (next.length() <= 6 && next.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String next = current.substring(0, offset) + text + current.substring(offset + length);
                if (next.length() <= 6 && next.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private void addLabel(JPanel panel, String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_REGULAR);
        label.setForeground(UITheme.TEXT_DARK);
        label.setBounds(x, y, 350, 18);
        panel.add(label);
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String card = cardField.getText().trim();
        String email = emailField.getText().trim();
        String dob = dobField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String pass = new String(passField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        try {
            Voter voter = Validation.validateAndCreateVoter(name, card, email, dob, gender, pass, confirmPass);
            boolean success = userOperations.registerVoter(voter, confirmPass);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Registration Successful!\nYour voter profile has been enrolled in the campus voting roll.\nYou can now log in to vote.",
                        "Voter Enrolled", JOptionPane.INFORMATION_MESSAGE);
                new LoginFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Registration failed. Email or Voter ID number may already be registered.",
                        "Enrollment Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Notice", JOptionPane.WARNING_MESSAGE);
        }
    }
}
