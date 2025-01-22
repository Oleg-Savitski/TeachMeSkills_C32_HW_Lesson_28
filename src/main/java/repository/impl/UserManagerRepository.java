package repository.impl;

import exceptions.RegistrationError;
import model.User;
import repository.IFileBasedRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.IConstantsPath.EMAIL_PATTERN;
import static utils.IConstantsPath.FILE_NAME;

public class UserManagerRepository implements IFileBasedRepository<User, String> {
    private static final Logger LOGGER = Logger.getLogger(UserManagerRepository.class.getName());
    private static UserManagerRepository instance;
    public final Map<String, User> users = new ConcurrentHashMap<>();

    private UserManagerRepository() {
        loadUsersFromFile();
        printAllUsers();
    }

    public static synchronized UserManagerRepository getInstance() {
        if (instance == null) {
            instance = new UserManagerRepository();
        }
        return instance;
    }

    private boolean isValidEmail(String email) {
        return email != null &&
                !email.trim().isEmpty() &&
                EMAIL_PATTERN.matcher(email).matches();
    }

    private RegistrationError validateUser(String username, String email,
                                           String password, String confirmPassword) {
        synchronized (this) {
            if (users.containsKey(username)) {
                return RegistrationError.USERNAME_EXISTS;
            }
            if (!isValidEmail(email)) {
                return RegistrationError.INVALID_EMAIL;
            }
            if (!password.equals(confirmPassword)) {
                return RegistrationError.PASSWORDS_DO_NOT_MATCH;
            }
            if (password.length() < 8 || password.length() > 30) {
                return RegistrationError.PASSWORD_LENGTH_INVALID;
            }
            return null;
        }
    }

    public synchronized void registerUser(String username, String email, String password,
                                          String confirmPassword, String fullName,
                                          String country, String city) throws IllegalArgumentException {
        synchronized (this) {
            RegistrationError error = validateUser(username, email, password, confirmPassword);

            if (error != null) {
                LOGGER.warning("Registration error -> " + error);
                throw new IllegalArgumentException(switch (error) {
                    case USERNAME_EXISTS -> "Username already exists.";
                    case INVALID_EMAIL -> "Invalid email format.";
                    case PASSWORDS_DO_NOT_MATCH -> "Passwords do not match.";
                    case PASSWORD_LENGTH_INVALID -> "Password must be between 8 and 30 characters.";
                });
            }

            User newUser = new User(username, email, password, fullName, country, city);
            users.put(username, newUser);
            saveUsersToFile();
            LOGGER.info("The user is registered -> " + username);
        }
    }

    @Override
    public void createDirectoryIfNotExists() {
        try {
            File directory = new File(FILE_NAME).getParentFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }
        } catch (Exception _) {
        }
    }

    @Override
    public Path getFilePath(String identifier) {
        return Paths.get(FILE_NAME);
    }

    @Override
    public void save(String username, List<User> usersToSave) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(FILE_NAME, false),
                        StandardCharsets.UTF_8))) {

            for (User user : usersToSave) {
                writer.write("Username: " + user.getUsername());
                writer.newLine();
                writer.write("Email: " + user.getEmail());
                writer.newLine();
                writer.write("Full Name: " + user.getFullName());
                writer.newLine();
                writer.write("Country: " + user.getCountry());
                writer.newLine();
                writer.write("City: " + user.getCity());
                writer.newLine();
                writer.write("Password: " + user.getPassword());
                writer.newLine();
                writer.write("--------------------------------------------------");
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving users!!!", e);
        }
    }

    @Override
    public Optional<List<User>> load(String username) {
        List<User> loadedUsers = new ArrayList<>();

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            LOGGER.warning("File not found -> " + FILE_NAME);
            return Optional.empty();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentUsername = null, email = null, password = null,
                    fullName = null, country = null, city = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username: ")) {
                    currentUsername = line.substring("Username: ".length()).trim();
                } else if (line.startsWith("Email: ")) {
                    email = line.substring("Email: ".length()).trim();
                } else if (line.startsWith("Full Name: ")) {
                    fullName = line.substring("Full Name: ".length()).trim();
                } else if (line.startsWith("Country: ")) {
                    country = line.substring("Country: ".length()).trim();
                } else if (line.startsWith("City: ")) {
                    city = line.substring("City: ".length()).trim();
                } else if (line.startsWith("Password: ")) {
                    password = line.substring("Password: ".length()).trim();
                }

                if (line.trim().equals("--------------------------------------------------")) {
                    if (currentUsername != null && email != null && password != null &&
                            fullName != null && country != null && city != null) {

                        User user = new User(currentUsername, email, password, fullName, country, city);
                        loadedUsers.add(user);
                        currentUsername = email = password = fullName = country = city = null;
                    }
                }
            }

            if (currentUsername != null && email != null && password != null &&
                    fullName != null && country != null && city != null) {
                User user = new User(currentUsername, email, password, fullName, country, city);
                loadedUsers.add(user);
            }

            return Optional.of(loadedUsers);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading users!!!", e);
            return Optional.empty();
        }
    }

    public synchronized void saveUsersToFile() {
        save(null, new ArrayList<>(users.values()));
    }

    public void loadUsersFromFile() {
        Optional<List<User>> loadedUsers = load(null);
        loadedUsers.ifPresent(usersList -> usersList.forEach(user -> users.put(user.getUsername(), user)));
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public void printAllUsers() {
        users.values().forEach(user -> LOGGER.info("User -> " + user.getUsername()));
    }
}