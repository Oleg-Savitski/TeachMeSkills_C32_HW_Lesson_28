package repository.impl;

import repository.IFileBasedRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static utils.IConstantsPath.GOALS_DIRECTORY;

public class GoalManagerRepository implements IFileBasedRepository<String, String> {
    private static final Logger LOGGER = Logger.getLogger(GoalManagerRepository.class.getName());

    @Override
    public void createDirectoryIfNotExists() {
        try {
            Files.createDirectories(Paths.get(GOALS_DIRECTORY));
        } catch (IOException e) {
            LOGGER.severe("Couldn't create a directory -> " + e.getMessage());
        }
    }

    @Override
    public Path getFilePath(String username) {
        return Paths.get(GOALS_DIRECTORY, username + "_goals.txt");
    }

    @Override
    public void save(String username, List<String> goals) {
        try {
            createDirectoryIfNotExists();
            Path filePath = getFilePath(username);

            Files.write(filePath, goals, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            LOGGER.info("Goals are saved for the user -> " + username);
        } catch (IOException e) {
            LOGGER.severe("Error saving goals -> " + e.getMessage());
        }
    }

    @Override
    public Optional<List<String>> load(String username) {
        Path filePath = getFilePath(username);

        try {
            if (Files.exists(filePath)) {
                return Optional.of(Files.readAllLines(filePath));
            }
            return Optional.empty();
        } catch (IOException e) {
            LOGGER.severe("Error loading goals -> " + e.getMessage());
            return Optional.empty();
        }
    }

    public void saveGoals(String username, List<String> goals) {
        save(username, goals);
    }

    public List<String> loadGoals(String username) {
        return load(username).orElse(new ArrayList<>());
    }
}