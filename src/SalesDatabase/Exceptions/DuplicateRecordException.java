package SalesDatabase.Exceptions;


public class DuplicateRecordException extends Exception {
    public DuplicateRecordException() {
        super("Duplicate record found exception");
    }

    public DuplicateRecordException(String message) {
        super(message);
    }
}