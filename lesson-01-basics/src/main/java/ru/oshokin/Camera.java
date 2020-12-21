package ru.oshokin;

import org.springframework.stereotype.Component;

@Component
public class Camera {

    private final CameraRoll cr;

    public Camera(CameraRoll cr) {
        this.cr = cr;
    }

    public void doPhoto() {
        System.out.println("Say cheese, motherfucker!");
        cr.processImage();
    }

}
