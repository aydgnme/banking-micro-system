package com.banking;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class BankingSystem {
    public static void main(String[] args) {
        try {
            // Pornire runtime JADE
            Runtime rt = Runtime.instance();
            
            // Configurare platformă
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            profile.setParameter(Profile.GUI, "true");
            
            // Creare container principal
            System.out.println("Pornire container principal...");
            AgentContainer mainContainer = rt.createMainContainer(profile);
            
            // Pornire agent bancă
            System.out.println("Pornire agent bancă...");
            AgentController bankAgent = mainContainer.createNewAgent(
                "BankAgent",
                "com.banking.agents.BankAgent",
                null
            );
            bankAgent.start();
            
            // Pornire agenți ATM
            System.out.println("Pornire agenți ATM...");
            for (int i = 1; i <= 3; i++) {
                AgentController atmAgent = mainContainer.createNewAgent(
                    "ATM" + i,
                    "com.banking.agents.ATMAgent",
                    null
                );
                atmAgent.start();
            }
            
            System.out.println("Sistem bancar pornit cu succes.");
            System.out.println("Pentru a monitoriza comunicația între agenți, deschideți Remote Agent Management GUI și porniți Sniffer Agent.");
            
        } catch (StaleProxyException e) {
            System.err.println("Eroare la pornirea sistemului: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 