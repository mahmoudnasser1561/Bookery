package code.demo.core;

import code.demo.model.Role;

public class UserSession {
    private static UserSession instance;

    private String username;
    private Role role;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public void login(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    public void logout() {
        this.username = null;
        this.role = null;
    }

    public boolean isLoggedIn() { return username != null; }

    public String getUsername() { return username; }

    public Role getRole() { return role; }
}
