package controllers;

import models.Order;
import models.OrderAttachment;
import org.apache.commons.io.FileUtils;
import play.i18n.Messages;

import java.io.File;
import java.io.IOException;

public class OrderAttachments extends ApplicationController {
    
    public static void modalUpload(Long orderId) {
        notFoundIfNull(orderId);
        Order order = Order.findById(orderId);
        notFoundIfNull(order);
        
        render(order);
    }
    
    public static void upload(Long orderId, File file) {
        notFoundIfNull(orderId);
        Order order = Order.findById(orderId);
        notFoundIfNull(order);

        if(file == null) {
            flash.error(Messages.get("orderAttachment.fileNotFound"));
            Orders.show(order.id);
        }
        
        if(!order.getAttachmentFolder().exists() && !order.getAttachmentFolder().mkdirs()) {
            flash.error(Messages.get("orderAttachment.unableToCreateAttachmentFolder"));
            Orders.show(order.id);
        }

        String filename = OrderAttachment.sanitizeFilename(file.getName());
        
        File targetFile = new File(order.getAttachmentFolder(), filename);
        if(targetFile.exists()) {
            flash.error(Messages.get("orderAttachment.fileExistsAlready"));
            Orders.show(order.id);
        }
        
        try {
            FileUtils.copyFile(file, targetFile);
        } catch (IOException e) {
            flash.error(Messages.get("orderAttachment.unableToCopyFile"));
            Orders.show(order.id);
        }

        flash.success(Messages.get("successfullyCreated", Messages.get("orderAttachment")));
        Orders.show(order.id);
    }
    
    public static void delete(Long orderId, String filename) {
        notFoundIfNull(orderId);
        Order order = Order.findById(orderId);
        notFoundIfNull(order);

        for(OrderAttachment orderAttachment : order.getAttachments()) {
            if(orderAttachment.getFilename().equals(filename)) {
                render(order, orderAttachment);
            }
        }
    }
    
    public static void destroy(Long orderId, String filename) {
        notFoundIfNull(orderId);
        Order order = Order.findById(orderId);
        notFoundIfNull(order);

        for(OrderAttachment orderAttachment : order.getAttachments()) {
            if(orderAttachment.getFilename().equals(filename)) {
                destroyOrderAttachment(orderAttachment, order);    
            }
        }
        flash.error(Messages.get("orderAttachment.fileNotFound"));
        Orders.show(order.id);
    }
    
    private static void destroyOrderAttachment(OrderAttachment orderAttachment, Order order) {
        if(orderAttachment.delete()) {
            flash.success(Messages.get("successfullyDeleted", Messages.get("orderAttachment")));
        } else {
            flash.error(Messages.get("orderAttachment.unableToDeleteFile"));
        }
        Orders.show(order.id);
    }
}
