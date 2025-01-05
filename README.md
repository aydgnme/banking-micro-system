# Sistem Bancar Micro cu JADE

Acest proiect este un sistem bancar bazat pe agenți, dezvoltat folosind JADE (Java Agent Development Framework). Sistemul constă într-un agent bancar central și mai mulți agenți ATM.

## Caracteristici

- Agent Bancar Central
- Agenți ATM Multipli
- Interfață Grafică (GUI)
- Validare PIN
- Operațiuni de Retragere
- Verificare Sold
- Înregistrare Tranzacții
- Creare Dinamică de Agenți

## Cerințe

- Java 11 sau mai nou
- Maven
- JADE Framework

## Instalare

1. Clonați proiectul:
```bash
git clone [repository-url]
cd banking-micro-system
```

2. Instalați dependențele Maven:
```bash
mvn clean install
```

## Rulare

Pentru a porni sistemul:

```bash
mvn exec:java -Dexec.mainClass="com.banking.BankingSystem"
```

## Utilizare

1. La pornire, sistemul creează automat un Agent Bancar și trei Agenți ATM.
2. Pentru fiecare ATM se deschide o fereastră GUI separată.
3. Detalii cont de test:
   - Număr Cont: 1234567890
   - PIN: 1234

### Operațiuni ATM

1. Autentificare:
   - Introduceți numărul de cont și PIN-ul
   - Apăsați butonul "Autentificare"

2. Retragere:
   - Introduceți suma
   - Apăsați butonul "Retragere"

3. Verificare Sold:
   - Apăsați butonul "Verificare Sold"

## Arhitectura Sistemului

- `BankAgent`: Gestionare conturi, validare PIN și procesare tranzacții
- `ATMAgent`: Interfață utilizator și comunicare cu agentul bancar
- `Account`: Model date cont
- `Transaction`: Model date tranzacție

## Securitate

- Validare PIN
- Înregistrare Tranzacții
- Protocol Securizat de Comunicare

## Licență

Acest proiect este licențiat sub licența MIT. 