package SalesDatabase.Driver;

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

    static void listFiles(String path, String relativePath) throws EmptyFolderException {
        String basePath = path + relativePath;
        System.out.println(basePath);
        File file = new File(basePath);
        File[] files = file.listFiles();
        if (files != null && files.length == 0) {
            throw new EmptyFolderException("Empty Folder : " + basePath + " contains no " +
                    "files inside it");
        }
        if (files != null) {
            for (File f: files) {
                String fileName = f.getName();
                logs.add("file: " + basePath + "/" + fileName);
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
            try {
                listFiles(rop, parentPath);
            } catch (EmptyFolderException exception) {
                System.out.println("Exception: " + exception.getMessage());
            }

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
        try {
            FileWriter fileWriter = new FileWriter(outputPath, false);
            PrintWriter writer = new PrintWriter(fileWriter);
            writer.println("");
            writer.flush();

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
                } else {
                    System.out.println(sale);
                    addRecord(sale);
                }
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
                listFiles();
                break;

            case 2:
                boolean showStatus = true;
                int selectedOption = 0;
                while (showStatus) {
                    System.out.println("1. Add Record");
                    System.out.println("2. Display File Contents");
                    System.out.println("3. Perform Binary Search");
                    System.out.println("4. Perform Sequential Search");
                    System.out.println("5. Store data to the Output Database.");
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
                        System.out.println("Invalid inout type received");
                    }

                }

                if (selectedOption == 1) {
                    Sales sale1 = new Sales("China","Baby_Food", 'H',
                            new Date("13/07/2014"),	888084399,	new Date("21/08/2014"),
                            2764,	255,	159,	705593.92,	440636.88,
                            264957.04);

                    Sales sale2 = new Sales("China","Clothing", 'L',
                            new Date("13/07/2014"),	988083399,	new Date("21/08/2014"),
                            2764,	255,	159,	705593.92,	440636.88,
                            264957.04);
                    addRecord(sale1);
                    addRecord(sale2);
                } else if (selectedOption == 2) {
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
                } else if (selectedOption == 3) {
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

    private void displayAllFiles() {
        ArrayList<String> allFiles = listFiles();
        for (String filePath: allFiles) {
            try {
                filePath = filePath.replace(" ", "");
                FileInputStream inputStream = new FileInputStream(filePath);
                if(!filePath.contains(".txt")) { throw new Exception("Cannot read non-txt files");}
                displayFileContents(inputStream);
                inputStream.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void printFiles(ArrayList<String> files) {
        for (String file: files) {
            String[] details = file.split("/");
            System.out.println("FileName: " + details[details.length - 1]);
            System.out.println("Path: " + file);
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

