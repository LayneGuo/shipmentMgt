package com.cienet.shipment.exception;

public class ShipmentException extends RuntimeException {
    private final String message;
    private Integer errorCode;

    public ShipmentException(String message) {
        super(message);
        this.message = message;
    }

    public ShipmentException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
}
