# Sistem Bancar cu Agenți JADE

## Descriere
Acest sistem bancar este implementat folosind framework-ul JADE (Java Agent Development Framework) și simulează interacțiunea dintre o bancă și mai multe ATM-uri folosind un sistem multi-agent.

## Arhitectură

### Componente
1. **Agent Bancă (BankAgent)**
   - Gestionează conturile și tranzacțiile
   - Procesează cererile de la ATM-uri
   - Menține persistența datelor

2. **Agenți ATM (ATMAgent)**
   - Interfață grafică pentru utilizatori
   - Comunicare cu agentul bancă
   - Procesare operațiuni bancare

3. **Servicii**
   - PersistenceService: salvare/încărcare date
   - Sniffer Agent: monitorizare comunicație

### Protocol de Comunicare

#### 1. Autentificare
```
ATM -> Bancă: REQUEST
Content: "VALIDATE:accountNumber:pin"

Bancă -> ATM: INFORM/FAILURE
Content: "VALID:accountNumber" / "PIN_INVALID"
```

#### 2. Retragere Numerar
```
ATM -> Bancă: REQUEST
Content: "WITHDRAW:accountNumber:amount:atmId"

Bancă -> ATM: INFORM/FAILURE
Content: "SUCCES:newBalance" / "FONDURI_INSUFICIENTE"
```

#### 3. Verificare Sold
```
ATM -> Bancă: REQUEST
Content: "BALANCE:accountNumber"

Bancă -> ATM: INFORM/FAILURE
Content: "SOLD:balance" / "CONT_INEXISTENT"
```

## Caracteristici Avansate

### 1. Persistența Datelor
- Salvare conturi în format JSON
- Salvare tranzacții în format JSON
- Încărcare automată la pornire

### 2. Securitate
- Validare PIN
- Verificare fonduri disponibile
- Logging tranzacții

### 3. Interfață Grafică
- Panou de autentificare
- Panou operațiuni
- Istoric tranzacții
- Mesaje de stare

### 4. Monitorizare
- Sniffer Agent pentru urmărirea comunicației
- Logging evenimente sistem
- Notificări în timp real

## Conturi de Test
1. Ion Popescu
   - Cont: 1234567890
   - PIN: 1234
   - Sold: 1000.0 RON

2. Maria Ionescu
   - Cont: 0987654321
   - PIN: 4321
   - Sold: 2500.0 RON

3. Andrei Popa
   - Cont: 1111222233
   - PIN: 5555
   - Sold: 5000.0 RON

4. Elena Dumitrescu
   - Cont: 4444555566
   - PIN: 6789
   - Sold: 7500.0 RON

5. George Constantinescu
   - Cont: 7777888899
   - PIN: 9876
   - Sold: 10000.0 RON

## Instalare și Rulare

1. Clonare repository:
```bash
git clone [URL_REPOSITORY]
cd banking-micro-system
```

2. Compilare:
```bash
mvn clean install
```

3. Rulare:
```bash
mvn exec:java
```

## Diagrama de Secvență pentru Operațiuni

### Retragere Numerar
```
ATM                     Bancă
 |                        |
 |--- Validare PIN ------>|
 |<---- PIN Valid --------|
 |                        |
 |--- Cerere Retragere -->|
 |      Verificare Sold   |
 |      Actualizare Cont  |
 |<---- Confirmare -------|
 |      Afișare Rezultat  |
```

## Note Tehnice
- Java 11+
- JADE Framework 4.5.0
- Jackson pentru JSON
- Swing pentru GUI 