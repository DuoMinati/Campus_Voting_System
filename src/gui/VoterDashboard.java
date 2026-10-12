package gui;

import dao.CandidateDAO;
import dao.VoteDAO;
import model.Candidate;
import model.Voter;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * College Voting Portal - Official Ballot Screen.
 * Presents candidate choices to an enrolled student voter.
 */
public class VoterDashboard extends JFrame {

    private final Voter voter;
    private ButtonGroup candidateGroup;
    private UITheme.StyledButton voteBtn;

    public VoterDashboard(Voter voter) {
        this.voter = voter;

        setTitle("Campus Voting Portal - Official Ballot");
        setSize(540, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // ---------- Top Header Banner ----------
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(UITheme.NAVY);
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel titleLabel = new JLabel("CAMPUS STUDENT BALLOT");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        bannerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel voterBadge = new JLabel("Voter: " + voter.getFullName() + " (" + voter.getVoterCardNo() + ")");
        voterBadge.setFont(UITheme.FONT_BOLD);
        voterBadge.setForeground(new Color(203, 213, 225));
        bannerPanel.add(voterBadge, BorderLayout.EAST);

        mainPanel.add(bannerPanel, BorderLayout.NORTH);

        // ---------- Center Ballot Card ----------
        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        cardPanel.setBackground(UITheme.BG_LIGHT);

        JLabel instructions = new JLabel("Please select exactly one candidate to cast your official ballot:");
        instructions.setFont(UITheme.FONT_BOLD);
        instructions.setForeground(UITheme.TEXT_DARK);
        cardPanel.add(instructions, BorderLayout.NORTH);

        JPanel candidatesListPanel = new JPanel();
        candidatesListPanel.setLayout(new BoxLayout(candidatesListPanel, BoxLayout.Y_AXIS));
        candidatesListPanel.setBackground(Color.WHITE);
        candidatesListPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        candidateGroup = new ButtonGroup();
        CandidateDAO candidateDAO = new CandidateDAO();
        List<Candidate> candidates = candidateDAO.getAllCandidates();

        if (candidates.isEmpty()) {
            JLabel emptyLabel = new JLabel("No candidates registered yet. Please check back later.");
            emptyLabel.setFont(UITheme.FONT_REGULAR);
            emptyLabel.setForeground(UITheme.TEXT_MUTED);
            candidatesListPanel.add(emptyLabel);
        }

        for (Candidate c : candidates) {
            JPanel cRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            cRow.setBackground(Color.WHITE);

            JRadioButton rb = new JRadioButton();
            rb.putClientProperty("candidateId", c.getCandidateId());
            rb.setBackground(Color.WHITE);
            rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
            candidateGroup.add(rb);

            JLabel nameLabel = new JLabel(c.getCandidateName());
            nameLabel.setFont(UITheme.FONT_BOLD);

            JLabel partyLabel = new JLabel("—  " + c.getPartyName());
            partyLabel.setFont(UITheme.FONT_REGULAR);
            partyLabel.setForeground(UITheme.TEXT_MUTED);

            JLabel symbolLabel = new JLabel("[" + c.getSymbol() + "]");
            symbolLabel.setFont(UITheme.FONT_BOLD);
            symbolLabel.setForeground(UITheme.ROYAL_BLUE);

            cRow.add(rb);
            cRow.add(nameLabel);
            cRow.add(partyLabel);
            cRow.add(symbolLabel);

            candidatesListPanel.add(cRow);
            candidatesListPanel.add(Box.createVerticalStrut(6));
        }

        JScrollPane scrollPane = new JScrollPane(candidatesListPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // ---------- Bottom Action Bar ----------
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));

        UITheme.StyledButton logoutBtn = UITheme.createSecondaryButton("Log Out");
        logoutBtn.setPreferredSize(new Dimension(110, 38));
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        bottomBar.add(logoutBtn);

        voteBtn = UITheme.createSuccessButton("Cast Official Vote");
        voteBtn.setPreferredSize(new Dimension(170, 38));
        voteBtn.addActionListener(e -> handleVote());
        bottomBar.add(voteBtn);

        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        // Check if already voted - retrieve whom the voter voted for from database
        if (voter.isHasVoted()) {
            dao.VoteDAO voteDAO = new dao.VoteDAO();
            model.Candidate votedForCand = voteDAO.getVotedCandidateForVoter(voter.getVoterId());
            String votedMsg = "✓ You have cast your official ballot in this election.";
            if (votedForCand != null) {
                votedMsg = "✓ Your Recorded Ballot: Voted for " + votedForCand.getCandidateName() 
                        + " (" + votedForCand.getPartyName() + ") [" + votedForCand.getSymbol() + "]";
                // Highlight the candidate radio button
                for (java.util.Enumeration<AbstractButton> buttons = candidateGroup.getElements(); buttons.hasMoreElements(); ) {
                    AbstractButton btn = buttons.nextElement();
                    if ((int) btn.getClientProperty("candidateId") == votedForCand.getCandidateId()) {
                        btn.setSelected(true);
                        break;
                    }
                }
            }
            disableVoting(candidatesListPanel, votedMsg);
        }

        add(mainPanel);
    }

    private void handleVote() {
        int selectedCandidateId = -1;

        for (java.util.Enumeration<AbstractButton> buttons = candidateGroup.getElements(); buttons.hasMoreElements(); ) {
            AbstractButton btn = buttons.nextElement();
            if (btn.isSelected()) {
                selectedCandidateId = (int) btn.getClientProperty("candidateId");
                break;
            }
        }

        if (selectedCandidateId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please choose a candidate by selecting a radio button before submitting.",
                    "Candidate Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm Ballot Submission:\nAre you sure you want to cast your vote for the selected candidate?\nThis action is irreversible.",
                "Confirm Vote", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        VoteDAO voteDAO = new VoteDAO();
        boolean success = voteDAO.castVote(voter.getVoterId(), selectedCandidateId);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Ballot Submitted Successfully!\nYour vote has been recorded in the secure election ledger.",
                    "Vote Confirmed", JOptionPane.INFORMATION_MESSAGE);
            voter.setHasVoted(true);
            voteBtn.setEnabled(false);
            for (java.util.Enumeration<AbstractButton> buttons = candidateGroup.getElements(); buttons.hasMoreElements(); ) {
                buttons.nextElement().setEnabled(false);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Could not record vote. Please check your database connection or consult the election officer.",
                    "Submission Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disableVoting(JPanel panel, String message) {
        voteBtn.setEnabled(false);
        for (java.util.Enumeration<AbstractButton> buttons = candidateGroup.getElements(); buttons.hasMoreElements(); ) {
            buttons.nextElement().setEnabled(false);
        }
        JLabel statusLabel = new JLabel(message);
        statusLabel.setForeground(UITheme.EMERALD_GREEN);
        statusLabel.setFont(UITheme.FONT_BOLD);
        panel.add(Box.createVerticalStrut(10));
        panel.add(statusLabel);
    }
}
