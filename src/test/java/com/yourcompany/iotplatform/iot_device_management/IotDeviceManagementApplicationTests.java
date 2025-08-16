package com.yourcompany.iotplatform.iot_device_management;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Ana uygulama test sınıfı
 * Spring Boot context'inin yüklenip yüklenmediğini test eder
 */
@SpringBootTest
@ActiveProfiles("test")
class IotDeviceManagementApplicationTests {

    @Test
    void contextLoads() {
        // Spring Boot context'inin başarıyla yüklendiğini doğrular
    }
}
