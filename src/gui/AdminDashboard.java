package gui;

import dao.CandidateDAO;
import dao.VoterDAO;
import model.Candidate;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * College Voting Portal - Administrator Console.
 * Enables the election officer to nominate candidates, monitor turnout, and inspect results.
 */
public class AdminDashboard extends JFrame {

    private DefaultTableModel tableModel;
    private JTable candidateTable;
    private JLabel statsLabel;

    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final VoterDAO voterDAO = new VoterDAO();

    public AdminDashboard() {
        setTitle("Campus Election Commission - Administrator Console");
        setSize(800, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // ---------- Top Header Banner ----------
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(UITheme.NAVY);
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        JLabel titleLabel = new JLabel("ELECTION ADMINISTRATION CONSOLE");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        bannerPanel.add(titleLabel, BorderLayout.WEST);

        UITheme.StyledButton logoutBtn = UITheme.createSecondaryButton("Sign Out");
        logoutBtn.setPreferredSize(new Dimension(100, 32));
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        bannerPanel.add(logoutBtn, BorderLayout.EAST);

        mainPanel.add(bannerPanel, BorderLayout.NORTH);

        // ---------- Body Split: Table (Center) & Form (Right) ----------
        JPanel bodyPanel = new JPanel(new BorderLayout(15, 15));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        bodyPanel.setBackground(UITheme.BG_LIGHT);

        // Table Panel
        JPanel tableContainer = new JPanel(new BorderLayout(8, 8));
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel tableTitle = new JLabel("Official Candidate Register");
        tableTitle.setFont(UITheme.FONT_BOLD);
        tableTitle.setForeground(UITheme.TEXT_DARK);
        tableContainer.add(tableTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Candidate Name", "Party", "Symbol", "Votes"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        candidateTable = new JTable(tableModel);
        candidateTable.setRowHeight(30);
        candidateTable.setFont(UITheme.FONT_REGULAR);
        candidateTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        candidateTable.getTableHeader().setBackground(UITheme.BG_LIGHT);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        candidateTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        candidateTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        candidateTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        candidateTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane tableScroll = new JScrollPane(candidateTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableContainer.add(tableScroll, BorderLayout.CENTER);

        bodyPanel.add(tableContainer, BorderLayout.CENTER);

        // Right Panel: Form and Action Buttons
        JPanel rightPanel = new JPanel(new BorderLayout(10, 12));
        rightPanel.setPreferredSize(new Dimension(290, 420));
        rightPanel.setBackground(UITheme.BG_LIGHT);

        // Add Candidate Box
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 6, 6));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel formHeader = new JLabel("Nominate Candidate");
        formHeader.setFont(UITheme.FONT_BOLD);
        formHeader.setForeground(UITheme.TEXT_DARK);
        formPanel.add(formHeader);

        JTextField nameField = UITheme.createTextField();
        JTextField partyField = UITheme.createTextField();
        JTextField symbolField = UITheme.createTextField();

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Party / Slate:"));
        formPanel.add(partyField);
        formPanel.add(new JLabel("Election Symbol:"));
        formPanel.add(symbolField);

        UITheme.StyledButton addBtn = UITheme.createSuccessButton("Nominate Candidate");
        addBtn.setPreferredSize(new Dimension(250, 36));
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String party = partyField.getText().trim();
            String symbol = symbolField.getText().trim();

            if (name.isEmpty() || party.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Candidate name and party affiliation are required.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean added = candidateDAO.addCandidate(name, party, symbol);
            if (added) {
                nameField.setText("");
                partyField.setText("");
                symbolField.setText("");
                loadCandidates();
                JOptionPane.showMessageDialog(this, "Candidate successfully enrolled on the ballot!");
            } else {
                JOptionPane.showMessageDialog(this, "Could not add candidate. Please verify database connection.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(addBtn);

        rightPanel.add(formPanel, BorderLayout.NORTH);

        // Quick Actions
        JPanel actionsPanel = new JPanel(new GridLayout(3, 1, 8, 8));
        actionsPanel.setOpaque(false);

        UITheme.StyledButton resultBtn = UITheme.createPrimaryButton("View Live Standings");
        resultBtn.addActionListener(e -> new ResultFrame().setVisible(true));
        actionsPanel.add(resultBtn);

        UITheme.StyledButton deleteBtn = UITheme.createDangerButton("Remove Candidate");
        deleteBtn.addActionListener(e -> {
            int row = candidateTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a candidate row from the table first.", "Selection Needed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int candidateId = (int) tableModel.getValueAt(row, 0);
            String cName = (String) tableModel.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove candidate '" + cName + "' from the ballot?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                candidateDAO.deleteCandidate(candidateId);
                loadCandidates();
            }
        });
        actionsPanel.add(deleteBtn);

        UITheme.StyledButton refreshBtn = UITheme.createSecondaryButton("Refresh Standings");
        refreshBtn.addActionListener(e -> {
            loadCandidates();
            loadStats();
        });
        actionsPanel.add(refreshBtn);

        rightPanel.add(actionsPanel, BorderLayout.SOUTH);
        bodyPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(bodyPanel, BorderLayout.CENTER);

        // ---------- Bottom Stats Footer ----------
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        statsLabel = new JLabel("Turnout: Calculating...");
        statsLabel.setFont(UITheme.FONT_BOLD);
        statsLabel.setForeground(UITheme.TEXT_DARK);
        statsPanel.add(statsLabel, BorderLayout.WEST);

        JLabel sessionTag = new JLabel("Election Officer Session Active");
        sessionTag.setFont(UITheme.FONT_SMALL);
        sessionTag.setForeground(UITheme.EMERALD_GREEN);
        statsPanel.add(sessionTag, BorderLayout.EAST);

        mainPanel.add(statsPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadCandidates();
        loadStats();
    }

    private void loadCandidates() {
        tableModel.setRowCount(0);
        List<Candidate> candidates = candidateDAO.getAllCandidates();
        for (Candidate c : candidates) {
            tableModel.addRow(new Object[]{
                    c.getCandidateId(), c.getCandidateName(), c.getPartyName(), c.getSymbol(), c.getTotalVotes()
            });
        }
        loadStats();
    }

    private void loadStats() {
        int totalVoters = voterDAO.getTotalVoterCount();
        int totalVotesCast = voterDAO.getTotalVotesCastCount();
        statsLabel.setText("Official College Roll: " + totalVoters + " registered voters    |    Ballots Cast: " + totalVotesCast);
    }
}
