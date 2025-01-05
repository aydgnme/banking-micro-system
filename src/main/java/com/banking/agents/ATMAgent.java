package com.banking.agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import javax.swing.*;
import java.awt.*;

public class ATMAgent extends Agent {
    private JFrame frame;
    private JTextField accountField;
    private JPasswordField pinField;
    private JTextField amountField;
    private JTextArea messageArea;
    private String currentAccount;
    private boolean isAuthenticated = false;
    private AID bankAgent;

    @Override
    protected void setup() {
        // Referință la agentul bancă
        bankAgent = new AID("bank", AID.ISLOCALNAME);
        
        // Creare și afișare GUI
        SwingUtilities.invokeLater(() -> createAndShowGUI());
        
        System.out.println("Agent ATM " + getAID().getName() + " este pregătit.");
    }

    private void createAndShowGUI() {
        frame = new JFrame("ATM - " + getAID().getLocalName());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout(10, 10));

        // Panou de autentificare
        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Autentificare"));
        
        accountField = new JTextField(15);
        pinField = new JPasswordField(15);
        JButton loginButton = new JButton("Autentificare");
        
        loginPanel.add(new JLabel("Număr Cont:"));
        loginPanel.add(accountField);
        loginPanel.add(new JLabel("PIN:"));
        loginPanel.add(pinField);
        loginPanel.add(new JLabel(""));
        loginPanel.add(loginButton);

        // Panou tranzacții
        JPanel transactionPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Tranzacții"));
        
        amountField = new JTextField(15);
        JButton withdrawButton = new JButton("Retragere");
        JButton balanceButton = new JButton("Verificare Sold");
        
        transactionPanel.add(new JLabel("Sumă:"));
        transactionPanel.add(amountField);
        transactionPanel.add(new JLabel(""));
        transactionPanel.add(withdrawButton);
        transactionPanel.add(new JLabel(""));
        transactionPanel.add(balanceButton);

        // Zonă mesaje
        messageArea = new JTextArea(10, 40);
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        frame.add(loginPanel, BorderLayout.NORTH);
        frame.add(transactionPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> {
            String account = accountField.getText();
            String pin = new String(pinField.getPassword());
            validateCredentials(account, pin);
        });

        withdrawButton.addActionListener(e -> {
            if (!isAuthenticated) {
                showMessage("Vă rugăm să vă autentificați mai întâi");
                return;
            }
            try {
                double amount = Double.parseDouble(amountField.getText());
                withdraw(amount);
            } catch (NumberFormatException ex) {
                showMessage("Sumă invalidă");
            }
        });

        balanceButton.addActionListener(e -> {
            if (!isAuthenticated) {
                showMessage("Vă rugăm să vă autentificați mai întâi");
                return;
            }
            checkBalance();
        });

        frame.setVisible(true);
    }

    private void validateCredentials(String account, String pin) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(bankAgent);
                msg.setContent("VALIDATE:" + account + ":" + pin);
                send(msg);
                
                ACLMessage reply = blockingReceive();
                if (reply != null) {
                    if (reply.getPerformative() == ACLMessage.INFORM) {
                        isAuthenticated = true;
                        currentAccount = account;
                        showMessage("Autentificare reușită");
                    } else {
                        showMessage("Credențiale invalide");
                    }
                }
            }
        });
    }

    private void withdraw(double amount) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(bankAgent);
                msg.setContent("WITHDRAW:" + currentAccount + ":" + amount + ":" + getAID().getLocalName());
                send(msg);
                
                ACLMessage reply = blockingReceive();
                if (reply != null) {
                    if (reply.getPerformative() == ACLMessage.INFORM) {
                        String[] parts = reply.getContent().split(":");
                        showMessage("Retragere reușită. Sold nou: " + parts[1]);
                    } else {
                        showMessage("Retragere eșuată: " + reply.getContent());
                    }
                }
            }
        });
    }

    private void checkBalance() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(bankAgent);
                msg.setContent("BALANCE:" + currentAccount);
                send(msg);
                
                ACLMessage reply = blockingReceive();
                if (reply != null) {
                    if (reply.getPerformative() == ACLMessage.INFORM) {
                        String[] parts = reply.getContent().split(":");
                        showMessage("Sold curent: " + parts[1]);
                    } else {
                        showMessage("Eroare la verificarea soldului");
                    }
                }
            }
        });
    }

    private void showMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.append(message + "\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        });
    }

    @Override
    protected void takeDown() {
        if (frame != null) {
            frame.dispose();
        }
        System.out.println("Agent ATM " + getAID().getName() + " se închide.");
    }
} 