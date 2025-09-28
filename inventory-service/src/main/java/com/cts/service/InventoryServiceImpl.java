package com.cts.service;

import com.cts.client.ItemServiceClient;
import com.cts.dtos.InventoryDto;

import java.util.List;

import com.cts.entities.Inventory;
import com.cts.exception.ResourceNotFoundException;
import com.cts.exception.ServiceUnavailableException;
import com.cts.repository.InventoryRepository;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final ItemValidationService itemValidationService;     // Inject ItemValidationService
    private final AuditService auditService;     // Inject AuditService
    private final NotificationSenderService notificationSenderService;     // Inject NotificationSenderService

    private static final int LOW_STOCK_THRESHOLD = 10;


    @Override
    public InventoryDto addInventory(InventoryDto inventoryDto) {
        // Validate item ID before adding inventory
        itemValidationService.validateItemId(inventoryDto.getItemId());

        Inventory inventory = modelMapper.map(inventoryDto, Inventory.class);
        inventory.setLastUpdated(LocalDateTime.now());
        Inventory savedInventory = inventoryRepository.save(inventory);

        // Log event
        auditService.logEvent("CREATE", savedInventory.getId(), "Inventory added for item ID: " + savedInventory.getItemId());

        return modelMapper.map(savedInventory, InventoryDto.class);
    }


    @Override
    public InventoryDto getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
        return modelMapper.map(inventory, InventoryDto.class);
    }

    @Override
    public List<InventoryDto> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(inventory -> modelMapper.map(inventory, InventoryDto.class))
                .toList();
    }

    @Override
    public InventoryDto updateInventory(Long id, InventoryDto inventoryDto) {
        // Validate item ID before adding inventory
        itemValidationService.validateItemId(inventoryDto.getItemId());

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));

        inventory.setItemId(inventoryDto.getItemId());
        inventory.setQuantity(inventoryDto.getQuantity());
        inventory.setWarehouseLocation(inventoryDto.getWarehouseLocation());
        inventory.setLastUpdated(LocalDateTime.now());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        // Log event
        auditService.logEvent("UPDATE", updatedInventory.getId(), "Inventory updated for item ID: " + updatedInventory.getItemId());

        // Check for low stock and send notification
        if (updatedInventory.getQuantity() < LOW_STOCK_THRESHOLD) {
            notificationSenderService.sendLowStockNotification(updatedInventory.getItemId());
        }

        return modelMapper.map(updatedInventory, InventoryDto.class);
    }

    @Override
    public String deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
        inventoryRepository.delete(inventory);

        auditService.logEvent("DELETE", id, "Inventory record with ID " + id + " was deleted.");

        return "Inventory with ID " + id + " deleted successfully.";
    }
}