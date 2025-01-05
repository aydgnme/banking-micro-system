package com.banking.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import com.banking.model.Account;
import com.banking.model.Transaction;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

public class BankAgent extends Agent {
    // Stocarea conturilor și tranzacțiilor
    private Map<String, Account> accounts = new HashMap<>();
    private Map<String, Transaction> transactions = new HashMap<>();

    @Override
    protected void setup() {
        // Test hesaplarını oluştur
        createTestAccounts();

        // ATM isteklerini işlemek için davranış ekle
        addBehaviour(new HandleATMRequests());
        
        System.out.println("Agent Bancă " + getAID().getName() + " este pregătit.");
        System.out.println("Test hesapları oluşturuldu:");
        accounts.forEach((accountNumber, account) -> {
            System.out.println("Hesap No: " + accountNumber + 
                             ", PIN: " + account.getPin() + 
                             ", Bakiye: " + account.getBalance() + 
                             ", Sahibi: " + account.getOwnerName());
        });
    }

    private void createTestAccounts() {
        // Test hesapları
        Account account1 = new Account("1234567890", "1234", 1000.0, "Ion Popescu");
        Account account2 = new Account("0987654321", "4321", 2500.0, "Maria Ionescu");
        Account account3 = new Account("1111222233", "5555", 5000.0, "Andrei Popa");
        Account account4 = new Account("4444555566", "6789", 7500.0, "Elena Dumitrescu");
        Account account5 = new Account("7777888899", "9876", 10000.0, "George Constantinescu");

        // Hesapları kaydet
        accounts.put(account1.getAccountNumber(), account1);
        accounts.put(account2.getAccountNumber(), account2);
        accounts.put(account3.getAccountNumber(), account3);
        accounts.put(account4.getAccountNumber(), account4);
        accounts.put(account5.getAccountNumber(), account5);
    }

    private class HandleATMRequests extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            
            if (msg != null) {
                String content = msg.getContent();
                ACLMessage reply = msg.createReply();

                if (content.startsWith("VALIDATE:")) {
                    // PIN doğrulama
                    String[] parts = content.split(":");
                    String accountNumber = parts[1];
                    String pin = parts[2];
                    
                    Account account = accounts.get(accountNumber);
                    if (account != null && account.getPin().equals(pin)) {
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("VALID");
                    } else {
                        reply.setPerformative(ACLMessage.FAILURE);
                        reply.setContent("PIN_INVALID");
                    }
                } else if (content.startsWith("WITHDRAW:")) {
                    // Para çekme işlemi
                    String[] parts = content.split(":");
                    String accountNumber = parts[1];
                    double amount = Double.parseDouble(parts[2]);
                    String atmId = parts[3];
                    
                    Account account = accounts.get(accountNumber);
                    if (account != null && account.getBalance() >= amount) {
                        account.setBalance(account.getBalance() - amount);
                        
                        // İşlem kaydı
                        Transaction transaction = new Transaction(
                            UUID.randomUUID().toString(),
                            accountNumber,
                            Transaction.TransactionType.WITHDRAWAL,
                            amount,
                            LocalDateTime.now(),
                            atmId,
                            Transaction.TransactionStatus.SUCCESS
                        );
                        transactions.put(transaction.getTransactionId(), transaction);
                        
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("SUCCES:" + account.getBalance());
                    } else {
                        reply.setPerformative(ACLMessage.FAILURE);
                        reply.setContent("FONDURI_INSUFICIENTE");
                    }
                } else if (content.startsWith("BALANCE:")) {
                    // Bakiye sorgulama
                    String accountNumber = content.split(":")[1];
                    Account account = accounts.get(accountNumber);
                    
                    if (account != null) {
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("SOLD:" + account.getBalance());
                    } else {
                        reply.setPerformative(ACLMessage.FAILURE);
                        reply.setContent("CONT_INEXISTENT");
                    }
                }
                
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }
} 