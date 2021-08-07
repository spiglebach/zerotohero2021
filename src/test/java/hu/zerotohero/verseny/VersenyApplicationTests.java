package hu.zerotohero.verseny;

import hu.zerotohero.verseny.inventory.Inventory;
import hu.zerotohero.verseny.inventory.InventoryService;
import hu.zerotohero.verseny.inventory.PostInventoryRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.AfterTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestExecution;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class VersenyApplicationTests {
    private static final String TEST_INVENTORY_STORAGE_FILENAME = "test_" + Inventory.STORAGE_FILENAME;

    @Spy InventoryService inventoryService;

    @BeforeTestExecution
    public void setup() {
        when(inventoryService.getInventoryStorageFileName()).thenReturn(TEST_INVENTORY_STORAGE_FILENAME);
    }

    @AfterTestExecution
    public void deleteTestFile() {
        new File(TEST_INVENTORY_STORAGE_FILENAME).delete();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testInventoryCreation() throws IOException {
        List<Inventory> initialInventories = inventoryService.getInventories();
        assertNotNull(initialInventories);
        int initialInventoryCount = initialInventories.size();
        Inventory createdInventory = inventoryService.create(new PostInventoryRequest(
                "ASD12345",
                "POIU123455",
                1711341112));
        assertNotNull(createdInventory);
        assertEquals(initialInventoryCount + 1, inventoryService.getInventories().size());
    }
}
