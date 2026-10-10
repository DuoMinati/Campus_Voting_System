package gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * College Voting Portal Theme and UI Components.
 * Provides custom-painted rounded buttons, clean typography, and a modern
 * college election portal color scheme that renders consistently on all systems.
 */
public class UITheme {

    // College Theme Color Palette
    public static final Color NAVY = new Color(24, 43, 73);              // #182B49 College Oxford Navy
    public static final Color ROYAL_BLUE = new Color(37, 99, 235);       // #2563EB Portal Primary Blue
    public static final Color ROYAL_BLUE_HOVER = new Color(29, 78, 216); // #1D4ED8
    public static final Color EMERALD_GREEN = new Color(16, 149, 106);    // #10956A Vote / Register Green
    public static final Color EMERALD_HOVER = new Color(5, 122, 85);     // #057A55
    public static final Color DANGER_RED = new Color(220, 38, 38);        // #DC2626 Delete Red
    public static final Color DANGER_HOVER = new Color(185, 28, 28);
    public static final Color BG_LIGHT = new Color(241, 245, 249);        // #F1F5F9 Slate Light Background
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_DARK = new Color(15, 23, 42);          // Slate 900
    public static final Color TEXT_MUTED = new Color(100, 116, 139);      // Slate 500
    public static final Color BORDER_LIGHT = new Color(203, 213, 225);    // Slate 300

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Custom Rounded Button with anti-aliasing and hover state.
     * Prevents Windows LookAndFeel from wiping out custom background colors.
     */
    public static class StyledButton extends JButton {
        private final Color baseColor;
        private final Color hoverColor;
        private boolean isHovered = false;

        public StyledButton(String text, Color baseColor, Color hoverColor, Color textColor) {
            super(text);
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            setForeground(textColor);
            setFont(FONT_BOLD);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        isHovered = true;
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Choose fill color based on state
            if (!isEnabled()) {
                g2.setColor(new Color(203, 213, 225));
            } else if (isHovered) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(baseColor);
            }

            // Draw rounded pill/button background
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            // Draw border if it's a light/outline button
            if (baseColor.equals(Color.WHITE)) {
                g2.setColor(BORDER_LIGHT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Factory method for Primary Royal Blue action buttons (Login, View Results)
     */
    public static StyledButton createPrimaryButton(String text) {
        return new StyledButton(text, ROYAL_BLUE, ROYAL_BLUE_HOVER, Color.WHITE);
    }

    /**
     * Factory method for Success Green action buttons (Register, Submit Vote)
     */
    public static StyledButton createSuccessButton(String text) {
        return new StyledButton(text, EMERALD_GREEN, EMERALD_HOVER, Color.WHITE);
    }

    /**
     * Factory method for Secondary outline buttons (Back to Login, Cancel, Logout)
     */
    public static StyledButton createSecondaryButton(String text) {
        return new StyledButton(text, Color.WHITE, new Color(241, 245, 249), TEXT_DARK);
    }

    /**
     * Factory method for Danger Red buttons (Delete Candidate)
     */
    public static StyledButton createDangerButton(String text) {
        return new StyledButton(text, DANGER_RED, DANGER_HOVER, Color.WHITE);
    }

    /**
     * Creates a styled text field with rounded padding and border.
     */
    public static JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_REGULAR);
        tf.setBorder(createFieldBorder());
        return tf;
    }

    /**
     * Creates a styled password field with rounded padding and border.
     */
    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_REGULAR);
        pf.setBorder(createFieldBorder());
        return pf;
    }

    private static Border createFieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        );
    }
}
