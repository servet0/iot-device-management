package com.yourcompany.iotplatform.iot_device_management.controller;

import com.yourcompany.iotplatform.iot_device_management.dto.TelemetryDataDto;
import com.yourcompany.iotplatform.iot_device_management.model.TelemetryData;
import com.yourcompany.iotplatform.iot_device_management.service.TelemetryDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Telemetri Verisi Controller'ı
 * Telemetri verilerinin sorgulanması için REST API endpoint'leri
 */
@RestController
@RequestMapping("/api/telemetry")
@CrossOrigin(origins = "*")
public class TelemetryController {
    
    private static final Logger logger = LoggerFactory.getLogger(TelemetryController.class);
    
    @Autowired
    private TelemetryDataService telemetryDataService;
    
    /**
     * Belirli cihaza ait telemetri verilerini getirme
     * @param deviceId Cihaz ID'si
     * @param limit Limit sayısı
     * @return Telemetri verileri
     */
    @GetMapping("/device/{deviceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<List<TelemetryDataDto>> getTelemetryDataByDevice(
            @PathVariable Long deviceId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<TelemetryData> telemetryData = telemetryDataService.getTelemetryDataByDevice(deviceId, limit);
            List<TelemetryDataDto> telemetryDataDtos = telemetryData.stream()
                    .map(TelemetryDataDto::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(telemetryDataDtos);
        } catch (Exception e) {
            logger.error("Telemetri verisi alma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Belirli cihaza ait telemetri verilerini zaman aralığına göre getirme
     * @param deviceId Cihaz ID'si
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Telemetri verileri
     */
    @GetMapping("/device/{deviceId}/timerange")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<List<TelemetryDataDto>> getTelemetryDataByTimeRange(
            @PathVariable Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<TelemetryData> telemetryData = telemetryDataService.getTelemetryDataByTimeRange(deviceId, startTime, endTime);
            List<TelemetryDataDto> telemetryDataDtos = telemetryData.stream()
                    .map(TelemetryDataDto::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(telemetryDataDtos);
        } catch (Exception e) {
            logger.error("Zaman aralığı telemetri verisi alma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Belirli cihazın en son telemetri verisini getirme
     * @param deviceId Cihaz ID'si
     * @return En son telemetri verisi
     */
    @GetMapping("/device/{deviceId}/latest")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<TelemetryDataDto> getLatestTelemetryData(@PathVariable Long deviceId) {
        try {
            Optional<TelemetryData> telemetryData = telemetryDataService.getLatestTelemetryData(deviceId);
            if (telemetryData.isPresent()) {
                return ResponseEntity.ok(new TelemetryDataDto(telemetryData.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("En son telemetri verisi alma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Belirli topic'e ait telemetri verilerini getirme
     * @param topic MQTT topic
     * @return Telemetri verileri
     */
    @GetMapping("/topic/{topic}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<List<TelemetryDataDto>> getTelemetryDataByTopic(@PathVariable String topic) {
        try {
            List<TelemetryData> telemetryData = telemetryDataService.getTelemetryDataByTopic(topic);
            List<TelemetryDataDto> telemetryDataDtos = telemetryData.stream()
                    .map(TelemetryDataDto::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(telemetryDataDtos);
        } catch (Exception e) {
            logger.error("Topic bazlı telemetri verisi alma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Belirli veri tipine ait telemetri verilerini getirme
     * @param dataType Veri tipi
     * @return Telemetri verileri
     */
    @GetMapping("/datatype/{dataType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<List<TelemetryDataDto>> getTelemetryDataByDataType(@PathVariable String dataType) {
        try {
            List<TelemetryData> telemetryData = telemetryDataService.getTelemetryDataByDataType(dataType);
            List<TelemetryDataDto> telemetryDataDtos = telemetryData.stream()
                    .map(TelemetryDataDto::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(telemetryDataDtos);
        } catch (Exception e) {
            logger.error("Veri tipi bazlı telemetri verisi alma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Belirli cihaz ve veri tipine ait telemetri verilerini getirme
     * @param deviceId Cihaz ID'si
     * @param dataType Veri tipi
     * @return Telemetri verileri
     */
    @GetMapping("/device/{deviceId}/datatype/{dataType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<List<TelemetryDataDto>> getTelemetryDataByDeviceAndDataType(
            @PathVariable Long deviceId,
            @PathVariable String dataType) {
        try {
            List<TelemetryData> telemetryData = telemetryDataService.getTelemetryDataByDeviceAndDataType(deviceId, dataType);
            List<TelemetryDataDto> telemetryDataDtos = telemetryData.stream()
                    .map(TelemetryDataDto::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(telemetryDataDtos);
        } catch (Exception e) {
            logger.error("Cihaz ve veri tipi bazlı telemetri verisi alma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Belirli zaman aralığındaki tüm telemetri verilerini getirme
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Telemetri verileri
     */
    @GetMapping("/timerange")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<List<TelemetryDataDto>> getTelemetryDataByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<TelemetryData> telemetryData = telemetryDataService.getTelemetryDataByTimeRange(startTime, endTime);
            List<TelemetryDataDto> telemetryDataDtos = telemetryData.stream()
                    .map(TelemetryDataDto::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(telemetryDataDtos);
        } catch (Exception e) {
            logger.error("Genel zaman aralığı telemetri verisi alma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Belirli cihazın belirli zaman aralığındaki ortalama değerini hesaplama
     * @param deviceId Cihaz ID'si
     * @param dataType Veri tipi
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Ortalama değer
     */
    @GetMapping("/device/{deviceId}/average")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<Double> getAverageValue(
            @PathVariable Long deviceId,
            @RequestParam String dataType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            Double averageValue = telemetryDataService.getAverageValueByDeviceAndDataTypeAndTimeRange(
                    deviceId, dataType, startTime, endTime);
            
            if (averageValue != null) {
                return ResponseEntity.ok(averageValue);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Ortalama değer hesaplama hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Belirli cihazın belirli zaman aralığındaki minimum değerini bulma
     * @param deviceId Cihaz ID'si
     * @param dataType Veri tipi
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Minimum değer
     */
    @GetMapping("/device/{deviceId}/min")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<Double> getMinValue(
            @PathVariable Long deviceId,
            @RequestParam String dataType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            Double minValue = telemetryDataService.getMinValueByDeviceAndDataTypeAndTimeRange(
                    deviceId, dataType, startTime, endTime);
            
            if (minValue != null) {
                return ResponseEntity.ok(minValue);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Minimum değer bulma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Belirli cihazın belirli zaman aralığındaki maksimum değerini bulma
     * @param deviceId Cihaz ID'si
     * @param dataType Veri tipi
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Maksimum değer
     */
    @GetMapping("/device/{deviceId}/max")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<Double> getMaxValue(
            @PathVariable Long deviceId,
            @RequestParam String dataType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            Double maxValue = telemetryDataService.getMaxValueByDeviceAndDataTypeAndTimeRange(
                    deviceId, dataType, startTime, endTime);
            
            if (maxValue != null) {
                return ResponseEntity.ok(maxValue);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Maksimum değer bulma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
