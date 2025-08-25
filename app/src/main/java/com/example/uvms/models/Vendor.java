package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;

public class Vendor {
    @SerializedName("vendor_id")
    public int vendorId;

    @SerializedName("email")
    public String email;

    @SerializedName("password_hash")
    public String password;

    @SerializedName("first_name")
    public String firstName;

    @SerializedName("last_name")
    public String lastName;

    @SerializedName("profile_picture_path")
    public String profilePicturePath;

    @SerializedName("phone_number")
    public String phoneNumber;

    @SerializedName("company_name")
    public String companyName;

    @SerializedName("tin_number")
    public String tinNumber;

    @SerializedName("business_address")
    public String businessAddress;

    @SerializedName("registration_date")
    public String registrationDate;

    @SerializedName("last_login")
    public String lastLogin;

    @SerializedName("is_active")
    public boolean isActive = true;

    @SerializedName("business_type")
    public String businessType;

    // Full constructor (kept all fields)
    public Vendor(int vendorId, String email, String password, String firstName, String lastName,
                  String profilePicturePath, String phoneNumber, String companyName, String tinNumber,
                  String businessAddress, String registrationDate, String lastLogin, boolean isActive,
                  String businessType) {
        this.vendorId = vendorId;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicturePath = profilePicturePath;
        this.phoneNumber = phoneNumber;
        this.companyName = companyName;
        this.tinNumber = tinNumber;
        this.businessAddress = businessAddress;
        this.registrationDate = registrationDate;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
        this.businessType = businessType;
    }

    // Simplified constructor for registration
    public Vendor(String firstName, String lastName, String email, String password,
                  String phoneNumber, String companyName, String tinNumber,
                  String businessAddress, String businessType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.companyName = companyName;
        this.tinNumber = tinNumber;
        this.businessAddress = businessAddress;
        this.businessType = businessType;
    }

    // Getters and Setters (unchanged)
    public int getVendorId() { return vendorId; }
    public void setVendorId(int vendorId) { this.vendorId = vendorId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getProfilePicturePath() { return profilePicturePath; }
    public void setProfilePicturePath(String profilePicturePath) { this.profilePicturePath = profilePicturePath; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getTinNumber() { return tinNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }

    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }

    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }


}
