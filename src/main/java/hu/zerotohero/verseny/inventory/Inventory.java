package hu.zerotohero.verseny.inventory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Inventory {
    public static final String STORAGE_FILENAME = "inventory.csv";
    public static final String TEMP_STORAGE_FILENAME = "inventory_temp.csv";
    public static final String HEADER = "ID;EMPLOYEE_ID;ITEM_ID;ORIGINAL_OWNER_ID;INVESTIGATION;TIMESTAMP\n";
    public String id;
    public String employeeId;
    public String itemId;
    public String originalOwnerId;
    public String investigation;
    public LocalDateTime timestamp; // todo get example format

    public Inventory() {
    }

    public Inventory(PostInventoryRequest request) {
        this.id = String.valueOf(System.nanoTime()); // todo customize id
        this.employeeId = request.employeeId;
        this.itemId = request.itemId;
        Instant timestampInstant = Instant.ofEpochSecond(request.epochSecond);
        this.timestamp = LocalDateTime.ofInstant(timestampInstant, ZoneId.systemDefault());
    }

    public void update(PostInventoryRequest newInventoryData) {
        this.employeeId = newInventoryData.employeeId;
        this.itemId = newInventoryData.itemId;
        Instant timestampInstant = Instant.ofEpochSecond(newInventoryData.epochSecond);
        this.timestamp = LocalDateTime.ofInstant(timestampInstant, ZoneId.systemDefault());
    }

    public static Inventory fromCsv(
            String id,
            String employeeId,
            String itemId,
            String originalOwnerId,
            String investigation,
            String timestamp) {
        Inventory inventory = new Inventory();
        inventory.id = id;
        inventory.employeeId = employeeId;
        inventory.itemId = itemId;
        inventory.originalOwnerId = originalOwnerId;
        inventory.investigation = investigation;
        inventory.timestamp = LocalDateTime.parse(timestamp);
        return inventory;
    }

    public String toCsv() {
        StringBuilder csvRow = new StringBuilder();
        if (id != null) csvRow.append(id);
        csvRow.append(";");
        if (employeeId != null) csvRow.append(employeeId);
        csvRow.append(";");
        if (itemId != null) csvRow.append(itemId);
        csvRow.append(";");
        if (originalOwnerId != null) csvRow.append(originalOwnerId);
        csvRow.append(";");
        if (investigation != null) csvRow.append(investigation);
        csvRow.append(";");
        if (timestamp != null) csvRow.append(timestamp.toString());
        return csvRow.toString();
    }
}
