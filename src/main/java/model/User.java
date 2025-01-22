package model;

public class User {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String country;
    private String city;

    public User(String username, String email, String password,
                String fullName, String country, String city) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.country = country;
        this.city = city;
    }

    public User() {}

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getCountry() { return country; }
    public String getCity() { return city; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setCountry(String country) { this.country = country; }
    public void setCity(String city) { this.city = city; }
}