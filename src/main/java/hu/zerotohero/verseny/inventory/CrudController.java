package hu.zerotohero.verseny.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class CrudController {
    @Autowired InventoryService inventoryService;

    private Map<String, Inventory> cache = new HashMap<>();

    @PostMapping
    public ResponseEntity<String> create(@RequestBody PostInventoryRequest request) {
        Inventory newInventory = inventoryService.create(request);

        if (newInventory == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        cache.putIfAbsent(newInventory.id, newInventory);
        return ResponseEntity.ok(newInventory.id);
    }

    @GetMapping("{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable String id) {
        Inventory inventory = cache.get(id);
        if (inventory != null) {
            return ResponseEntity.ok(inventory);
        }

        inventory = inventoryService.findById(id);
        if (inventory == null) {
            return ResponseEntity.notFound().build();
        }

        cache.putIfAbsent(id, inventory);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> getInventories() {
        try {
            return ResponseEntity.ok(inventoryService.getInventories());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("check")
    public ResponseEntity<Void> performInventoryCheck(){
        cache.clear();
        try {
            inventoryService.performInventoryCheck();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Inventory> updateInventory(
            @PathVariable("id") String id,
            @RequestBody PostInventoryRequest newInventoryData) {
        Inventory updatedInventory = inventoryService.updateById(id, newInventoryData);
        if (updatedInventory == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedInventory);
    }
}

