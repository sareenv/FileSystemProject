package SalesDatabase.Exceptions;


/**
 * Empty folder exception is the class which is thrown when the reading of file
 * is performed from the empty folder.
 * @author Vinayak Sareen
 * @see RuntimeException
 * */

public class EmptyFolderException extends RuntimeException{

    public EmptyFolderException() {
        super("No files can be found in the requested directory");
    }

    public EmptyFolderException(String message) {
        super(message);
    }
}
