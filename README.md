# Steganography Desktop App
# ----------------------------------

## How to Build and Run

### Prerequisites
- Java JDK 11 or newer  
- Maven 3.6 or newer  
- MySQL running on localhost:3306

### 1. Set up the database

```sql
CREATE DATABASE IF NOT EXISTS steg;

USE steg;

CREATE TABLE IF NOT EXISTS reg (
    Userid   INT AUTO_INCREMENT PRIMARY KEY,
    F_name   VARCHAR(100) NOT NULL,
    L_name   VARCHAR(100) NOT NULL,
    Username VARCHAR(100) NOT NULL UNIQUE,
    Password VARCHAR(100) NOT NULL
);
```

### 2. Configure DB credentials (if different from defaults)

Open `src/main/java/com/steganography/repository/DatabaseConnection.java` 
and change these three constants as per your machine:

```java
private static final String DB_URLb= "jdbc:mysql://localhost:3306/steg";
private static final String USER = "root";
private static final String PASSWORD = "1234";
```

### 3. Build
`mvn clean install` OR `mvn clean install -U`

This creates `target/steganography-desktop-1.0.jar`

### 4. Run
java -jar target/Steganography-1.0.jar 

