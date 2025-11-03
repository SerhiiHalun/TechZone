package com.example.techzone.controller.api;


import com.example.techzone.dto.CreateAddressRequestDTO;
import com.example.techzone.model.Address;
import com.example.techzone.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressApiController {
    private final AddressService addressService;

    public AddressApiController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Address>> getAllAddresses() {
        List<Address> addresses = addressService.getAllAddresses();
        return addresses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(addresses);
    }

    @GetMapping("/get-all-for-current-user")
    public ResponseEntity<List<Address>> getAllAddressesForCurrentUser() {
        List<Address> addresses = addressService.getAllAddressesForCurrentUser();
        return addresses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(addresses);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable int id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAddress(@RequestBody CreateAddressRequestDTO address) {
        Address createdAddress = addressService.createAddress(address.getCountry(), address.getCity(),
                address.getState(), address.getStreet(), address.getPhoneNumber(), address.getPostCode(), address.getIsDefault());
        return createdAddress != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(createdAddress)
                : ResponseEntity.badRequest().body("Address creation failed");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable int id,
                                           @RequestParam(required = false) String country,
                                           @RequestParam(required = false) String city,
                                           @RequestParam(required = false) String state,
                                           @RequestParam(required = false) String street,
                                           @RequestParam(required = false) String phoneNumber,
                                           @RequestParam(required = false) String postCode,
                                           @RequestParam(required = false) Boolean isDefault) {
        Address updatedAddress = addressService.updateAddress(id, country, city, state,
                street, phoneNumber, postCode, isDefault);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable int id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok("Address deleted");
    }


}
