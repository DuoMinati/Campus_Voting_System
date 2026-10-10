package gui;

import dao.CandidateDAO;
import model.Candidate;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * College Voting Portal - Live Results & Standings Screen.
 */
public class ResultFrame extends JFrame {

    public ResultFrame() {
        setTitle("Live Standings - Campus Election Commission");
        setSize(540, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // ---------- Top Header Banner ----------
        JPanel bannerPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        bannerPanel.setBackground(UITheme.NAVY);
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel titleLabel = new JLabel("LIVE ELECTION STANDINGS", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        bannerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Official Campus Ballot Count", SwingConstants.CENTER);
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(new Color(203, 213, 225));
        bannerPanel.add(subtitleLabel);

        mainPanel.add(bannerPanel, BorderLayout.NORTH);

        // ---------- Center Table Container ----------
        JPanel tableContainer = new JPanel(new BorderLayout(8, 8));
        tableContainer.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        tableContainer.setBackground(UITheme.BG_LIGHT);

        CandidateDAO candidateDAO = new CandidateDAO();
        List<Candidate> candidates = candidateDAO.getAllCandidates();

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Candidate Name", "Party Affiliation", "Symbol", "Votes Cast"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Candidate leader = null;
        int totalVotesOverall = 0;
        for (Candidate c : candidates) {
            model.addRow(new Object[]{c.getCandidateName(), c.getPartyName(), c.getSymbol(), c.getTotalVotes()});
            totalVotesOverall += c.getTotalVotes();
            if (leader == null || c.getTotalVotes() > leader.getTotalVotes()) {
                leader = c;
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(UITheme.FONT_REGULAR);
        table.getTableHeader().setFont(UITheme.FONT_BOLD);
        table.getTableHeader().setBackground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tableContainer, BorderLayout.CENTER);

        // ---------- Bottom Winner Card & Close Button ----------
        JPanel bottomCard = new JPanel(new BorderLayout(10, 10));
        bottomCard.setBackground(Color.WHITE);
        bottomCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        JLabel winnerLabel = new JLabel("", SwingConstants.CENTER);
        winnerLabel.setFont(UITheme.FONT_BOLD);

        if (leader != null && leader.getTotalVotes() > 0) {
            winnerLabel.setText("Currently Leading: " + leader.getCandidateName() + " (" + leader.getPartyName() + ") with " + leader.getTotalVotes() + " votes");
            winnerLabel.setForeground(UITheme.EMERALD_GREEN);
        } else {
            winnerLabel.setText("No ballots have been counted yet.");
            winnerLabel.setForeground(UITheme.TEXT_MUTED);
        }
        bottomCard.add(winnerLabel, BorderLayout.NORTH);

        UITheme.StyledButton closeBtn = UITheme.createSecondaryButton("Close Standings Window");
        closeBtn.setPreferredSize(new Dimension(200, 36));
        closeBtn.addActionListener(e -> dispose());
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrap.setOpaque(false);
        btnWrap.add(closeBtn);
        bottomCard.add(btnWrap, BorderLayout.SOUTH);

        mainPanel.add(bottomCard, BorderLayout.SOUTH);

        add(mainPanel);
    }
}
