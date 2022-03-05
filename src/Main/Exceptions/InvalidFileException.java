package Main.Exceptions;

/*
*
* @author: Vinayak Sareen.
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
