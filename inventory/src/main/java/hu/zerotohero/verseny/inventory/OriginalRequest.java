package hu.zerotohero.verseny.inventory;

public class OriginalRequest {
    public static final String STORAGE_FILENAME = "original_requests.csv";
    public String employeeId;
    public String itemId;

    public static OriginalRequest fromCsv(String employeeId, String itemId) {
        OriginalRequest originalRequest = new OriginalRequest();
        originalRequest.employeeId = employeeId;
        originalRequest.itemId = itemId;
        return originalRequest;
    }
}
