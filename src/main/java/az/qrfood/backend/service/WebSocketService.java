package az.qrfood.backend.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send a notification that a new order has been created for a specific restaurant
     * @param restaurantId the ID of the restaurant
     */
    public void notifyNewOrder(String restaurantId) {
        messagingTemplate.convertAndSend("/topic/orders/" + restaurantId, 
            new OrderNotification("NEW_ORDER", "A new order has been created"));
    }

    /**
     * Send a notification that an order has been updated for a specific restaurant
     * @param restaurantId the ID of the restaurant
     * @param orderId the ID of the updated order
     * @param status the new status of the order (optional)
     */
    public void notifyOrderUpdate(String restaurantId, Long orderId, String status) {
        String message = "Order #" + orderId + " has been updated";
        if (status != null && !status.isEmpty()) {
            message += " to status: " + status;
        }
        messagingTemplate.convertAndSend("/topic/orders/" + restaurantId, 
            new OrderNotification("ORDER_UPDATED", message));
    }

    // Simple notification class
    public static class OrderNotification {
        private String type;
        private String message;

        public OrderNotification(String type, String message) {
            this.type = type;
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}