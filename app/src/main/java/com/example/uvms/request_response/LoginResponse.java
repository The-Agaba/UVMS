package com.example.uvms.request_response;

public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String vendor_id;
    private String email;
    private String first_name;
    private String last_name;
    private String company_name;
    private String tin_number;
    private boolean is_active;
    private String message;
    private boolean success;

    // Getters
    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public Long getExpiresIn() { return expiresIn; }
    public String getVendor_id() { return vendor_id; }
    public String getEmail() { return email; }
    public String getFirst_name() { return first_name; }
    public String getLast_name() { return last_name; }
    public String getCompany_name() { return company_name; }
    public String getTin_number() { return tin_number; }
    public boolean isIs_active() { return is_active; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
}
