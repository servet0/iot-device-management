package com.yourcompany.iotplatform.iot_device_management.dto;

import com.yourcompany.iotplatform.iot_device_management.model.TelemetryData;

import java.time.LocalDateTime;

/**
 * Telemetri Verisi DTO (Data Transfer Object)
 * REST API'de telemetri verilerini transfer etmek için kullanılır
 */
public class TelemetryDataDto {
    
    private Long id;
    private Long deviceId;
    private String deviceName;
    private LocalDateTime timestamp;
    private String topic;
    private String payload;
    private String dataType;
    private String unit;
    private Double valueNumeric;
    private String valueString;
    private Boolean valueBoolean;
    private Integer quality;
    private LocalDateTime receivedAt;
    
    // Constructors
    public TelemetryDataDto() {}
    
    public TelemetryDataDto(TelemetryData telemetryData) {
        this.id = telemetryData.getId();
        this.timestamp = telemetryData.getTimestamp();
        this.topic = telemetryData.getTopic();
        this.payload = telemetryData.getPayload();
        this.dataType = telemetryData.getDataType();
        this.unit = telemetryData.getUnit();
        this.valueNumeric = telemetryData.getValueNumeric();
        this.valueString = telemetryData.getValueString();
        this.valueBoolean = telemetryData.getValueBoolean();
        this.quality = telemetryData.getQuality();
        this.receivedAt = telemetryData.getReceivedAt();
        
        if (telemetryData.getDevice() != null) {
            this.deviceId = telemetryData.getDevice().getId();
            this.deviceName = telemetryData.getDevice().getName();
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public String getPayload() {
        return payload;
    }
    
    public void setPayload(String payload) {
        this.payload = payload;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public Double getValueNumeric() {
        return valueNumeric;
    }
    
    public void setValueNumeric(Double valueNumeric) {
        this.valueNumeric = valueNumeric;
    }
    
    public String getValueString() {
        return valueString;
    }
    
    public void setValueString(String valueString) {
        this.valueString = valueString;
    }
    
    public Boolean getValueBoolean() {
        return valueBoolean;
    }
    
    public void setValueBoolean(Boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }
    
    public Integer getQuality() {
        return quality;
    }
    
    public void setQuality(Integer quality) {
        this.quality = quality;
    }
    
    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }
    
    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}
