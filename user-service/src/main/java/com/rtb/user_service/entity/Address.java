package com.rtb.user_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "user_addresses")
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotBlank(message = "Address Label is required and cannot bve blank")
    @Size(max = 100, message = "Label should be less than 100 characters")
    private String addressLabel;

    private String contactName;

    private String contactPhone;

    private String addressLine1;

    private String addressLine2;

    private String locality;

    private String city;

    private String state;

    private String pincode;

    private Boolean isDefaultShippingAddress;

    private Instant createdAt;

    private Instant updatedAt;
}
