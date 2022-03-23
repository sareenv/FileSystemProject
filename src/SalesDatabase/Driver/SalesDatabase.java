package SalesDatabase.Driver;

import SalesDatabase.Exceptions.EmptyFolderException;
import SalesDatabase.Exceptions.InvalidFileException;
import SalesDatabase.Models.Sales;
import SalesDatabase.Models.SearchResult;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * SalesDatabase class is  responsible for managing the file system interaction and manipulation.
 *
 * @author Vinayak Sareen
 * @see Sales
 * @see InvalidFileException
 * */

public class SalesDatabase {
    private final String basePath;
    static ArrayList<String> logs = new ArrayList<>();
    static final int salesSize = 40;
    static int lastSalesObject = 0;
    static Sales[] salesArr = new Sales[salesSize];

    public SalesDatabase() {
        this.basePath = System.getenv("PWD");
    }

    public static void main(String[] args) {
        SalesDatabase db = new SalesDatabase();
        Scanner snc = new Scanner(System.in);
        boolean isExit = true;
        while (isExit) { db.showOperationMenu(snc); }
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
        //  base case
        //  no more folders are present.
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

    /**
     * Returns the list to images from the directories from the Data folder.
     * @return ArrayList<String>
     * */
    public ArrayList<String> listFiles() {
        String logFilePath = basePath + "/log.txt";
        String dataFolderPath = basePath + "/src/SalesDatabase/Data";
        directories(dataFolderPath);
        writeLog(logFilePath);
        ArrayList<String> files = listFiles(this.basePath);
        printFiles(files);
        return files;
    }
    /**
     * Adds the sales object to the salesArr.
     * @see Sales
     * @param obj passed the sales object to be added to the array.
     * */
    public void addRecord(Sales obj) {
        if (lastSalesObject == salesArr.length - 1) {
            System.out.println("Sorry the limit of array ");
        } else {
            salesArr[lastSalesObject] = obj;
            lastSalesObject++;
        }
    }
    public void displayFileContents(FileInputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String currentLine = "";
        StringBuilder buffer = new StringBuilder();
        while ((currentLine = bufferedReader.readLine()) != null) {
            buffer.append(currentLine);
            buffer.append("\n");
        }
        String value = buffer.toString();
        String[] records = value.split("\n");

        Scanner snc = new Scanner(records[2]);
        while (snc.hasNext()) {
            System.out.println("Word is " + snc.next());
        }

    }
    /**
     * Performs the optimal search operation for the searching in the sorted sales array.
     * The operations are performed in O(log n).
     * @see Sales
     * @see SearchResult
     * @param orderID order id describes the particular order.
     * @return SearchResult
     * */
    public SearchResult binarySalesSearch(long orderID) {
        Arrays.sort(salesArr);
        int low = 0;
        int high = salesArr.length - 1;
        int opCnt = 0;
        while (low <= high) {
            int middle = low + (high - low) / 2;
            opCnt++;
            if (salesArr[middle].order_ID == orderID) {
                return new SearchResult(true, opCnt);
            } else if(orderID > salesArr[middle].order_ID) {
                low = middle + 1;
                opCnt++;
            } else {
                high = middle - 1;
                opCnt++;
            }
        }
        return new SearchResult(false, opCnt);
    }
    /**
     * search for the sales record in the file system with order id.
     * @param order_ID is the order id of the sale item.
     * @return searchResult the search result which contain the count of operations to search the record
     *  and if the item is found.
     * */
    public SearchResult sequentialSaleSearch(long order_ID) {
        boolean isFound = false;
        int operationCount = 0;
        for (Sales sale: salesArr) {
            operationCount++;
            if (sale.order_ID == order_ID) {
                isFound = true;
                break;
            }
        }
        return new SearchResult(isFound, operationCount);
    }
    private void printMenuOptions() {
        System.out.println("Welcome to the database, ..... 💻");
        System.out.println("Please select from the following options.");
        System.out.println("1. List Files");
        System.out.println("2. Process Files");
        System.out.println("3. Exit");
    }
    public void showOperationMenu(Scanner snc) {
        printMenuOptions();
        // develop the exception here
        int selectOption = snc.nextInt();
        switch (selectOption) {
            case 1:
                ArrayList<String> files = listFiles();
                try {
                    for(String file: files)
                        validateLogFile(file);
                } catch (InvalidFileException | IOException fne) {
                    System.out.println(fne.getMessage());
                }
                break;

            case 2:
                try {
                    FileInputStream inputStream = new FileInputStream("/Users/databunker/IdeaProjects/AssignmentFS/src/SalesDatabase/Data/1/Tom.txt");
                    displayFileContents(inputStream);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

