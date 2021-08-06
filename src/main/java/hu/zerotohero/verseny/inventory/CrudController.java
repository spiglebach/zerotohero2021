package hu.zerotohero.verseny.inventory;

import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/inventory")
public class CrudController {
    @PostMapping
    public String create(@RequestBody PostInventoryRequest request) throws Exception {
        Inventory newInventory = new Inventory();
        newInventory.id = String.valueOf(System.nanoTime());
        newInventory.employeeId = request.employeeId;
        newInventory.itemId = request.itemId;
        Instant timestampInstant = Instant.ofEpochSecond(request.epochSecond);
        newInventory.timestamp = LocalDateTime.ofInstant(timestampInstant, ZoneId.systemDefault());

        File file = new File("inventory.csv");
        boolean newFile = !file.exists();
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            if (newFile) {
                fileWriter.write(Inventory.HEADER);
            }
            fileWriter.write(newInventory.toCsv());
            fileWriter.write("\n");
        }
        return newInventory.id;
    }

    @GetMapping("{id}")
    public Inventory getInventoryById(@PathVariable String id) throws IOException { // todo generalize file reading
        File file = new File("inventory.csv");
        try {
            FileReader fileReader = new FileReader(file);
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                String header = bufferedReader.readLine();
                String line = bufferedReader.readLine();
                while (line != null) {
                    String[] properties = line.split(";");
                    if (id.equals(properties[0])) {
                        return Inventory.fromCsv(
                                properties[0],
                                properties[1],
                                properties[2],
                                properties[3],
                                properties[4],
                                properties[5]);
                    }
                    line = bufferedReader.readLine();
                }
            }
        } catch (FileNotFoundException e) {
            return null;
        }
        return null;
    }

    @GetMapping
    public List<Inventory> getInventories() throws IOException { // todo generalize file reading
        File file = new File("inventory.csv");
        try {
            FileReader fileReader = new FileReader(file);
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                String header = bufferedReader.readLine();
                List<Inventory> inventories = new ArrayList<>();
                String line = bufferedReader.readLine();
                while (line != null) {
                    String[] properties = line.split(";");
                    Inventory inventory = Inventory.fromCsv(
                            properties[0],
                            properties[1],
                            properties[2],
                            properties[3],
                            properties[4],
                            properties[5]);
                    inventories.add(inventory);
                    line = bufferedReader.readLine();
                }
                return inventories;
            }
        } catch (FileNotFoundException e) {
            return Collections.emptyList();
        }
    }
}
class PostInventoryRequest {
    public String employeeId;
    public String itemId;
    public long epochSecond;
}

class Inventory {
    public static final String HEADER = "ID;EMPLOYEE_ID;ITEM_ID;ORIGINAL_OWNER_ID;INVESTIGATION;TIMESTAMP\n";
    public String id;
    public String employeeId;
    public String itemId;
    public String originalOwnerId;
    public boolean investigation;
    public LocalDateTime timestamp;

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
        inventory.investigation = "REQUIRED".equals(investigation);
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
        csvRow.append(investigation ? "REQUIRED" : "NOT REQUIRED");
        csvRow.append(";");
        if (timestamp != null) csvRow.append(timestamp.toString());
        return csvRow.toString();
    }
}
