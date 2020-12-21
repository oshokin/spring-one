package ru.oshokin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

//@Configuration
//@PropertySource("classpath:application.properties")
public class JavaConfig {

    @Value("${frames.count}")
    private int count;

    @Bean
    public CameraRoll cameraRoll() {
        return new BWCameraRoll(count);
    }

    @Bean
    public Camera camera() {
        return new Camera(cameraRoll());
    }
}
