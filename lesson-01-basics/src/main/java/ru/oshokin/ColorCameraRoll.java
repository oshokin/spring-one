package ru.oshokin;

import org.springframework.stereotype.Component;

@Component
public class ColorCameraRoll implements CameraRoll {

    @Override
    public void processImage() {
        System.out.println("Yeah, zoomer right here!");
    }

}
