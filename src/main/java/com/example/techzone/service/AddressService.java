package com.example.techzone.service;


import com.example.techzone.model.Address;
import com.example.techzone.model.Role;
import com.example.techzone.model.User;
import com.example.techzone.repository.AddressRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    public AddressService(AddressRepository addressRepository, UserService userService) {
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    public List<Address> getAllAddressesForCurrentUser() {
        return addressRepository.findAllByUser(userService.getCurrentUser());
    }

    public Address getAddressById(long id) {
        User user = userService.getCurrentUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (user.getRole() == Role.ADMIN || user == address.getUser()) {
            return address;
        }
        return null;

    }

    @Transactional
    public Address createAddress(String country, String city, String state,
                                 String street, String phoneNumber, String postCode, boolean isDefault) {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("Current user not found");
        }


        Address address = new Address();
        address.setUser(user);
        address.setCountry(country);
        address.setCity(city);
        address.setState(state);
        address.setStreet(street);
        address.setPhoneNumber(phoneNumber);
        address.setPostCode(postCode);

        if (isDefault) {
            addressRepository.findByDefaultAddressIsTrue().ifPresent(defaultAddress -> {
                defaultAddress.setDefaultAddress(false);
                addressRepository.save(defaultAddress);
                System.out.println("Default address updated");
            });

            address.setDefaultAddress(true);
            System.out.println("Default address updated1");
        } else {
            address.setDefaultAddress(false);
            System.out.println("Default address updated2");
        }
        System.out.println("Address created");
        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(long id, String country, String city, String state,
                                 String street, String phoneNumber, String postCode, Boolean isDefault) {

        return addressRepository.findById(id).map(address -> {
            User user = userService.getCurrentUser();
            if (user != null && !address.getUser().getId().equals(user.getId())) {
                address.setUser(user);
            }
            if (country != null && !country.isBlank()) {
                address.setCountry(country);
            }
            if (city != null && !city.isBlank()) {
                address.setCity(city);
            }
            if (state != null && !state.isBlank()) {
                address.setState(state);
            }
            if (street != null && !street.isBlank()) {
                address.setStreet(street);
            }
            if (phoneNumber != null && !phoneNumber.isBlank()) {
                address.setPhoneNumber(phoneNumber);
            }
            if (postCode != null && !postCode.isBlank()) {
                address.setPostCode(postCode);
            }
            if (isDefault) {
                addressRepository.findByDefaultAddressIsTrue().ifPresent(defaultAddress -> {
                    defaultAddress.setDefaultAddress(false);
                    addressRepository.save(defaultAddress);
                });
            }
            address.setDefaultAddress(isDefault);


            return addressRepository.save(address);
        }).orElseThrow(() -> new EntityNotFoundException("Address not found"));
    }

    @Transactional
    public void deleteAddress(long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        addressRepository.deleteById(id);

        if (address.isDefaultAddress()) {
            addressRepository.findTopByOrderByIdDesc().ifPresent(newDefault -> {
                newDefault.setDefaultAddress(true);
                addressRepository.save(newDefault);
            });
        }
    }
}