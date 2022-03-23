package SalesDatabase.Driver;

import SalesDatabase.Exceptions.DuplicateRecordException;
import SalesDatabase.Exceptions.EmptyFolderException;
import SalesDatabase.Exceptions.InvalidFileException;
import SalesDatabase.Models.Sales;
import SalesDatabase.Models.SearchResult;

import java.io.*;
import java.util.*;

/**
 * SalesDatabase class is  responsible for managing the file system interaction and manipulation.
 *
 * @author Vinayak Sareen
 * @see Sales
 * @see InvalidFileException
 * */

public class SalesDatabase {
    private final String basePath;
    static final int salesSize = 40;
    static int lastSalesObject = -1;
    static Sales[] salesArr = new Sales[salesSize];
    public SalesDatabase() {
        this.basePath = System.getenv("PWD");
    }
    static ArrayList<String> logs = new ArrayList<>();



    /**
     * Method lists all the files in the current directory and throws an exception
     * when empty folder is passed
     * @param basePath basePath of the project for reading the file
     * @param paths result paths of files and directories.
     * @see EmptyFolderException
     * */

    public static void listFiles(String basePath, ArrayList<String> paths)
            throws EmptyFolderException, InvalidFileException {

        File file = new File(basePath);
        File[] files = file.listFiles();

        if (files != null && files.length == 0) {
            throw new EmptyFolderException();
        }

        if (files != null) {
            for (File f1 : files) {
                String fileName = f1.getName();
                if (fileName.equals(".DS_Store")) { continue; }

                if (f1.isFile() && !fileName.contains(".txt")) {
                    System.out.println("Invalid/Unsupported File: " + fileName);
                }

                if (file.isDirectory()) {
                    String pathName = file.getAbsolutePath();
                    pathName = pathName + "/" + fileName;
                    paths.add(pathName);
                    try {
                        listFiles(pathName, paths);
                    } catch (Exception exception) {
                        System.out.println("Exception: " + exception.getMessage());
                    }
                }
            }
        }
    }


    public void writeLog(String outputPath) {
        try {
            FileWriter fileWriter = new FileWriter(outputPath, false);
            PrintWriter writer = new PrintWriter(fileWriter);
            writer.println("");
            writer.flush();
            for (String log: logs) {
                if (log.contains(".txt")) {
                    writer.println("\t" +log);
                    writer.flush(); // this method is required to because if automatic
                    // flushing is enabled flush would work.
                } else {
                    writer.println(log);
                    writer.flush();
                }
            }
        } catch (IOException ioException) {
            System.out.println("Exception opening/closing the output " +
                    "stream ");
        }
        catch (Exception e) {
            System.out.println("Generic Exception found!" + e.getMessage());
        }
    }

