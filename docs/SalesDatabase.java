package SalesDatabase;

import SalesDatabase.Exceptions.EmptyFolderException;
import SalesDatabase.Exceptions.InvalidFileException;
import SalesDatabase.Models.Sales;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

/**
 * SalesDatabase provides the functionality to manipulate and test the file
 * system provided in the Data directory
 * @author Vinayak Sareen
 * @version 1.0
 * @see Sales
*/

public class SalesDatabase {
    private final String basePath;
    private static final ArrayList<String> logs = new ArrayList<>();
    static Sales[] salesArr;



    public SalesDatabase() {
        this.basePath = System.getenv("PWD");
    }

    public static void main(String[] args) {
        SalesDatabase db = new SalesDatabase();
        Scanner snc = new Scanner(System.in);
        boolean isExit = true;
        while (isExit) {
            try {
                db.showOperationMenu(snc);
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
        }
        snc.close();
    }


    public static boolean noInnerFolder(String basePath) {
        File file = new File(basePath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f: files) {
                if(!f.isFile()) {
                    return false;
                }
            }
        }
        return true;
    }

    static void listFiles(String path, String relativePath) {
        String basePath = path + relativePath;
        System.out.println(basePath);
        File file = new File(basePath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f: files) {
                logs.add("file: " +basePath + "/" + f.getName());
            }
        }
    }


    public static void directories(String basePath) {

        if(noInnerFolder(basePath)) {
            String[] paths = basePath.split("/");
            String parentPath = paths[paths.length - 1];
            String rop = basePath.replace(parentPath, "");
            listFiles(rop, parentPath);
            return;
        }

        File file = new File(basePath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f: files) {
                if(f.isFile()) {
                    String[] folderPath = basePath.split("/");
                    String baseFolder = folderPath[folderPath.length - 1];
                    logs.add("file: " + baseFolder + "/" + f.getName());
                } else {
                    if (!f.getName().equals(".idea")) {
                        logs.add("Directory: " + basePath + "/" +  f.getName() + "/");
                        directories(basePath + "/" + f.getName());
                    }
                }
            }
        }
    }

    public void writeLog(String outputPath) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(outputPath);
            PrintWriter writer = new PrintWriter(fos);
            for (String log: logs) {
                if (log.contains("file:")) {
                    writer.println("\t" +log);
                    writer.flush(); // this method is required to because if automatic flushing is enabled
                    // flush would work.
                } else {
                    writer.println(log);
                    writer.flush();
                }
            }
            fos.close();
        } catch (IOException ioException) {
            System.out.println("Exception opening/closing the output " +
                    "stream ");
        }
        catch (Exception e) {
            System.out.println("Generic Exception found!" + e.getMessage());
        }
    }

    public ArrayList<String> listFiles(String basePath) {
        ArrayList<String> result = new ArrayList<>();
        for (String log: logs) {
            if (log.contains("file:")) {
                String fileName = log.replace("file:", "");
                result.add(fileName);
            }
        }
        return result;
    }


    public static void checkEmptyFolder(String path) throws EmptyFolderException {
        File file = new File(path);
        boolean isEmpty = true;
        File[] files = file.listFiles();
        if (files == null) { throw new EmptyFolderException(); }
        for (File f : files) {
            if (f.isFile()) {
                isEmpty = false;
                break;
            }
        }
        if (isEmpty) { throw new EmptyFolderException();}
    }

    // invalid file exception.
    public static void validateLogFile(String path) throws InvalidFileException,
            IOException {
        // check if the folder is empty.
        try {
            FileInputStream inputStream = new FileInputStream(path.substring(1));
            inputStream.close();
        } catch (FileNotFoundException notFoundException) {
            System.out.println("Not found exception "
                    + notFoundException.getMessage());
            String[] details = path.split("/");
            String file = details[details.length - 1];
            throw new InvalidFileException(file);
        } catch (Exception e) {
            System.out.println("Another Exception " + e.getMessage());
        }
    }

    public ArrayList<String> listFiles() {
        String logFilePath = basePath + "/log.txt";
        String dataFolderPath = basePath + "/src/SalesDatabase/Data";
        directories(dataFolderPath);
        writeLog(logFilePath);
        ArrayList<String> files = listFiles(this.basePath);
        printFiles(files);
        return files;
    }

    // not sure about this part ....! - We can read and write the objects.
    public void addRecord(Sales obj) throws IOException {
        String record = obj.country + "\t" + obj.item_type + "\t" + obj.order_priority +
                "\t" + obj.order_date + "\t" + obj.order_ID + "\t" + obj.ship_date +
                "\t" + obj.units_sold + "\t" + obj.unit_price + "\t" + obj.unit_cost +
                "\t" + obj.revenue + "\t" + obj.total_cost + "\t" + obj.total_profit;
        // needs to be made generic

        FileOutputStream outStream = new FileOutputStream( basePath + "/src/SalesDatabase/Data/1/New.txt");
        PrintWriter printWriter = new PrintWriter(outStream);
        printWriter.println(record);
        printWriter.flush();
        outStream.close();
    }

    // utility function to save the records.
    public void saveRecord(String[] records) {
        Scanner snc = new Scanner(records[2]);
        while (snc.hasNext()) {
            System.out.println("Word is " + snc.next());
        }
    }


    // MUST Use the buffered reader. - done.
    public void displayFileContents(FileInputStream instream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(instream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String currentLine = "";
        StringBuilder buffer = new StringBuilder();
        while ((currentLine = bufferedReader.readLine()) != null) {
            buffer.append(currentLine);
            buffer.append("\n");
        }
        String value = buffer.toString();
        String[] records = value.split("\n");
        for(String record: records) {
            System.out.println(record);
        }
    }


    // BinarySalesSearch method - done.
    // must keep track of no of iterations required to search.
    public boolean binarySalesSearch(long order_ID) {
        // this method is great but requires the array/seq to be in the sorted fashion.
        Arrays.sort(salesArr);
        int cntIterations = 0;
        int low = 0;
        int high = salesArr.length - 1;
        int middle;
        while (low <= high) {
            cntIterations += 1;
            middle = low + (high - low) / 2;
            if (middle == order_ID) {
                System.out.println("Found the element in " + cntIterations + " iterations");
                return true;
            } else if(order_ID > middle) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        System.out.println("Not Found Element Count iterations was " + cntIterations);
        return false;
    }

    // SequentialSaleSearch
    // must keep track of no of operations required to search.
    public boolean sequentialSaleSearch(long order_ID) {
        boolean isFound = false;
        int operationCount = 0;
        for (Sales sale: salesArr) {
            operationCount++;
            if (sale.order_ID == order_ID) {
                isFound = true;
                break;
            }
        }
        if(isFound) {
            System.out.println("Found the sale within " + operationCount + " iterations.");
        } else {
            System.out.println("Item not found and used " + operationCount + " iterations.");
        }
        return isFound;
    }

    private void printMenuOptions() {
        System.out.println("Welcome to the database, ..... 💻");
        System.out.println("Please select from the following options.");
        System.out.println("1. List Files");
        System.out.println("2. Process Files");
        System.out.println("3. Exit");
    }

    public void showOperationMenu(Scanner snc) throws IOException {
        printMenuOptions();
        // develop the exception here
        int selectOption = snc.nextInt();
        switch (selectOption) {
            case 1:
                ArrayList<String> files = listFiles();
                try {
                    System.out.println("the src is "  + files);
                    for(String file: files)
                        validateLogFile(file);
                } catch (InvalidFileException fne) {
                    System.out.println(fne.getMessage());
                } catch (IOException ioException) {
                    System.out.println(ioException.getMessage());
                }
                break;

            case 2:
                System.out.println("processing files ....");
                Sales t = new Sales("China",	"Baby_Food",	'H',	new Date("13/07/2014"),
                        888084399,	new Date("21/08/2014"),	2764,	(float) 255.28,
                        (float) 159.42,	705593.92,	440636.88,	264957.04);
                addRecord(t);
                //               try {
                //                   FileInputStream inputStream = new FileInputStream("/Users/databunker/IdeaProjects/AssignmentFS/src/SalesDatabase/Data/1/Tom.txt");
                //                   displayFileContents(inputStream);
                //                   inputStream.close();
                //               } catch (IOException e) {
                //                   e.printStackTrace();
                //               }
                break;

            case 3:
                System.out.println("Terminating program: Thanks for using the system");
                System.exit(1);

            default:
                System.out.println("Invalid option was selected! Please try again ");
                showOperationMenu(snc);
                break;
        }
    }



    private void printFiles(ArrayList<String> files) {
        for (String file: files) {
            String[] details = file.split("/");
            System.out.println("FileName: " + details[details.length - 1]);
            System.out.println("Path: " + file);
        }
    }
}
