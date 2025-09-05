package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Admin implements Serializable {

    @SerializedName("admin_id")
    private int adminId;

    @SerializedName("email")
    private String email;

    @SerializedName("password_hash")
    private String passwordHash;

    @SerializedName("name")
    private String name;

    @SerializedName("role")
    private String role; // COLLEGE_ADMIN or SUPER_ADMIN

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("last_login")
    private String lastLogin;

    @SerializedName("is_active")
    private boolean isActive;

    // ✅ Optional: If backend later adds collegeId, you can uncomment
    // private Integer collegeId;

    // Getters and setters
    public int getAdminId() { return adminId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getCreatedAt() { return createdAt; }
    public String getLastLogin() { return lastLogin; }
    public boolean isActive() { return isActive; }
}
