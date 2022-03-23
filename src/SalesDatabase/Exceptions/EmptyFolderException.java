package SalesDatabase.Exceptions;

public class EmptyFolderException extends RuntimeException{

    public EmptyFolderException() {
        super("Not files can be found in the requested directory");
    }

    public EmptyFolderException(String message) {
        super(message);
    }
}
