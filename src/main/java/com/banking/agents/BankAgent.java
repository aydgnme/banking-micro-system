package com.banking.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.service.PersistenceService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

public class BankAgent extends Agent {
    private Map<String, Account> accounts = new HashMap<>();
    private Map<String, Transaction> transactions = new HashMap<>();
    private PersistenceService persistenceService;

    @Override
    protected void setup() {
        persistenceService = new PersistenceService();
        
        // Încărcare conturi existente
        accounts = persistenceService.loadAccounts();
        transactions = persistenceService.loadTransactions();
        
        // Creare conturi de test dacă nu există
        if (accounts.isEmpty()) {
            createTestAccounts();
            persistenceService.saveAccounts(accounts);
        }

        // Adăugare comportament pentru gestionarea cererilor ATM
        addBehaviour(new HandleATMRequests());
        
        System.out.println("Agent Bancă " + getAID().getName() + " este pregătit.");
        System.out.println("Conturi existente:");
        accounts.forEach((accountNumber, account) -> {
            System.out.println("Număr Cont: " + accountNumber + 
                             ", PIN: " + account.getPin() + 
                             ", Sold: " + account.getBalance() + 
                             ", Titular: " + account.getOwnerName());
        });
    }

    @Override
    protected void takeDown() {
        // Salvare date
        persistenceService.saveAccounts(accounts);
        persistenceService.saveTransactions(transactions);
        System.out.println("Agent Bancă se închide. Date salvate.");
    }

    private void createTestAccounts() {
        // Conturi de test
        Account account1 = new Account("1234567890", "1234", 1000.0, "Ion Popescu");
        Account account2 = new Account("0987654321", "4321", 2500.0, "Maria Ionescu");
        Account account3 = new Account("1111222233", "5555", 5000.0, "Andrei Popa");
        Account account4 = new Account("4444555566", "6789", 7500.0, "Elena Dumitrescu");
        Account account5 = new Account("7777888899", "9876", 10000.0, "George Constantinescu");

        // Salvare conturi
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
                    // Validare PIN
                    String[] parts = content.split(":");
                    String accountNumber = parts[1];
                    String pin = parts[2];
                    
                    Account account = accounts.get(accountNumber);
                    if (account != null && account.getPin().equals(pin)) {
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("VALID:" + accountNumber);
                    } else {
                        reply.setPerformative(ACLMessage.FAILURE);
                        reply.setContent("PIN_INVALID");
                    }
                } else if (content.startsWith("WITHDRAW:")) {
                    // Procesare retragere numerar
                    String[] parts = content.split(":");
                    String accountNumber = parts[1];
                    double amount = Double.parseDouble(parts[2]);
                    String atmId = parts[3];
                    
                    Account account = accounts.get(accountNumber);
                    if (account != null && account.getBalance() >= amount) {
                        account.setBalance(account.getBalance() - amount);
                        
                        // Înregistrare tranzacție
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
                        
                        // Salvare date
                        persistenceService.saveAccounts(accounts);
                        persistenceService.saveTransactions(transactions);
                        
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("SUCCES:" + account.getBalance());
                    } else {
                        reply.setPerformative(ACLMessage.FAILURE);
                        reply.setContent("FONDURI_INSUFICIENTE");
                    }
                } else if (content.startsWith("BALANCE:")) {
                    // Verificare sold
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