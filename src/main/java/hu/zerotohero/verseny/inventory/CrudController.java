package hu.zerotohero.verseny.inventory;

import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/inventory")
public class CrudController {
    private Map<String, Inventory> cache = new HashMap<>();

    @PostMapping
    public String create(@RequestBody PostInventoryRequest request) throws Exception {
        Inventory newInventory = new Inventory();
        newInventory.id = String.valueOf(System.nanoTime());
        newInventory.employeeId = request.employeeId;
        newInventory.itemId = request.itemId;
        Instant timestampInstant = Instant.ofEpochSecond(request.epochSecond);
        newInventory.timestamp = LocalDateTime.ofInstant(timestampInstant, ZoneId.systemDefault());

        File file = new File(Inventory.STORAGE_FILENAME);
        boolean newFile = !file.exists();
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            if (newFile) {
                fileWriter.write(Inventory.HEADER);
            }
            fileWriter.write(newInventory.toCsv());
            fileWriter.write("\n");
        }

        cache.putIfAbsent(newInventory.id, newInventory);

        return newInventory.id;
    }

    @GetMapping("{id}")
    public Inventory getInventoryById(@PathVariable String id) throws IOException {
        Inventory cachedInventory = cache.get(id);
        if (cachedInventory != null) {
            //System.out.println("returning cached inventory: " + id);
            return cachedInventory;
        }

        //System.out.println("finding inventory in storage: " + id);
        File file = new File(Inventory.STORAGE_FILENAME);
        try {
            FileReader fileReader = new FileReader(file);
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                bufferedReader.readLine(); // header
                String line = bufferedReader.readLine();
                while (line != null) {
                    String[] properties = line.split(";");
                    if (id.equals(properties[0])) {
                        Inventory foundInventory = Inventory.fromCsv(
                                properties[0],
                                properties[1],
                                properties[2],
                                properties[3],
                                properties[4],
                                properties[5]);
                        cache.put(id, foundInventory);
                        return foundInventory;
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
        File file = new File(Inventory.STORAGE_FILENAME);
        try {
            FileReader fileReader = new FileReader(file);
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                bufferedReader.readLine(); // header
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

    @GetMapping("check")
    public void checkInventory() throws IOException {
        List<OriginalRequest> originalRequests = getOriginalRequests();
        File file = new File(Inventory.STORAGE_FILENAME);
        File tempFile = new File(Inventory.TEMP_STORAGE_FILENAME);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        try {
            FileReader fileReader = new FileReader(file);
            try (BufferedReader bufferedReader = new BufferedReader(fileReader);
                 FileWriter tempFileWriter = new FileWriter(tempFile, true)) {
                tempFileWriter.write(bufferedReader.readLine() + "\n"); // header
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
                    Optional<OriginalRequest> originalRequest = originalRequests.stream()
                            .filter(or -> or.itemId.equals(inventory.itemId))
                            .findFirst();
                    boolean employeeDoesNotExist = originalRequests.stream()
                            .noneMatch(or -> or.employeeId.equals(inventory.employeeId));
                    if (!originalRequest.isPresent() || employeeDoesNotExist) {
                        inventory.investigation = "REQUIRED";
                    } else {
                        inventory.investigation = "NOT REQUIRED";
                        String originalOwnerId = originalRequest.get().employeeId;
                        if (!inventory.employeeId.equals(originalOwnerId)) {
                            inventory.originalOwnerId = originalOwnerId;
                        }
                    }

                    tempFileWriter.write(inventory.toCsv());
                    tempFileWriter.write("\n");

                    line = bufferedReader.readLine();
                }
                // todo swap temp file
            }
        } catch (FileNotFoundException e) {
            // If there is no storage file, there is nothing to check
        }

        cache.clear();
    }

    public List<OriginalRequest> getOriginalRequests() {
        try {
            File file = new File(OriginalRequest.STORAGE_FILENAME);
            FileReader fileReader = new FileReader(file);
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                bufferedReader.readLine(); // header
                List<OriginalRequest> originalRequests = new ArrayList<>();
                String line = bufferedReader.readLine();
                while (line != null) {
                    String[] properties = line.split(";");
                    originalRequests.add(OriginalRequest.fromCsv(
                            properties[0],
                            properties[1]));

                    line = bufferedReader.readLine();
                }
                return originalRequests;
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}

class PostInventoryRequest {
    public String employeeId;
    public String itemId;
    public long epochSecond;
}

class OriginalRequest {
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

class Inventory {
    public static final String STORAGE_FILENAME = "inventory.csv";
    public static final String TEMP_STORAGE_FILENAME = "inventory_temp.csv";
    public static final String HEADER = "ID;EMPLOYEE_ID;ITEM_ID;ORIGINAL_OWNER_ID;INVESTIGATION;TIMESTAMP\n";
    public String id;
    public String employeeId;
    public String itemId;
    public String originalOwnerId;
    public String investigation;
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
