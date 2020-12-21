package ru.oshokin;

import org.springframework.stereotype.Component;

@Component
public class BWCameraRoll implements CameraRoll {

    private int count;

    public BWCameraRoll(int count) {
        this.count = count;
    }

    @Override
    public void processImage() {
        System.out.println("You like retro shit, ain't ya?");
        count--;
        System.out.printf("%d slides left%n", count);
    }

}
