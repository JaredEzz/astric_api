package account;

import astric.model.domain.User;
import astric.server.dao.UserDAOImpl;
import org.junit.jupiter.api.Test;

public class UserDAOImplTest {
    @Test
    public void testWriteToUserTable(){
        UserDAOImpl userDAO = new UserDAOImpl();
        userDAO.writeToUserTable(new User("jared", "hasson", "@jaredezz", "imageurl", "jaredhasson"), "passHash");

    }
}
