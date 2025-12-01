package Backend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.UUID;
import java.lang.reflect.Field;

import com.csvreader.CsvWriter;

public class UserCSVTest {
    
    private UserCSV userCSV;
    private String originalUserPath;
    
    private static final String TEST_USER_PATH = "TestDatabase.csv";
    
    private UserCSV createIsolatedUserCSV() throws Exception {
        String isolatedPath = "IsolatedUserTestDatabase.csv";
        UserCSV userCSV = UserCSV.getInstance();
        Field pathField = UserCSV.class.getDeclaredField("PATH");
        pathField.setAccessible(true);
        pathField.set(userCSV, isolatedPath);
        
        File isolatedFile = new File(isolatedPath);
        if (isolatedFile.exists()) {
            isolatedFile.delete();
        }
        
        CsvWriter csvWrite = new CsvWriter(new FileWriter(isolatedPath, false), ',');
        csvWrite.write("ID");
        csvWrite.write("Type");
        csvWrite.write("Org ID");
        csvWrite.write("Email");
        csvWrite.write("Password");
        csvWrite.write("Date Created");
        csvWrite.endRecord();
        csvWrite.close();
        
        userCSV.write(ChiefEventCoordinator.getCEOInstance());
        
        return userCSV;
    }
    
    private void cleanupIsolatedUserCSV() throws Exception {
        String isolatedPath = "IsolatedUserTestDatabase.csv";
        File isolatedFile = new File(isolatedPath);
        if (isolatedFile.exists()) {
            isolatedFile.delete();
        }
        
        UserCSV userCSV = UserCSV.getInstance();
        Field pathField = UserCSV.class.getDeclaredField("PATH");
        pathField.setAccessible(true);
        pathField.set(userCSV, TEST_USER_PATH);
    }
    
    @Test
    public void testUserCSV_GetInstance() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            UserCSV instance1 = UserCSV.getInstance();
            UserCSV instance2 = UserCSV.getInstance();
            assertNotNull("Instance should not be null", instance1);
            assertSame("Should return same instance", instance1, instance2);
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Write_Student() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student = new Student("student@yorku.ca", "password123", "12345678");
            isolated.write(student);
            
