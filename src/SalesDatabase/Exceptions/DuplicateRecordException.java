package SalesDatabase.Exceptions;


/**
 * DuplicateRecordException is an exception class which is thrown when the duplicate
 * records are found in the files.
 * @author Vinayak Sareen
 * @see Exception
 * */

public class DuplicateRecordException extends Exception {
    public DuplicateRecordException() {
        super("Duplicate record found exception");
    }

    public DuplicateRecordException(String message) {
        super(message);
    }
}