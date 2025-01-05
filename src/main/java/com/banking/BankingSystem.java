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
            // Get JADE runtime
            Runtime rt = Runtime.instance();

            // Create a default profile
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            profile.setParameter(Profile.GUI, "true");

            // Create main container
            AgentContainer mainContainer = rt.createMainContainer(profile);

            // Create and start the Bank agent
            AgentController bankAgent = mainContainer.createNewAgent(
                "bank",
                "com.banking.agents.BankAgent",
                null
            );
            bankAgent.start();

            // Create and start ATM agents
            for (int i = 1; i <= 3; i++) {
                AgentController atmAgent = mainContainer.createNewAgent(
                    "atm" + i,
                    "com.banking.agents.ATMAgent",
                    null
                );
                atmAgent.start();
            }

            System.out.println("Banking System started successfully!");

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
} 