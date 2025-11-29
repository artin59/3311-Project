package Backend;

import org.junit.Test;
import static org.junit.Assert.*;


public class StudentTest {

    @Test
    public void testCreateStudent() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        assertNotNull(student);
        assertEquals("student@my.yorku.ca", student.getEmail());
        assertEquals("123456789", student.getOrgID());
    }


}

