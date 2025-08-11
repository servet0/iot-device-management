package com.yourcompany.iotplatform.iot_device_management.dto;

import com.yourcompany.iotplatform.iot_device_management.model.Device;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Cihaz DTO (Data Transfer Object)
 * REST API'de cihaz verilerini transfer etmek için kullanılır
 */
public class DeviceDto {
    
    private Long id;
    
    @NotBlank(message = "Cihaz ID boş olamaz")
    @Size(min = 3, max = 100, message = "Cihaz ID 3-100 karakter arasında olmalıdır")
    private String deviceId;
    
    @NotBlank(message = "Cihaz adı boş olamaz")
    @Size(max = 200, message = "Cihaz adı 200 karakterden uzun olamaz")
    private String name;
    
    @Size(max = 500, message = "Açıklama 500 karakterden uzun olamaz")
    private String description;
    
    @NotNull(message = "Cihaz tipi boş olamaz")
    private Device.DeviceType deviceType;
    
    private Device.DeviceStatus status;
    private LocalDateTime lastSeen;
    private String ipAddress;
    private String firmwareVersion;
    private String hardwareVersion;
    private Double locationLat;
    private Double locationLng;
    private String locationDescription;
    private Long ownerId;
    private String ownerUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;
    
    // Constructors
    public DeviceDto() {}
    
    public DeviceDto(Device device) {
        this.id = device.getId();
        this.deviceId = device.getDeviceId();
        this.name = device.getName();
        this.description = device.getDescription();
        this.deviceType = device.getDeviceType();
        this.status = device.getStatus();
        this.lastSeen = device.getLastSeen();
        this.ipAddress = device.getIpAddress();
        this.firmwareVersion = device.getFirmwareVersion();
        this.hardwareVersion = device.getHardwareVersion();
        this.locationLat = device.getLocationLat();
        this.locationLng = device.getLocationLng();
        this.locationDescription = device.getLocationDescription();
        this.createdAt = device.getCreatedAt();
        this.updatedAt = device.getUpdatedAt();
        this.isActive = device.isActive();
        
        if (device.getOwner() != null) {
            this.ownerId = device.getOwner().getId();
            this.ownerUsername = device.getOwner().getUsername();
        }
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
    
    public Device.DeviceType getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(Device.DeviceType deviceType) {
        this.deviceType = deviceType;
    }
    
    public Device.DeviceStatus getStatus() {
        return status;
    }
    
    public void setStatus(Device.DeviceStatus status) {
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
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getOwnerUsername() {
        return ownerUsername;
    }
    
    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
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
