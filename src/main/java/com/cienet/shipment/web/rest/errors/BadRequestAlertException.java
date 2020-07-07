package com.cienet.shipment.web.rest.errors;

public class BadRequestAlertException extends ShipmentException {

    public BadRequestAlertException(String defaultMessage) {
        super(defaultMessage);
    }
}
