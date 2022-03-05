package Main;

import Main.Exceptions.EmptyFolderException;
import Main.Exceptions.InvalidFileException;
import Main.Models.Sales;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class SalesDatabase {
    private final String basePath;
    static ArrayList<String> logs = new ArrayList<>();
    static Sales[] salesArr;

    // constructor.
    public SalesDatabase() {
        this.basePath = System.getenv("PWD");
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
        String dataFolderPath = basePath + "/src/Main/Data";
        directories(dataFolderPath);
        writeLog(logFilePath);
        ArrayList<String> files = listFiles(this.basePath);
        printFiles(files);
        return files;
    }

    public void addRecord(Sales obj) {

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

        }
        Scanner snc = new Scanner(records[2]);
        while (snc.hasNext()) {
            System.out.println("Word is " + snc.next());
        }

    }


    // BinarySalesSearch
    // must keep track of no of iterations required to search.
    public void binarySalesSearch(long order_ID) {
        // this method is great but requires the array/seq to be in the sorted fashion.

    }

    // SequentialSaleSearch
    // must keep track of no of operations required to search.
    public void sequentialSaleSearch(long order_ID) {
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
               try {
                   FileInputStream inputStream = new FileInputStream("/Users/databunker/IdeaProjects/AssignmentFS/src/Main/Data/1/Tom.txt");
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

public class Main {

    public static void main(String[] args) {
        SalesDatabase db = new SalesDatabase();
        Scanner snc = new Scanner(System.in);
        boolean isExit = true;
        while (isExit) { db.showOperationMenu(snc); }
        snc.close();
    }
}
