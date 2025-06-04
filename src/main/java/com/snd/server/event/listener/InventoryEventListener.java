package com.snd.server.event.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.snd.server.constant.RabbitMqConstant;
import com.snd.server.event.domain.InventoryEvent;
import com.snd.server.service.InventoryService;
import com.snd.server.service.ProductService;

@Component
public class InventoryEventListener {
    private final InventoryService inventoryService;
    private final ProductService productService;

    public InventoryEventListener(InventoryService inventoryService,
            ProductService productService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
    }

    @RabbitListener(queues = RabbitMqConstant.INVENTORY_QUEUE)
    public void handleInventoryEvent(InventoryEvent event) {
        try {

            switch (event.getEventType()) {
                case INVENTORY_CREATE:
                    inventoryService.createInventory(event);
                    break;
                case INVENTORY_UPDATE:
                    inventoryService.updateInventory(event);
                    break;
                case INVENTORY_DELETE:
                    inventoryService.deleteInventory(event);
                    break;
                case INVENTORY_ORDER:
                    inventoryService.deductInventory(event);
                    break;
                case INVENTORY_PRODUCT:
                    productService.updateProductInventory(event);
                    break;

                default:
                    System.err.println("Invalid event type: " + event.getEventType());
            }
        } catch (Exception e) {
            System.err.println("Error processing event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
