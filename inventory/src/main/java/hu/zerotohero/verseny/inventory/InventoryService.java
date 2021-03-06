package hu.zerotohero.verseny.inventory;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    public String getInventoryStorageFileName() {
        return Inventory.STORAGE_FILENAME;
    }

    public Inventory create(PostInventoryRequest request) {
        try {
            Inventory newInventory = new Inventory(request);
            File file = new File(getInventoryStorageFileName());
            boolean newFile = !file.exists();
            try (FileWriter fileWriter = new FileWriter(file, true)) {
                if (newFile) {
                    fileWriter.write(Inventory.HEADER);
                }
                fileWriter.write(newInventory.toCsv() + "\n");
            }
            return newInventory;
        } catch (Exception e) {
            return null;
        }
    }

    public Inventory findById(String id) {
        try {
            FileReader fileReader = new FileReader(getInventoryStorageFileName());
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                bufferedReader.readLine(); // header
                String line = bufferedReader.readLine();
                while (line != null) {
                    if (id.equals(line.split(";")[0])) {
                        return parseInventoryFromLine(line);
                    }
                    line = bufferedReader.readLine();
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public List<Inventory> getInventories() throws IOException {
        try {
            FileReader fileReader = new FileReader(getInventoryStorageFileName());
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                bufferedReader.readLine(); // header
                List<Inventory> inventories = new ArrayList<>();
                String line = bufferedReader.readLine();
                while (line != null) {
                    Inventory inventory = parseInventoryFromLine(line);
                    inventories.add(inventory);
                    line = bufferedReader.readLine();
                }
                return inventories;
            }
        } catch (FileNotFoundException e) {
            return Collections.emptyList();
        }
    }

    private Inventory parseInventoryFromLine(String line) {
        String[] properties = line.split(";");
        return Inventory.fromCsv(
                StringUtils.getValueOrNullIfNullOrBlank(properties[0]),
                StringUtils.getValueOrNullIfNullOrBlank(properties[1]),
                StringUtils.getValueOrNullIfNullOrBlank(properties[2]),
                StringUtils.getValueOrNullIfNullOrBlank(properties[3]),
                StringUtils.getValueOrNullIfNullOrBlank(properties[4]),
                StringUtils.getValueOrNullIfNullOrBlank(properties[5]));
    }

    public List<OriginalRequest> getOriginalRequests() {
            try (BufferedReader originalRequestStorageReader =
                         new BufferedReader(new FileReader(OriginalRequest.STORAGE_FILENAME))) {
                originalRequestStorageReader.readLine(); // read and ignore header
                List<OriginalRequest> originalRequests = new ArrayList<>();
                String line = originalRequestStorageReader.readLine();
                while (line != null) {
                    String[] properties = line.split(";");
                    originalRequests.add(OriginalRequest.fromCsv(
                            StringUtils.getValueOrNullIfNullOrBlank(properties[0]),
                            StringUtils.getValueOrNullIfNullOrBlank(properties[1])));

                    line = originalRequestStorageReader.readLine();
                }
                return originalRequests;
            } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Inventory updateById(String id, PostInventoryRequest newInventoryData) {
        Inventory foundInventory = null;
        List<OriginalRequest> originalRequests = getOriginalRequests();
        File file = new File(getInventoryStorageFileName());
        File tempFile = new File(Inventory.TEMP_STORAGE_FILENAME);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        try (BufferedReader inventoryStorageReader = new BufferedReader(new FileReader(file));
             FileWriter tempFileWriter = new FileWriter(tempFile, true)) {
            tempFileWriter.write(inventoryStorageReader.readLine() + "\n"); // header
            String line = inventoryStorageReader.readLine();
            while (line != null) {
                if (!id.equals(line.split(";")[0])) {
                    tempFileWriter.write(line + "\n");
                    line = inventoryStorageReader.readLine();
                    continue;
                }
                Inventory inventory = parseInventoryFromLine(line);
                inventory.update(newInventoryData);

                try {
                    OriginalRequest originalRequestForItem =
                            checkInventoryAgainstOriginalRequests(inventory, originalRequests);
                    inventory.investigation = Inventory.NOT_REQUIRED;
                    String originalOwnerId = originalRequestForItem.employeeId;
                    if (!inventory.employeeId.equals(originalOwnerId)) {
                        inventory.originalOwnerId = originalOwnerId;
                    } else {
                        inventory.originalOwnerId = null;
                    }
                } catch (InventoryDiscrepancyException e) {
                    inventory.investigation = Inventory.REQUIRED;
                }

                foundInventory = inventory;

                tempFileWriter.write(inventory.toCsv() + "\n");

                line = inventoryStorageReader.readLine();
            }
        } catch (Exception e) {
            tempFile.delete();
            // If there is no storage file, there is nothing to check
        }

        if (file.exists() && tempFile.exists()) {
            file.delete();
            tempFile.renameTo(file);
        }

        return foundInventory;
    }

    public void performInventoryCheck() throws IOException {
        List<OriginalRequest> originalRequests = getOriginalRequests();
        File file = new File(getInventoryStorageFileName());
        File tempFile = new File(Inventory.TEMP_STORAGE_FILENAME);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
             FileWriter tempFileWriter = new FileWriter(tempFile, true)) {
            tempFileWriter.write(bufferedReader.readLine() + "\n"); // header
            String line = bufferedReader.readLine();
            while (line != null) {
                Inventory inventory = parseInventoryFromLine(line);
                try {
                    OriginalRequest originalRequestForItem =
                            checkInventoryAgainstOriginalRequests(inventory, originalRequests);
                    inventory.investigation = Inventory.NOT_REQUIRED;
                    String originalOwnerId = originalRequestForItem.employeeId;
                    if (!inventory.employeeId.equals(originalOwnerId)) {
                        inventory.originalOwnerId = originalOwnerId;
                    }
                } catch (InventoryDiscrepancyException e) {
                    inventory.investigation = Inventory.REQUIRED;
                }

                tempFileWriter.write(inventory.toCsv() + "\n");

                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            // If there is no storage file, there is nothing to check
        }

        if (file.exists() && tempFile.exists()) {
            file.delete();
            tempFile.renameTo(file);
        }
    }

    private OriginalRequest checkInventoryAgainstOriginalRequests(
            Inventory currentInventory,
            List<OriginalRequest> originalRequests) {
        boolean employeeDoesNotExist = originalRequests.stream()
                .noneMatch(or -> or.employeeId.equals(currentInventory.employeeId));
        if (employeeDoesNotExist) throw new InventoryDiscrepancyException();

        Optional<OriginalRequest> originalRequest = originalRequests.stream()
                .filter(or -> or.itemId.equals(currentInventory.itemId))
                .findFirst();

        return originalRequest.orElseThrow(InventoryDiscrepancyException::new);
    }
}
