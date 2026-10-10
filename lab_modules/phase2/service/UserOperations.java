package service;

public interface UserOperations {

    boolean validateUser(String username, String password);

    boolean login(String username, String password);

}
