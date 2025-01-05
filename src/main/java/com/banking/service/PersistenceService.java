package com.banking.service;

import com.banking.model.Account;
import com.banking.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PersistenceService {
    private static final String ACCOUNTS_FILE = "accounts.json";
    private static final String TRANSACTIONS_FILE = "transactions.json";
    private final ObjectMapper objectMapper;

    public PersistenceService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void saveAccounts(Map<String, Account> accounts) {
        try {
            objectMapper.writeValue(new File(ACCOUNTS_FILE), accounts);
        } catch (IOException e) {
            System.err.println("Hesaplar kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    public Map<String, Account> loadAccounts() {
        try {
            File file = new File(ACCOUNTS_FILE);
            if (file.exists()) {
                return objectMapper.readValue(file, 
                    objectMapper.getTypeFactory().constructMapType(
                        HashMap.class, String.class, Account.class));
            }
        } catch (IOException e) {
            System.err.println("Hesaplar yüklenirken hata oluştu: " + e.getMessage());
        }
        return new HashMap<>();
    }

    public void saveTransactions(Map<String, Transaction> transactions) {
        try {
            objectMapper.writeValue(new File(TRANSACTIONS_FILE), transactions);
        } catch (IOException e) {
            System.err.println("İşlemler kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    public Map<String, Transaction> loadTransactions() {
        try {
            File file = new File(TRANSACTIONS_FILE);
            if (file.exists()) {
                return objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructMapType(
                        HashMap.class, String.class, Transaction.class));
            }
        } catch (IOException e) {
            System.err.println("İşlemler yüklenirken hata oluştu: " + e.getMessage());
        }
        return new HashMap<>();
    }
} 