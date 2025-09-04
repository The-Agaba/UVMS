package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Vendor implements Serializable {

    @SerializedName("vendorId")
    private int vendorId;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("profilePicturePath")
    private String profilePicturePath;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("companyName")
    private String companyName;

    @SerializedName("tinNumber")
    private String tinNumber;

    @SerializedName("businessAddress")
    private String businessAddress;

    @SerializedName("registrationDate")
    private String registrationDate;

    @SerializedName("lastLogin")
    private String lastLogin;

    @SerializedName("active")
    private boolean isActive;

    @SerializedName("businessType")
    private String businessType;

    @SerializedName("role")
    private String role;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("deletedAt")
    private String deletedAt;

    // --- Constructor ---
    public Vendor(int vendorId, String email, String password, String firstName, String lastName,
                  String profilePicturePath, String phoneNumber, String companyName, String tinNumber,
                  String businessAddress, String registrationDate, String lastLogin, boolean isActive,
                  String businessType, String role, String createdAt, String updatedAt, String deletedAt) {
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
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // --- Getters & Setters ---
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

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getDeletedAt() { return deletedAt; }
    public void setDeletedAt(String deletedAt) { this.deletedAt = deletedAt; }
}
