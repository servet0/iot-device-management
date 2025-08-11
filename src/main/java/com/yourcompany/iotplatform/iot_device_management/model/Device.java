package com.yourcompany.iotplatform.iot_device_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * IoT Cihazı modeli
 * Cihaz bilgileri, durumu ve konfigürasyonu için kullanılır
 */
@Entity
@Table(name = "devices")
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Cihaz ID boş olamaz")
    @Size(min = 3, max = 100, message = "Cihaz ID 3-100 karakter arasında olmalıdır")
    @Column(unique = true, nullable = false)
    private String deviceId;
    
    @NotBlank(message = "Cihaz adı boş olamaz")
    @Size(max = 200, message = "Cihaz adı 200 karakterden uzun olamaz")
    @Column(nullable = false)
    private String name;
    
    @Size(max = 500, message = "Açıklama 500 karakterden uzun olamaz")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status = DeviceStatus.OFFLINE;
    
    @Column(name = "last_seen")
    private LocalDateTime lastSeen;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "firmware_version")
    private String firmwareVersion;
    
    @Column(name = "hardware_version")
    private String hardwareVersion;
    
    @Column(name = "location_lat")
    private Double locationLat;
    
    @Column(name = "location_lng")
    private Double locationLng;
    
    @Column(name = "location_description")
    private String locationDescription;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    // JPA Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Enums
    public enum DeviceType {
        SENSOR, ACTUATOR, GATEWAY, CONTROLLER, CAMERA
    }
    
    public enum DeviceStatus {
        ONLINE, OFFLINE, MAINTENANCE, ERROR, DISABLED
    }
    
    // Constructors
    public Device() {}
    
    public Device(String deviceId, String name, DeviceType deviceType) {
        this.deviceId = deviceId;
        this.name = name;
        this.deviceType = deviceType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public DeviceType getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
    
    public DeviceStatus getStatus() {
        return status;
    }
    
    public void setStatus(DeviceStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getLastSeen() {
        return lastSeen;
    }
    
    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
    
    public String getHardwareVersion() {
        return hardwareVersion;
    }
    
    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }
    
    public Double getLocationLat() {
        return locationLat;
    }
    
    public void setLocationLat(Double locationLat) {
        this.locationLat = locationLat;
    }
    
    public Double getLocationLng() {
        return locationLng;
    }
    
    public void setLocationLng(Double locationLng) {
        this.locationLng = locationLng;
    }
    
    public String getLocationDescription() {
        return locationDescription;
    }
    
    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
}
