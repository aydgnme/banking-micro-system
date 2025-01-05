package com.banking.agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import com.banking.gui.ATMGUI;

public class ATMAgent extends Agent {
    private ATMGUI gui;
    private AID bankAgent;

    @Override
    protected void setup() {
        // Găsire agent bancă
        bankAgent = new AID("BankAgent", AID.ISLOCALNAME);
        
        // Inițializare GUI
        gui = new ATMGUI(this);
        
        // Adăugare comportament pentru procesarea răspunsurilor de la bancă
        addBehaviour(new HandleBankResponses());
        
        System.out.println("Agent ATM " + getLocalName() + " este pregătit.");
    }

    public void validatePin(String accountNumber, String pin) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(bankAgent);
        msg.setContent("VALIDATE:" + accountNumber + ":" + pin);
        send(msg);
    }

    public void withdraw(String accountNumber, double amount) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(bankAgent);
        msg.setContent("WITHDRAW:" + accountNumber + ":" + amount + ":" + getLocalName());
        send(msg);
    }

    public void checkBalance(String accountNumber) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(bankAgent);
        msg.setContent("BALANCE:" + accountNumber);
        send(msg);
    }

    private class HandleBankResponses extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchPerformative(ACLMessage.FAILURE)
            );
            ACLMessage msg = myAgent.receive(mt);
            
            if (msg != null) {
                String content = msg.getContent();
                
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    if (content.startsWith("VALID")) {
                        gui.loginSuccess(content.split(":")[1]);
                    } else if (content.startsWith("SUCCES:")) {
                        double newBalance = Double.parseDouble(content.split(":")[1]);
                        gui.withdrawalSuccess(newBalance);
                    } else if (content.startsWith("SOLD:")) {
                        double balance = Double.parseDouble(content.split(":")[1]);
                        gui.showBalance(balance);
                    }
                } else {
                    if (content.equals("PIN_INVALID")) {
                        gui.loginFailed();
                    } else if (content.equals("FONDURI_INSUFICIENTE")) {
                        gui.withdrawalFailed();
                    }
                }
            } else {
                block();
            }
        }
    }

    @Override
    protected void takeDown() {
        if (gui != null) {
            gui.dispose();
        }
        System.out.println("Agent ATM " + getLocalName() + " se închide.");
    }
} 