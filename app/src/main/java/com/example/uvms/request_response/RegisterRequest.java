package com.example.uvms.request_response;


import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("password_hash")  // server expects hashed/stored password field
    private String passwordHash;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("company_name")
    private String companyName;

    @SerializedName("tin_number")
    private String tinNumber;

    @SerializedName("business_address")
    private String businessAddress;

    @SerializedName("business_type")
    private String businessType;

    // Constructor
    public RegisterRequest(String firstName, String lastName, String email, String passwordHash,
                           String phoneNumber, String companyName, String tinNumber,
                           String businessAddress, String businessType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
        this.companyName = companyName;
        this.tinNumber = tinNumber;
        this.businessAddress = businessAddress;
        this.businessType = businessType;
    }
}
