package gui;

import exception.AuthenticationException;
import exception.InvalidInputException;
import model.Voter;
import service.UserOperations;
import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * College Voting Portal - Login Screen.
 * Clean, modern layout with perfectly positioned, fully visible buttons.
 */
public class LoginFrame extends JFrame {

    private final UserOperations userOperations = new UserService();
    private JRadioButton adminRadio, voterRadio;
    private JTextField userField;
    private JPasswordField passField;

    public LoginFrame() {
        setTitle("Campus Voting Portal - Login");
        setSize(480, 490);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // ---------- Top Header Banner ----------
        JPanel bannerPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        bannerPanel.setBackground(UITheme.NAVY);
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("CAMPUS ELECTION COMMISSION", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        bannerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("MCA Student Council Elections", SwingConstants.CENTER);
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(new Color(203, 213, 225));
        bannerPanel.add(subtitleLabel);

        mainPanel.add(bannerPanel, BorderLayout.NORTH);

        // ---------- Center Card Panel ----------
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(null);
        cardPanel.setBackground(Color.WHITE);

        int leftX = 45;
        int fieldWidth = 375;

        // Role Selection
        JLabel roleLabel = new JLabel("Select Portal Access:");
        roleLabel.setFont(UITheme.FONT_BOLD);
        roleLabel.setForeground(UITheme.TEXT_DARK);
        roleLabel.setBounds(leftX, 15, 200, 20);
        cardPanel.add(roleLabel);

        voterRadio = new JRadioButton("Voter (Student)");
        voterRadio.setSelected(true);
        voterRadio.setFont(UITheme.FONT_REGULAR);
        voterRadio.setBackground(Color.WHITE);
        voterRadio.setBounds(leftX, 38, 140, 26);

        adminRadio = new JRadioButton("Admin (Faculty)");
        adminRadio.setFont(UITheme.FONT_REGULAR);
        adminRadio.setBackground(Color.WHITE);
        adminRadio.setBounds(leftX + 150, 38, 150, 26);

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(voterRadio);
        roleGroup.add(adminRadio);
        cardPanel.add(voterRadio);
        cardPanel.add(adminRadio);

        // Email / Username field
        JLabel userLabel = new JLabel("Email / Username:");
        userLabel.setFont(UITheme.FONT_REGULAR);
        userLabel.setForeground(UITheme.TEXT_DARK);
        userLabel.setBounds(leftX, 75, fieldWidth, 20);
        cardPanel.add(userLabel);

        userField = UITheme.createTextField();
        userField.setBounds(leftX, 98, fieldWidth, 34);
        cardPanel.add(userField);

        // Password field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UITheme.FONT_REGULAR);
        passLabel.setForeground(UITheme.TEXT_DARK);
        passLabel.setBounds(leftX, 142, fieldWidth, 20);
        cardPanel.add(passLabel);

        passField = UITheme.createPasswordField();
        passField.setBounds(leftX, 165, fieldWidth, 34);
        cardPanel.add(passField);

        // Action Buttons with generous height and full visibility
        int btnWidth = 180;
        int btnHeight = 42;
        int btnY = 218;

        UITheme.StyledButton loginBtn = UITheme.createPrimaryButton("Login to Vote");
        loginBtn.setBounds(leftX, btnY, btnWidth, btnHeight);
        loginBtn.addActionListener(this::handleLogin);
        cardPanel.add(loginBtn);

        UITheme.StyledButton registerBtn = UITheme.createSuccessButton("Register as Voter");
        registerBtn.setBounds(leftX + btnWidth + 15, btnY, btnWidth, btnHeight);
        registerBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
        cardPanel.add(registerBtn);

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // ---------- Bottom Footer ----------
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(UITheme.BG_LIGHT);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 12, 0));
        JLabel footerLabel = new JLabel("Department of Computer Applications • Secure Electronic Voting");
        footerLabel.setFont(UITheme.FONT_SMALL);
        footerLabel.setForeground(UITheme.TEXT_MUTED);
        footerPanel.add(footerLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void handleLogin(ActionEvent e) {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());

        try {
            if (adminRadio.isSelected()) {
                boolean authenticated = userOperations.loginAdmin(user, pass);
                if (authenticated) {
                    new AdminDashboard().setVisible(true);
                    dispose();
                }
            } else {
                Voter voter = userOperations.loginVoter(user, pass);
                if (voter != null) {
                    new VoterDashboard(voter).setVisible(true);
                    dispose();
                }
            }
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Required", JOptionPane.WARNING_MESSAGE);
        } catch (AuthenticationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
