package com.banking.gui;

import com.banking.agents.ATMAgent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ATMGUI extends JFrame {
    private ATMAgent agent;
    private JPanel loginPanel;
    private JPanel operationsPanel;
    private JTextField accountField;
    private JPasswordField pinField;
    private JTextField amountField;
    private JLabel messageLabel;
    private String currentAccount;

    public ATMGUI(ATMAgent agent) {
        this.agent = agent;
        setupGUI();
    }

    private void setupGUI() {
        setTitle("ATM - " + agent.getLocalName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        // Panou principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panou de autentificare
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createTitledBorder("Autentificare"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        accountField = new JTextField(15);
        pinField = new JPasswordField(15);
        JButton loginButton = new JButton("Autentificare");

        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(new JLabel("Număr Cont:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(accountField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("PIN:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(pinField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        // Panou operațiuni
        operationsPanel = new JPanel(new BorderLayout(10, 10));
        operationsPanel.setBorder(BorderFactory.createTitledBorder("Operațiuni"));

        // Panou stânga pentru operațiuni
        JPanel leftPanel = new JPanel(new GridBagLayout());
        amountField = new JTextField(15);
        JButton withdrawButton = new JButton("Retragere");
        JButton balanceButton = new JButton("Verificare Sold");
        JButton logoutButton = new JButton("Ieșire");

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        leftPanel.add(new JLabel("Sumă:"), gbc);
        gbc.gridx = 1;
        leftPanel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        leftPanel.add(withdrawButton, gbc);

        gbc.gridy = 2;
        leftPanel.add(balanceButton, gbc);

        gbc.gridy = 3;
        leftPanel.add(logoutButton, gbc);

        // Panou dreapta pentru informații
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Informații Cont"));

        // Etichetă mesaje
        messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Zonă pentru istoric tranzacții
        JTextArea historyArea = new JTextArea(10, 30);
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Istoric Tranzacții"));

        rightPanel.add(messageLabel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        operationsPanel.add(leftPanel, BorderLayout.WEST);
        operationsPanel.add(rightPanel, BorderLayout.CENTER);

        // Event Handlers
        loginButton.addActionListener(e -> {
            String account = accountField.getText();
            String pin = new String(pinField.getPassword());
            agent.validatePin(account, pin);
        });

        withdrawButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showMessage("Suma trebuie să fie pozitivă!", Color.RED);
                    return;
                }
                agent.withdraw(currentAccount, amount);
            } catch (NumberFormatException ex) {
                showMessage("Sumă invalidă!", Color.RED);
            }
        });

        balanceButton.addActionListener(e -> {
            agent.checkBalance(currentAccount);
        });

        logoutButton.addActionListener(e -> {
            showLoginPanel();
            currentAccount = null;
            accountField.setText("");
            pinField.setText("");
            amountField.setText("");
            historyArea.setText("");
            showMessage(" ", Color.BLACK);
        });

        // Configurare inițială
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        add(mainPanel);
        operationsPanel.setVisible(false);
        setVisible(true);
    }

    public void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }

    public void loginSuccess(String account) {
        currentAccount = account;
        showOperationsPanel();
        showMessage("Bine ați venit!", Color.BLACK);
    }

    public void loginFailed() {
        showMessage("Număr cont sau PIN invalid!", Color.RED);
        pinField.setText("");
    }

    public void showBalance(double balance) {
        showMessage(String.format("Sold curent: %.2f RON", balance), Color.BLACK);
    }

    public void withdrawalSuccess(double newBalance) {
        showMessage(String.format("Tranzacție reușită. Sold nou: %.2f RON", newBalance), Color.BLACK);
        amountField.setText("");
    }

    public void withdrawalFailed() {
        showMessage("Fonduri insuficiente!", Color.RED);
        amountField.setText("");
    }

    private void showLoginPanel() {
        getContentPane().removeAll();
        getContentPane().add(loginPanel);
        loginPanel.setVisible(true);
        operationsPanel.setVisible(false);
        validate();
        repaint();
    }

    private void showOperationsPanel() {
        getContentPane().removeAll();
        getContentPane().add(operationsPanel);
        loginPanel.setVisible(false);
        operationsPanel.setVisible(true);
        validate();
        repaint();
    }
} 