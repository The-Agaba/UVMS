package com.example.uvms.request_response;

public class RegisterResponse {

    private boolean success;
    private String message;
    private String tokenType;
    private String token;
    private long expiresIn;
    private VendorData vendor;

    // --- Inner static class for Vendor details ---
    public static class VendorData {
        private Integer vendorId;
        private String email;
        private String firstName;
        private String lastName;
        private String companyName;
        private String tinNumber;
        private boolean isActive;

        // Getters & Setters
        public Integer getVendorId() {
            return vendorId;
        }
        public void setVendorId(Integer vendorId) {
            this.vendorId = vendorId;
        }

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getCompanyName() {
            return companyName;
        }
        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getTinNumber() {
            return tinNumber;
        }
        public void setTinNumber(String tinNumber) {
            this.tinNumber = tinNumber;
        }

        public boolean isActive() {
            return isActive;
        }
        public void setActive(boolean active) {
            isActive = active;
        }
    }

    // --- Getters & Setters for main response ---
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getTokenType() {
        return tokenType;
    }
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public VendorData getVendor() {
        return vendor;
    }
    public void setVendor(VendorData vendor) {
        this.vendor = vendor;
    }
}
