package repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface IFileBasedRepository<T, ID> {
    void createDirectoryIfNotExists();
    Path getFilePath(ID identifier);
    void save(ID identifier, List<T> items);
    Optional<List<T>> load(ID identifier);
}