    public ArrayList<String> listFilesAndDirectories() throws InvalidFileException {
        String path = basePath + "/src/SalesDatabase/Data";
        ArrayList<String> result = new ArrayList<>();
        listFiles(path, result);
        return result;
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
            lastSalesObject++;
            salesArr[lastSalesObject] = obj;
        }
    }

    private boolean checkDuplicateSales(Sales[] sales, Sales oSale) {
        ArrayList<Sales> countMap = new ArrayList<>(Arrays.asList(sales));
        return countMap.contains(oSale);
    }

    public void writeToDatabaseFile(String basePath, String record) throws IOException {
        FileWriter fileWriter = new FileWriter(basePath, true);
        PrintWriter writer = new PrintWriter(fileWriter);
        writer.println(record);
        writer.flush();
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

        ArrayList<ArrayList<String>> content = new ArrayList<>();

        for (String record: records) {
            Scanner snc = new Scanner(record);
            ArrayList<String> instanceRecord = new ArrayList<>();
            while (snc.hasNext()) {
                String data = snc.next();
                data = data.replace(" ", "");
                instanceRecord.add(data);
            }
            content.add(instanceRecord);
            snc.close();
        }


        for (ArrayList<String> record: content) {
            try {
                Sales sale = new Sales(record.get(0), record.get(1), record.get(2).length() > 0 ? record.get(2).charAt(0) :
                        'N', new Date(record.get(3)), Long.parseLong(record.get(4)), new Date(record.get(5)),
                        Integer.parseInt(record.get(6)), Float.parseFloat(record.get(7)), Float.parseFloat(record.get(8)),
                        Double.parseDouble(record.get(9)), Double.parseDouble(record.get(10)),
                        Double.parseDouble(record.get(11)));

                if (checkDuplicateSales(salesArr, sale)) {
                    System.out.println("DUPLICATE RECORD:: " + sale);
                    throw new DuplicateRecordException();
                } else {
                    System.out.println(sale);
                    addRecord(sale);
                }
            } catch (DuplicateRecordException duplicationException) {
                System.out.println("Duplication Exception: " + duplicationException.getMessage());
            } catch (Exception exception) {
                System.out.println("Exception saving the sales object, check your input data \n Message: "
                        + exception.getMessage());
            }
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

        if (lastSalesObject == 0) { return new SearchResult(false, 0);}
        Arrays.sort(salesArr, new Comparator<Sales>() {
            @Override
            public int compare(Sales s1, Sales s2) {
                if (s1 == s2) {
                    return 0;
                } else if (s1 == null) {
                    return 1;
                } else if (s2 == null) {
                    return -1;
                } else {
                    return s1.compareTo(s2);
                }

            }
        });

        int low = 0;
        int high = lastSalesObject; // only until this point the array is filled.
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
            if (sale == null) {
                continue;
            }
            operationCount++;
            if (sale.order_ID == order_ID) {
                isFound = true;
                break;
            }
        }
        return new SearchResult(isFound, operationCount);
    }

    private void printLogs() {
        for (String log: logs) {
            if (log.contains(".txt")) {
                System.out.println("File: " + log);
            } else {
                System.out.println("Directory: " + log);
            }
        }
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
                try {
                    ArrayList<String> result = listFilesAndDirectories();
                    String outputPath = basePath + "/output.txt/";
                    logs.addAll(result);
                    printLogs();
                    writeLog(outputPath);
                } catch (InvalidFileException e) {
                    System.out.println("Invalid File Exception " + e.getMessage());
                }
                break;

            case 2:
                boolean showStatus = true;
                int selectedOption = 0;
                while (showStatus) {
                    System.out.println("1. Add Record and Display Contents");
                    System.out.println("2. Perform Binary Search");
                    System.out.println("3. Perform Sequential Search");
                    try {
                        selectedOption = snc.nextInt();
                        if (selectedOption < 0) {
                            System.out.println("Invalid option select, try again ");
                        } else if (selectedOption > 5) {
                            System.out.println("Invalid option select, try again ");
                        } else {
                            showStatus = false;
                        }
                    } catch (InputMismatchException exception) {
                        System.out.println("Invalid input type received");
                    }
                }

                if (selectedOption == 1) {
                    displayAllFiles();
                    // write the content to the output database
                    String outputFilePath = basePath + "/output.txt";
                    try  {
                        PrintWriter writer = new PrintWriter(outputFilePath);
                        writer.println("");
                    } catch (Exception e) {
                        System.out.println("Error clearing the contents" +
                                " before running Details: " + e.getMessage());
                    }

                    for (int i = 0; i< lastSalesObject; i++) {
                        Sales sale = salesArr[i];
                        try {
                            writeToDatabaseFile(outputFilePath, sale.toString());
                        } catch (IOException ioException) {
                            System.out.println("ioException has occurred");
                        }
                    }
                } else if (selectedOption == 2) {
                    System.out.println("Performing binary search ");
                    System.out.println("Please enter the order id");
                    long orderId = snc.nextLong();
                    SearchResult result = binarySalesSearch(orderId);
                    System.out.println("Found Element: " + result.isFound());
                    System.out.println("Comparison Required: " + result.getRecordCount());
                } else {
                    System.out.println("Performing sequential search ");
                    System.out.println("Please enter the order id");
                    long orderId = snc.nextLong();
                    SearchResult result = sequentialSaleSearch(orderId);
                    System.out.println("Found Element: " + result.isFound());
                    System.out.println("Comparison Required: " + result.getRecordCount());
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

    private ArrayList<String> filterPaths() {
        ArrayList<String> solution = new ArrayList<>();
        for (String path: logs) {
            if (path.contains(".txt")) {
                solution.add(path);
            }
        }
        return solution;
    }

    private void displayAllFiles() {
        // here we need path to all the files
        ArrayList<String> allFiles = filterPaths();
        for (String filePath: allFiles) {
            try {
                filePath = filePath.replace(" ", "");
                if(!filePath.contains(".txt")) { throw new InvalidFileException("Cannot read non-txt files");}
                FileInputStream inputStream = new FileInputStream(filePath);
                displayFileContents(inputStream);
                inputStream.close();
            } catch (InvalidFileException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }
    }



    public static void main(String[] args) {
        SalesDatabase db = new SalesDatabase();
        Scanner snc = new Scanner(System.in);
        boolean isExit = true;
        while (isExit) { db.showOperationMenu(snc); }
        snc.close();
    }
}

