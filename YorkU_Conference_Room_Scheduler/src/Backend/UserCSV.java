package Backend;

import java.io.File;
import java.io.FileWriter;
import java.util.UUID;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class UserCSV {
    
    private static UserCSV instance = new UserCSV();
    private final String PATH = "A:\\EECS 3311\\EECS 3311\\Database.csv";
        
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
                        account.setAccountId(id); // Preserve original UUID
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
                        account.setAccountId(id); // Preserve original UUID
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
                    String storedId = csvRead.get("ID");
                    
                    if (type.equals("Admin")) {    
                        account = new Admin(storedEmail, storedPassword);
                        try {
                            account.setAccountId(UUID.fromString(storedId)); // Preserve original UUID
                        } catch (Exception e) {
                            // If UUID parsing fails, keep the new one
                        }
                    } else if (type.equals("Chief Event Coordinator")) {
                        csvRead.close();
                        return ChiefEventCoordinator.getCEOInstance();
                    } else {
                        UserFactory factory = new UserFactory();
                        account = factory.createUser(storedEmail, storedPassword, type, orgId);
                        try {
                            account.setAccountId(UUID.fromString(storedId)); // Preserve original UUID
                        } catch (Exception e) {
                            // If UUID parsing fails, keep the new one
                        }
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
}