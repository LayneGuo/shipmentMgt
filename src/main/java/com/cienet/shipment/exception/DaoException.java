package com.cienet.shipment.exception;

public class DaoException extends ShipmentException {
    private final String message;
    private Integer errorCode;

    public DaoException(String message) {
        super(message);
        this.message = message;
    }

    public DaoException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
}
