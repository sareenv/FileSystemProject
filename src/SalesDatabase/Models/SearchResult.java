package SalesDatabase.Models;

public class SearchResult {
    private boolean isFound;
    private int recordCount;

    public SearchResult(boolean isFound, int recordCount) {
        this.isFound = isFound;
        this.recordCount = recordCount;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setFound(boolean found) {
        isFound = found;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }
}
