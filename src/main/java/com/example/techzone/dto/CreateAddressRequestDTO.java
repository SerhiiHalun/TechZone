package com.example.techzone.dto;

import lombok.Data;

@Data
public class CreateAddressRequestDTO {
    String country;
    String city;
    String state;
    String street;
    String phoneNumber;
    String postCode;
    Boolean isDefault;
}
