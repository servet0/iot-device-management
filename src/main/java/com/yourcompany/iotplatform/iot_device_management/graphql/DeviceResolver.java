package com.yourcompany.iotplatform.iot_device_management.graphql;

import com.yourcompany.iotplatform.iot_device_management.model.Device;
import com.yourcompany.iotplatform.iot_device_management.model.TelemetryData;
import com.yourcompany.iotplatform.iot_device_management.service.TelemetryDataService;
import graphql.kickstart.tools.GraphQLResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GraphQL Device Resolver
 * Device tipi için GraphQL resolver işlemleri
 */
@Component
public class DeviceResolver implements GraphQLResolver<Device> {
    
    @Autowired
    private TelemetryDataService telemetryDataService;
    
    /**
     * Cihazın telemetri verilerini getirme
     * @param device Cihaz
     * @return Cihazın telemetri verileri
     */
    public List<TelemetryData> telemetryData(Device device) {
        return telemetryDataService.getTelemetryDataByDevice(device.getId(), 10); // Son 10 veri
    }
}
