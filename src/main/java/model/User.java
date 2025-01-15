package model;

public class User {
    private final String username;
    private final String email;
    private final String password;
    private final String fullName;
    private final String country;
    private final String city;

    public User(String username, String email, String password, String fullName, String country, String city) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.country = country;
        this.city = city;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }
}