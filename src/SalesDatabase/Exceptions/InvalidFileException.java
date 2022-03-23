package SalesDatabase.Exceptions;


/**
 * InvalidFileException class is the custom exception class which is thrown
 * file invalid file type is provided to be processed
 * @author Vinayak Sareen
 * @see Exception
 * */

public class InvalidFileException extends Exception {

    public InvalidFileException(String file, String errorMessage) throws Exception {
        super("Error Input File named: " + file + " cannot be found " +
                "\n Details: " + errorMessage);
    }

    public InvalidFileException(String file) {
        super("Error Input File named: " + file + " cannot be found " +
                "\nDetails: Cannot locate the file path");
    }
}
