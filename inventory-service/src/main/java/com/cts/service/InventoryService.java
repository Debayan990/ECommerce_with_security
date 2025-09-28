package com.cts.service;

import com.cts.dtos.InventoryDto;

import java.util.List;

public interface InventoryService {
    InventoryDto addInventory(InventoryDto inventoryDto);
    InventoryDto getInventoryById(Long id);
    List<InventoryDto> getAllInventory();
    InventoryDto updateInventory(Long id, InventoryDto inventoryDto);
    String deleteInventory(Long id);

}
