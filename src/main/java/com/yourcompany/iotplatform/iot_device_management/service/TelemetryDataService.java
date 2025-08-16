package com.yourcompany.iotplatform.iot_device_management.service;

import com.yourcompany.iotplatform.iot_device_management.model.Device;
import com.yourcompany.iotplatform.iot_device_management.model.TelemetryData;
import com.yourcompany.iotplatform.iot_device_management.repository.DeviceRepository;
import com.yourcompany.iotplatform.iot_device_management.repository.TelemetryDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Telemetri Verisi Servisi
 * Telemetri verilerinin yönetimi ve sorgulanması
 */
@Service
public class TelemetryDataService {
    
    private static final Logger logger = LoggerFactory.getLogger(TelemetryDataService.class);
    
    @Autowired
    private TelemetryDataRepository telemetryDataRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    /**
     * Belirli cihaza ait telemetri verilerini getirme
     * @param deviceId Cihaz ID'si
     * @param limit Limit sayısı
     * @return Telemetri verileri
     */
    public List<TelemetryData> getTelemetryDataByDevice(Long deviceId, int limit) {
        Optional<Device> device = deviceRepository.findById(deviceId);
        if (device.isEmpty()) {
            return List.of();
        }
        
        return telemetryDataRepository.findLatestByDevice(device.get(), PageRequest.of(0, limit));
    }
    
    /**
     * Belirli cihaza ait telemetri verilerini zaman aralığına göre getirme
     * @param deviceId Cihaz ID'si
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Telemetri verileri
     */
    public List<TelemetryData> getTelemetryDataByTimeRange(Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        Optional<Device> device = deviceRepository.findById(deviceId);
        if (device.isEmpty()) {
            return List.of();
        }
        
        return telemetryDataRepository.findByDeviceAndTimestampBetweenOrderByTimestampDesc(device.get(), startTime, endTime);
    }
    
    /**
     * Belirli cihazın en son telemetri verisini getirme
     * @param deviceId Cihaz ID'si
     * @return En son telemetri verisi
     */
    public Optional<TelemetryData> getLatestTelemetryData(Long deviceId) {
        Optional<Device> device = deviceRepository.findById(deviceId);
        if (device.isEmpty()) {
            return Optional.empty();
        }
        
        List<TelemetryData> latestData = telemetryDataRepository.findLatestByDevice(device.get(), PageRequest.of(0, 1));
        return latestData.isEmpty() ? Optional.empty() : Optional.of(latestData.get(0));
    }
    
    /**
     * Belirli topic'e ait telemetri verilerini getirme
     * @param topic MQTT topic
     * @return Telemetri verileri
     */
    public List<TelemetryData> getTelemetryDataByTopic(String topic) {
        return telemetryDataRepository.findByTopicOrderByTimestampDesc(topic);
    }
    
    /**
     * Belirli veri tipine ait telemetri verilerini getirme
     * @param dataType Veri tipi
     * @return Telemetri verileri
     */
    public List<TelemetryData> getTelemetryDataByDataType(String dataType) {
        return telemetryDataRepository.findByDataTypeOrderByTimestampDesc(dataType);
    }
    
    /**
     * Belirli cihaz ve veri tipine ait telemetri verilerini getirme
     * @param deviceId Cihaz ID'si
     * @param dataType Veri tipi
     * @return Telemetri verileri
     */
    public List<TelemetryData> getTelemetryDataByDeviceAndDataType(Long deviceId, String dataType) {
        Optional<Device> device = deviceRepository.findById(deviceId);
        if (device.isEmpty()) {
            return List.of();
        }
        
        return telemetryDataRepository.findByDeviceAndDataTypeOrderByTimestampDesc(device.get(), dataType);
    }
    
    /**
     * Belirli zaman aralığındaki tüm telemetri verilerini getirme
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Telemetri verileri
     */
    public List<TelemetryData> getTelemetryDataByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return telemetryDataRepository.findByTimestampBetweenOrderByTimestampDesc(startTime, endTime);
    }
    
    /**
     * Belirli cihazın belirli zaman aralığındaki ortalama değerini hesaplama
     * @param deviceId Cihaz ID'si
     * @param dataType Veri tipi
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Ortalama değer
     */
    public Double getAverageValueByDeviceAndDataTypeAndTimeRange(Long deviceId, String dataType, LocalDateTime startTime, LocalDateTime endTime) {
        Optional<Device> device = deviceRepository.findById(deviceId);
        if (device.isEmpty()) {
            return null;
        }
        
        return telemetryDataRepository.findAverageValueByDeviceAndDataTypeAndTimeRange(device.get(), dataType, startTime, endTime);
    }
    
    /**
     * Belirli cihazın belirli zaman aralığındaki minimum değerini bulma
     * @param deviceId Cihaz ID'si
     * @param dataType Veri tipi
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Minimum değer
     */
    public Double getMinValueByDeviceAndDataTypeAndTimeRange(Long deviceId, String dataType, LocalDateTime startTime, LocalDateTime endTime) {
        Optional<Device> device = deviceRepository.findById(deviceId);
        if (device.isEmpty()) {
            return null;
        }
        
        return telemetryDataRepository.findMinValueByDeviceAndDataTypeAndTimeRange(device.get(), dataType, startTime, endTime);
    }
    
    /**
     * Belirli cihazın belirli zaman aralığındaki maksimum değerini bulma
     * @param deviceId Cihaz ID'si
     * @param dataType Veri tipi
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Maksimum değer
     */
    public Double getMaxValueByDeviceAndDataTypeAndTimeRange(Long deviceId, String dataType, LocalDateTime startTime, LocalDateTime endTime) {
        Optional<Device> device = deviceRepository.findById(deviceId);
        if (device.isEmpty()) {
            return null;
        }
        
        return telemetryDataRepository.findMaxValueByDeviceAndDataTypeAndTimeRange(device.get(), dataType, startTime, endTime);
    }
    
    /**
     * Eski telemetri verilerini silme (veri temizleme)
     * @param cutoffTime Kesme zamanı
     * @return Silinen kayıt sayısı
     */
    public int deleteOldTelemetryData(LocalDateTime cutoffTime) {
        int deletedCount = telemetryDataRepository.deleteOldTelemetryData(cutoffTime);
        logger.info("{} adet eski telemetri verisi silindi", deletedCount);
        return deletedCount;
    }
    
    /**
     * Telemetri verisi kaydetme
     * @param telemetryData Telemetri verisi
     * @return Kaydedilen telemetri verisi
     */
    public TelemetryData saveTelemetryData(TelemetryData telemetryData) {
        TelemetryData savedData = telemetryDataRepository.save(telemetryData);
        logger.debug("Telemetri verisi kaydedildi: Cihaz: {}, Veri Tipi: {}", 
                savedData.getDevice().getDeviceId(), savedData.getDataType());
        return savedData;
    }
}
