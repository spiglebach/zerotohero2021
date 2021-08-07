package hu.zerotohero.verseny.inventory;

public class PostInventoryRequest {
    public String employeeId;
    public String itemId;
    public long epochSecond;

    public PostInventoryRequest(String employeeId, String itemId, long epochSecond) {
        this.employeeId = employeeId;
        this.itemId = itemId;
        this.epochSecond = epochSecond;
    }
}