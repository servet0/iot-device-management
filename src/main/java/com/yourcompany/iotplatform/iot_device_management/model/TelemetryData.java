package com.yourcompany.iotplatform.iot_device_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Telemetri Verisi modeli
 * MQTT üzerinden gelen cihaz verilerini saklamak için kullanılır
 */
@Entity
@Table(name = "telemetry_data")
public class TelemetryData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    @NotNull(message = "Cihaz boş olamaz")
    private Device device;
    
    @Column(name = "timestamp", nullable = false)
    @NotNull(message = "Zaman damgası boş olamaz")
    private LocalDateTime timestamp;
    
    @Column(name = "topic", nullable = false)
    @NotNull(message = "MQTT topic boş olamaz")
    private String topic;
    
    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    @NotNull(message = "Veri yükü boş olamaz")
    private String payload;
    
    @Column(name = "data_type")
    private String dataType;
    
    @Column(name = "unit")
    private String unit;
    
    @Column(name = "value_numeric")
    private Double valueNumeric;
    
    @Column(name = "value_string")
    private String valueString;
    
    @Column(name = "value_boolean")
    private Boolean valueBoolean;
    
    @Column(name = "quality")
    private Integer quality;
    
    @Column(name = "received_at")
    private LocalDateTime receivedAt;
    
    // JPA Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        if (receivedAt == null) {
            receivedAt = LocalDateTime.now();
        }
    }
    
    // Constructors
    public TelemetryData() {}
    
    public TelemetryData(Device device, String topic, String payload) {
        this.device = device;
        this.topic = topic;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
        this.receivedAt = LocalDateTime.now();
    }
    
    public TelemetryData(Device device, String topic, String payload, String dataType, Double valueNumeric) {
        this(device, topic, payload);
        this.dataType = dataType;
        this.valueNumeric = valueNumeric;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Device getDevice() {
        return device;
    }
    
    public void setDevice(Device device) {
        this.device = device;
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
