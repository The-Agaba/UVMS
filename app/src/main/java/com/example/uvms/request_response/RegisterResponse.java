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
        private Integer vendor_id;
        private String email;
        private String first_name;
        private String last_name;
        private String company_name;
        private String tin_number;
        private boolean is_active;

        // Getters & Setters
        public Integer getVendor_id() {
            return vendor_id;
        }
        public void setVendor_id(Integer vendor_id) {
            this.vendor_id = vendor_id;
        }

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirst_name() {
            return first_name;
        }
        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }
        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getCompany_name() {
            return company_name;
        }
        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public String getTin_number() {
            return tin_number;
        }
        public void setTin_number(String tin_number) {
            this.tin_number = tin_number;
        }

        public boolean isIs_active() {
            return is_active;
        }
        public void setIs_active(boolean is_active) {
            this.is_active = is_active;
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