            Accounts found = isolated.findByEmail("student@yorku.ca");
            assertNotNull("Student should be written", found);
            assertEquals("Email should match", "student@yorku.ca", found.getEmail());
            assertEquals("Account type should be Student", "Student", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Write_Admin() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Admin admin = new Admin("admin@yorku.ca", "adminpass");
            isolated.write(admin);
            
            Accounts found = isolated.findByEmail("admin@yorku.ca");
            assertNotNull("Admin should be written", found);
            assertEquals("Email should match", "admin@yorku.ca", found.getEmail());
            assertEquals("Account type should be Admin", "Admin", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Write_Faculty() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Faculty faculty = new Faculty("faculty@yorku.ca", "facultypass", "FAC001");
            isolated.write(faculty);
            
            Accounts found = isolated.findByEmail("faculty@yorku.ca");
            assertNotNull("Faculty should be written", found);
            assertEquals("Email should match", "faculty@yorku.ca", found.getEmail());
            assertEquals("Account type should be Faculty", "Faculty", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Write_Staff() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Staff staff = new Staff("staff@yorku.ca", "staffpass", "STAFF001");
            isolated.write(staff);
            
            Accounts found = isolated.findByEmail("staff@yorku.ca");
            assertNotNull("Staff should be written", found);
            assertEquals("Email should match", "staff@yorku.ca", found.getEmail());
            assertEquals("Account type should be Staff", "Staff", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Write_ExternalPartner() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            ExternalPartner partner = new ExternalPartner("partner@external.com", "partnerpass", "ORG001");
            isolated.write(partner);
            
            Accounts found = isolated.findByEmail("partner@external.com");
            assertNotNull("External Partner should be written", found);
            assertEquals("Email should match", "partner@external.com", found.getEmail());
            assertEquals("Account type should be External Partner", "External Partner", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Find_Student() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student = new Student("findstudent@yorku.ca", "password123", "87654321");
            isolated.write(student);
            
            UUID studentId = student.getAccountId();
            Accounts found = isolated.find(studentId);
            assertNotNull("Should find student by ID", found);
            assertEquals("Email should match", "findstudent@yorku.ca", found.getEmail());
            assertEquals("Account type should be Student", "Student", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Find_Admin() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Admin admin = new Admin("findadmin@yorku.ca", "adminpass");
            isolated.write(admin);
            
            UUID adminId = admin.getAccountId();
            Accounts found = isolated.find(adminId);
            assertNotNull("Should find admin by ID", found);
            assertEquals("Email should match", "findadmin@yorku.ca", found.getEmail());
            assertEquals("Account type should be Admin", "Admin", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Find_ChiefEventCoordinator() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            ChiefEventCoordinator ceo = ChiefEventCoordinator.getCEOInstance();
            UUID ceoId = ceo.getAccountId();
            
            Accounts found = isolated.find(ceoId);
            assertNotNull("Should find CEO by ID", found);
            assertEquals("Should return CEO instance", ceo, found);
            assertEquals("Account type should be Chief Event Coordinator", "Chief Event Coordinator", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Find_NotFound() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            UUID nonExistentId = UUID.randomUUID();
            Accounts found = isolated.find(nonExistentId);
            assertNull("Should return null for non-existent ID", found);
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_FindByEmail_Student() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student = new Student("findbystudent@yorku.ca", "password123", "11111111");
            isolated.write(student);
            
            Accounts found = isolated.findByEmail("findbystudent@yorku.ca");
            assertNotNull("Should find student by email", found);
            assertEquals("Email should match", "findbystudent@yorku.ca", found.getEmail());
            assertEquals("Account type should be Student", "Student", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_FindByEmail_Admin() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Admin admin = new Admin("findbyadmin@yorku.ca", "adminpass");
            isolated.write(admin);
            
            Accounts found = isolated.findByEmail("findbyadmin@yorku.ca");
            assertNotNull("Should find admin by email", found);
            assertEquals("Email should match", "findbyadmin@yorku.ca", found.getEmail());
            assertEquals("Account type should be Admin", "Admin", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_FindByEmail_ChiefEventCoordinator() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            ChiefEventCoordinator ceo = ChiefEventCoordinator.getCEOInstance();
            
            Accounts found = isolated.findByEmail("chief@gmail.com");
            assertNotNull("Should find CEO by email", found);
            assertEquals("Should return CEO instance", ceo, found);
            assertEquals("Account type should be Chief Event Coordinator", "Chief Event Coordinator", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_FindByEmail_CaseInsensitive() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student = new Student("caseinsensitive@yorku.ca", "password123", "22222222");
            isolated.write(student);
            
            Accounts found = isolated.findByEmail("CASEINSENSITIVE@YORKU.CA");
            assertNotNull("Should find student with case-insensitive email", found);
            assertEquals("Email should match", "caseinsensitive@yorku.ca", found.getEmail());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_FindByEmail_NotFound() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Accounts found = isolated.findByEmail("nonexistent@yorku.ca");
            assertNull("Should return null for non-existent email", found);
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_EmailExists_True() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student = new Student("exists@yorku.ca", "password123", "33333333");
            isolated.write(student);
            
            boolean exists = isolated.emailExists("exists@yorku.ca");
            assertTrue("Email should exist", exists);
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_EmailExists_CaseInsensitive() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student = new Student("caseexists@yorku.ca", "password123", "44444444");
            isolated.write(student);
            
            boolean exists = isolated.emailExists("CASEEXISTS@YORKU.CA");
            assertTrue("Email should exist with case-insensitive check", exists);
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_EmailExists_False() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            boolean exists = isolated.emailExists("doesnotexist@yorku.ca");
            assertFalse("Email should not exist", exists);
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_FindAll() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student1 = new Student("all1@yorku.ca", "password123", "55555555");
            Student student2 = new Student("all2@yorku.ca", "password123", "66666666");
            isolated.write(student1);
            isolated.write(student2);
            
            List<Accounts> allAccounts = isolated.findAll();
            assertNotNull("Should return list", allAccounts);
            assertTrue("Should have at least CEO and new students", allAccounts.size() >= 3);
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_FindAll_IncludesAllTypes() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student = new Student("allstudent@yorku.ca", "password123", "77777777");
            Admin admin = new Admin("alladmin@yorku.ca", "adminpass");
            isolated.write(student);
            isolated.write(admin);
            
            List<Accounts> allAccounts = isolated.findAll();
            assertNotNull("Should return list", allAccounts);
            
            boolean foundStudent = false;
            boolean foundAdmin = false;
            for (Accounts account : allAccounts) {
                if (account.getEmail().equals("allstudent@yorku.ca")) {
                    foundStudent = true;
                    assertEquals("Should be Student", "Student", account.getAccountType());
                }
                if (account.getEmail().equals("alladmin@yorku.ca")) {
                    foundAdmin = true;
                    assertEquals("Should be Admin", "Admin", account.getAccountType());
                }
            }
            assertTrue("Should find student", foundStudent);
            assertTrue("Should find admin", foundAdmin);
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_FindAll_ChiefEventCoordinator() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            List<Accounts> allAccounts = isolated.findAll();
            assertNotNull("Should return list", allAccounts);
            
            boolean foundCEO = false;
            for (Accounts account : allAccounts) {
                if (account instanceof ChiefEventCoordinator) {
                    foundCEO = true;
                    assertEquals("Should be Chief Event Coordinator", "Chief Event Coordinator", account.getAccountType());
                    break;
                }
            }
            assertTrue("Should find CEO", foundCEO);
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Update() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student = new Student("update@yorku.ca", "oldpassword", "88888888");
            isolated.write(student);
            
            UUID studentId = student.getAccountId();
            student.setPassword("newpassword");
            isolated.update(student);
            
            Accounts updated = isolated.find(studentId);
            assertNotNull("Account should still exist", updated);
            assertEquals("Password should be updated", "newpassword", updated.getPassword());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Update_Email() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student = new Student("updateemail@yorku.ca", "password123", "99999999");
            isolated.write(student);
            
            UUID studentId = student.getAccountId();
            student.setEmail("updatedemail@yorku.ca");
            isolated.update(student);
            
            Accounts updated = isolated.find(studentId);
            assertNotNull("Account should still exist", updated);
            assertEquals("Email should be updated", "updatedemail@yorku.ca", updated.getEmail());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Update_OtherAccountsPreserved() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student1 = new Student("update1@yorku.ca", "password123", "10101010");
            Student student2 = new Student("update2@yorku.ca", "password123", "20202020");
            isolated.write(student1);
            isolated.write(student2);
            
            student1.setPassword("newpass1");
            isolated.update(student1);
            
            Accounts updated1 = isolated.findByEmail("update1@yorku.ca");
            Accounts updated2 = isolated.findByEmail("update2@yorku.ca");
            assertNotNull("Student1 should still exist", updated1);
            assertNotNull("Student2 should still exist", updated2);
            assertEquals("Student1 password should be updated", "newpass1", updated1.getPassword());
            assertEquals("Student2 password should remain unchanged", "password123", updated2.getPassword());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_UpdateAccountTypeByEmail() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student = new Student("updatetype@yorku.ca", "password123", "30303030");
            isolated.write(student);
            
            isolated.updateAccountTypeByEmail("updatetype@yorku.ca", "Staff");
            
            Accounts updated = isolated.findByEmail("updatetype@yorku.ca");
            assertNotNull("Account should still exist", updated);
            assertEquals("Account type should be updated", "Staff", updated.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_UpdateAccountTypeByEmail_CaseInsensitive() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student = new Student("updatetypecase@yorku.ca", "password123", "40404040");
            isolated.write(student);
            
            isolated.updateAccountTypeByEmail("UPDATETYPECASE@YORKU.CA", "Faculty");
            
            Accounts updated = isolated.findByEmail("updatetypecase@yorku.ca");
            assertNotNull("Account should still exist", updated);
            assertEquals("Account type should be updated", "Faculty", updated.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_UpdateAccountTypeByEmail_OtherAccountsPreserved() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Student student1 = new Student("updatetype1@yorku.ca", "password123", "50505050");
            Student student2 = new Student("updatetype2@yorku.ca", "password123", "60606060");
            isolated.write(student1);
            isolated.write(student2);
            
            isolated.updateAccountTypeByEmail("updatetype1@yorku.ca", "External Partner");
            
            Accounts updated1 = isolated.findByEmail("updatetype1@yorku.ca");
            Accounts updated2 = isolated.findByEmail("updatetype2@yorku.ca");
            assertNotNull("Student1 should still exist", updated1);
            assertNotNull("Student2 should still exist", updated2);
            assertEquals("Student1 type should be updated", "External Partner", updated1.getAccountType());
            assertEquals("Student2 type should remain unchanged", "Student", updated2.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_WriteAccountRecord_User() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Staff staff = new Staff("writeaccount@yorku.ca", "password123", "STAFF001");
            isolated.write(staff);
            
            Accounts found = isolated.findByEmail("writeaccount@yorku.ca");
            assertNotNull("Staff should be written", found);
            assertEquals("Email should match", "writeaccount@yorku.ca", found.getEmail());
            assertEquals("Account type should be Staff", "Staff", found.getAccountType());
            if (found instanceof User) {
                assertEquals("Org ID should match", "STAFF001", ((User) found).getOrgID());
            }
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_WriteAccountRecord_NonUser() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Admin admin = new Admin("writeadmin@yorku.ca", "adminpass");
            isolated.write(admin);
            
            Accounts found = isolated.findByEmail("writeadmin@yorku.ca");
            assertNotNull("Admin should be written", found);
            assertEquals("Email should match", "writeadmin@yorku.ca", found.getEmail());
            assertFalse("Admin should not be User", found instanceof User);
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Find_Faculty() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Faculty faculty = new Faculty("findfaculty@yorku.ca", "facultypass", "FAC001");
            isolated.write(faculty);
            
            UUID facultyId = faculty.getAccountId();
            Accounts found = isolated.find(facultyId);
            assertNotNull("Should find faculty by ID", found);
            assertEquals("Email should match", "findfaculty@yorku.ca", found.getEmail());
            assertEquals("Account type should be Faculty", "Faculty", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_FindByEmail_Faculty() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Faculty faculty = new Faculty("findbyfaculty@yorku.ca", "facultypass", "FAC002");
            isolated.write(faculty);
            
            Accounts found = isolated.findByEmail("findbyfaculty@yorku.ca");
            assertNotNull("Should find faculty by email", found);
            assertEquals("Email should match", "findbyfaculty@yorku.ca", found.getEmail());
            assertEquals("Account type should be Faculty", "Faculty", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Find_Staff() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Staff staff = new Staff("findstaff@yorku.ca", "staffpass", "STAFF002");
            isolated.write(staff);
            
            UUID staffId = staff.getAccountId();
            Accounts found = isolated.find(staffId);
            assertNotNull("Should find staff by ID", found);
            assertEquals("Email should match", "findstaff@yorku.ca", found.getEmail());
            assertEquals("Account type should be Staff", "Staff", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_FindByEmail_Staff() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            Staff staff = new Staff("findbystaff@yorku.ca", "staffpass", "STAFF003");
            isolated.write(staff);
            
            Accounts found = isolated.findByEmail("findbystaff@yorku.ca");
            assertNotNull("Should find staff by email", found);
            assertEquals("Email should match", "findbystaff@yorku.ca", found.getEmail());
            assertEquals("Account type should be Staff", "Staff", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_Find_ExternalPartner() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            ExternalPartner partner = new ExternalPartner("findpartner@external.com", "partnerpass", "ORG002");
            isolated.write(partner);
            
            UUID partnerId = partner.getAccountId();
            Accounts found = isolated.find(partnerId);
            assertNotNull("Should find external partner by ID", found);
            assertEquals("Email should match", "findpartner@external.com", found.getEmail());
            assertEquals("Account type should be External Partner", "External Partner", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
    
    @Test
    public void testUserCSV_FindByEmail_ExternalPartner() throws Exception {
        UserCSV isolated = createIsolatedUserCSV();
        try {
            ExternalPartner partner = new ExternalPartner("findbypartner@external.com", "partnerpass", "ORG003");
            isolated.write(partner);
            
            Accounts found = isolated.findByEmail("findbypartner@external.com");
            assertNotNull("Should find external partner by email", found);
            assertEquals("Email should match", "findbypartner@external.com", found.getEmail());
            assertEquals("Account type should be External Partner", "External Partner", found.getAccountType());
        } finally {
            cleanupIsolatedUserCSV();
        }
    }
}

