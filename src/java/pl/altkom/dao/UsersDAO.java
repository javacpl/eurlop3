package pl.altkom.dao;

import javax.ejb.Local;
import pl.altkom.model.UserBean;

@Local
public interface UsersDAO {

    public UserBean getUser(String login);
}
