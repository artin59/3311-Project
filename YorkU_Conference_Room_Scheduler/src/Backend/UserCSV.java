package Backend;

import java.io.File;
import java.io.FileWriter;
import java.util.UUID;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class UserCSV {
    
    private static UserCSV instance = new UserCSV();
    private final String PATH = "../Database.csv";
        
    private UserCSV() { 
        try {
            File file = new File(PATH);
            
            if (!file.exists()) {
                CsvWriter csvWrite = new CsvWriter(new FileWriter(PATH, false), ',');
                csvWrite.write("ID");
                csvWrite.write("Type");
                csvWrite.write("Org ID");
                csvWrite.write("Email");
                csvWrite.write("Password");
                csvWrite.write("Date Created");
                csvWrite.endRecord();
                csvWrite.close();
                
                this.write(ChiefEventCoordinator.getCEOInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static UserCSV getInstance() {
        return instance;
    }
    
    public void write(Accounts a) {
        try {
            CsvWriter csvWrite = new CsvWriter(new FileWriter(PATH, true), ',');
            csvWrite.write(String.valueOf(a.getAccountId()));
            csvWrite.write(a.getAccountType());
            csvWrite.write((a instanceof User) ? ((User) a).getOrgID() : "-");
            csvWrite.write(a.getEmail());
            csvWrite.write(a.getPassword());
            csvWrite.write(String.valueOf(a.getCreatedDate()));
            csvWrite.endRecord();
            csvWrite.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Accounts find(UUID id) {
        Accounts account;
        try {
            CsvReader csvRead = new CsvReader(PATH);
            csvRead.readHeaders();
            
            while (csvRead.readRecord()) {
                if (csvRead.get("ID").equals(String.valueOf(id))) {
                    if (csvRead.get("Type").equals("Admin")) {    
                        account = new Admin(csvRead.get("Email"), csvRead.get("Password")); 
                    } else if (csvRead.get("Type").equals("Chief Event Coordinator")) {
                        csvRead.close();
                        return ChiefEventCoordinator.getCEOInstance();
                    } else {
                        UserFactory factory = new UserFactory();
                        account = factory.createUser(
                            csvRead.get("Email"), 
                            csvRead.get("Password"), 
                            csvRead.get("Type"), 
                            csvRead.get("Org ID")
                        );
                    }
                    csvRead.close();
                    return account;
                }
            }
            csvRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;     
    }
    
    public Accounts findByEmail(String email) {
        Accounts account;
        try {
            CsvReader csvRead = new CsvReader(PATH);
            csvRead.readHeaders();
            
            while (csvRead.readRecord()) {
                if (csvRead.get("Email").equalsIgnoreCase(email)) {
                    String type = csvRead.get("Type");
                    String storedEmail = csvRead.get("Email");
                    String storedPassword = csvRead.get("Password");
                    String orgId = csvRead.get("Org ID");
                    
                    if (type.equals("Admin")) {    
                        account = new Admin(storedEmail, storedPassword); 
                    } else if (type.equals("Chief Event Coordinator")) {
                        csvRead.close();
                        return ChiefEventCoordinator.getCEOInstance();
                    } else {
                        UserFactory factory = new UserFactory();
                        account = factory.createUser(storedEmail, storedPassword, type, orgId);
                    }
                    csvRead.close();
                    return account;
                }
            }
            csvRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;     
    }
    
    public boolean emailExists(String email) {
        try {
            CsvReader csvRead = new CsvReader(PATH);
            csvRead.readHeaders();
            
            while (csvRead.readRecord()) {
                if (csvRead.get("Email").equalsIgnoreCase(email)) {
                    csvRead.close();
                    return true;
                }
            }
            csvRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public java.util.List<Accounts> findAll() {
        java.util.List<Accounts> accounts = new java.util.ArrayList<>();
        try {
            CsvReader csvRead = new CsvReader(PATH);
            csvRead.readHeaders();
            
            while (csvRead.readRecord()) {
                String type = csvRead.get("Type");
                String storedEmail = csvRead.get("Email");
                String storedPassword = csvRead.get("Password");
                String orgId = csvRead.get("Org ID");
                
                Accounts account;
                if (type.equals("Admin")) {    
                    account = new Admin(storedEmail, storedPassword); 
                } else if (type.equals("Chief Event Coordinator")) {
                    account = ChiefEventCoordinator.getCEOInstance();
                } else {
                    UserFactory factory = new UserFactory();
                    account = factory.createUser(storedEmail, storedPassword, type, orgId);
                }
                accounts.add(account);
            }
            csvRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }
    
    public void update(Accounts updatedAccount) {
        java.util.List<Accounts> allAccounts = findAll();
        
        try {
            // Rewrite entire file with headers
            CsvWriter csvWrite = new CsvWriter(new FileWriter(PATH, false), ',');
            csvWrite.write("ID");
            csvWrite.write("Type");
            csvWrite.write("Org ID");
            csvWrite.write("Email");
            csvWrite.write("Password");
            csvWrite.write("Date Created");
            csvWrite.endRecord();
            
            for (Accounts account : allAccounts) {
                if (account.getAccountId().equals(updatedAccount.getAccountId())) {
                    // Write updated account
                    writeAccountRecord(csvWrite, updatedAccount);
                } else {
                    // Write existing account
                    writeAccountRecord(csvWrite, account);
                }
            }
            csvWrite.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void writeAccountRecord(CsvWriter csvWrite, Accounts account) throws Exception {
        csvWrite.write(String.valueOf(account.getAccountId()));
        csvWrite.write(account.getAccountType());
        csvWrite.write((account instanceof User) ? ((User) account).getOrgID() : "-");
        csvWrite.write(account.getEmail());
        csvWrite.write(account.getPassword());
        csvWrite.write(String.valueOf(account.getCreatedDate()));
        csvWrite.endRecord();
    }
    public void updateAccountTypeByEmail(String email, String newType) {
        try {
            java.util.List<String[]> allRows = new java.util.ArrayList<>();
            CsvReader csvRead = new CsvReader(PATH);
            csvRead.readHeaders();
            
            String[] headers = csvRead.getHeaders();
            allRows.add(headers);
            
            // Read all rows and update the matching one
            while (csvRead.readRecord()) {
                String rowEmail = csvRead.get("Email");
                if (rowEmail.equalsIgnoreCase(email)) {
                    String[] row = new String[headers.length];
                    row[0] = csvRead.get("ID");
                    row[1] = newType; 
                    row[2] = csvRead.get("Org ID"); 
                    row[3] = csvRead.get("Email");
                    row[4] = csvRead.get("Password");
                    row[5] = csvRead.get("Date Created");
                    allRows.add(row);
                    System.out.println("Found account with email " + email + " - updating type to " + newType);
                } else {
                    // Keep existing row as-is
                    String[] row = new String[headers.length];
                    for (int i = 0; i < headers.length; i++) {
                        row[i] = csvRead.get(i);
                    }
                    allRows.add(row);
                }
            }
            csvRead.close();
            
            // Write everything back
            CsvWriter csvWrite = new CsvWriter(new FileWriter(PATH, false), ',');
            for (String[] row : allRows) {
                for (String value : row) {
                    csvWrite.write(value);
                }
                csvWrite.endRecord();
            }
            csvWrite.close();
            
            System.out.println("Account type updated to " + newType + " for email " + email);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}