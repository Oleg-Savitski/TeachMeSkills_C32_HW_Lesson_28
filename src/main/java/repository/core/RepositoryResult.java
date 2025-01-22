package repository.core;

public class RepositoryResult {
    private final RepositoryStatus status;
    private final String message;

    private RepositoryResult(RepositoryStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public static RepositoryResult success() {
        return new RepositoryResult(RepositoryStatus.SUCCESS, "Operation completed successfully");
    }

    public static RepositoryResult error(RepositoryStatus status, String message) {
        return new RepositoryResult(status, message);
    }

    public RepositoryStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return status == RepositoryStatus.SUCCESS;
    }
}
