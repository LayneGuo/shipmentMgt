package com.cienet.shipment.web.rest;

import com.cienet.shipment.service.ShipmentService;
import com.cienet.shipment.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;

/**
 * REST controller for managing shipment.
 *
 */
@RestController
@RequestMapping("/api/ship")
public class ShipmentResource {

    private final Logger log = LoggerFactory.getLogger(ShipmentResource.class);

    private final ShipmentService shipmentService;

    public ShipmentResource(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    /**
     * {@code POST  /users}  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userDTO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already in use.
     */
    @PostMapping("/users")
    public void test() {

    }
//    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) {
//        log.debug("REST request to save User : {}", userDTO);
//        return null;
//    }
}
