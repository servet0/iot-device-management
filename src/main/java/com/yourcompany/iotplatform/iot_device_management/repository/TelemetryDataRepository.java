package com.yourcompany.iotplatform.iot_device_management.repository;

import com.yourcompany.iotplatform.iot_device_management.model.Device;
import com.yourcompany.iotplatform.iot_device_management.model.TelemetryData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Telemetri verisi veritabanı işlemleri için repository
 * Spring Data JPA ile otomatik CRUD işlemleri sağlar
 */
@Repository
public interface TelemetryDataRepository extends JpaRepository<TelemetryData, Long> {
    
    /**
     * Belirli cihaza ait telemetri verilerini listeleme
     * @param device Cihaz
     * @return Telemetri verisi listesi
     */
    List<TelemetryData> findByDeviceOrderByTimestampDesc(Device device);
    
    /**
     * Belirli cihaza ait telemetri verilerini sayfalı listeleme
     * @param device Cihaz
     * @param pageable Sayfalama bilgisi
     * @return Sayfalı telemetri verisi listesi
     */
    Page<TelemetryData> findByDeviceOrderByTimestampDesc(Device device, Pageable pageable);
    
    /**
     * Belirli zaman aralığındaki telemetri verilerini bulma
     * @param device Cihaz
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Telemetri verisi listesi
     */
    List<TelemetryData> findByDeviceAndTimestampBetweenOrderByTimestampDesc(
            Device device, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Belirli topic'e ait telemetri verilerini bulma
     * @param topic MQTT topic
     * @return Telemetri verisi listesi
     */
    List<TelemetryData> findByTopicOrderByTimestampDesc(String topic);
    
    /**
     * Belirli veri tipine ait telemetri verilerini bulma
     * @param dataType Veri tipi
     * @return Telemetri verisi listesi
     */
    List<TelemetryData> findByDataTypeOrderByTimestampDesc(String dataType);
    
    /**
     * Belirli cihaz ve veri tipine ait telemetri verilerini bulma
     * @param device Cihaz
     * @param dataType Veri tipi
     * @return Telemetri verisi listesi
     */
    List<TelemetryData> findByDeviceAndDataTypeOrderByTimestampDesc(Device device, String dataType);
    
    /**
     * Son N adet telemetri verisini bulma
     * @param device Cihaz
     * @param limit Limit sayısı
     * @return Telemetri verisi listesi
     */
    @Query("SELECT t FROM TelemetryData t WHERE t.device = :device ORDER BY t.timestamp DESC")
    List<TelemetryData> findLatestByDevice(@Param("device") Device device, Pageable pageable);
    
    /**
     * Belirli cihazın en son telemetri verisini bulma
     * @param device Cihaz
     * @return En son telemetri verisi (varsa)
     */
    @Query("SELECT t FROM TelemetryData t WHERE t.device = :device ORDER BY t.timestamp DESC")
    List<TelemetryData> findLatestByDevice(@Param("device") Device device);
    
    /**
     * Belirli zaman aralığındaki tüm telemetri verilerini bulma
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Telemetri verisi listesi
     */
    List<TelemetryData> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Belirli cihazın belirli zaman aralığındaki ortalama değerini hesaplama
     * @param device Cihaz
     * @param dataType Veri tipi
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Ortalama değer
     */
    @Query("SELECT AVG(t.valueNumeric) FROM TelemetryData t WHERE t.device = :device AND t.dataType = :dataType AND t.timestamp BETWEEN :startTime AND :endTime")
    Double findAverageValueByDeviceAndDataTypeAndTimeRange(
            @Param("device") Device device,
            @Param("dataType") String dataType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * Belirli cihazın belirli zaman aralığındaki minimum değerini bulma
     * @param device Cihaz
     * @param dataType Veri tipi
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Minimum değer
     */
    @Query("SELECT MIN(t.valueNumeric) FROM TelemetryData t WHERE t.device = :device AND t.dataType = :dataType AND t.timestamp BETWEEN :startTime AND :endTime")
    Double findMinValueByDeviceAndDataTypeAndTimeRange(
            @Param("device") Device device,
            @Param("dataType") String dataType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * Belirli cihazın belirli zaman aralığındaki maksimum değerini bulma
     * @param device Cihaz
     * @param dataType Veri tipi
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Maksimum değer
     */
    @Query("SELECT MAX(t.valueNumeric) FROM TelemetryData t WHERE t.device = :device AND t.dataType = :dataType AND t.timestamp BETWEEN :startTime AND :endTime")
    Double findMaxValueByDeviceAndDataTypeAndTimeRange(
            @Param("device") Device device,
            @Param("dataType") String dataType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * Eski telemetri verilerini silme (veri temizleme için)
     * @param cutoffTime Kesme zamanı
     * @return Silinen kayıt sayısı
     */
    @Query("DELETE FROM TelemetryData t WHERE t.timestamp < :cutoffTime")
    int deleteOldTelemetryData(@Param("cutoffTime") LocalDateTime cutoffTime);
}
