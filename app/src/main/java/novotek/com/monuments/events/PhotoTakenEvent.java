/*
 * PhotoTakenEvent.java
 * Monuments-Android
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */


package novotek.com.monuments.events;

public class PhotoTakenEvent {
    public String imageName;
    public String currentPhotoPath;
    public String amazonUrl;
    public boolean sendingToAmazon;

    public PhotoTakenEvent() {}

    public PhotoTakenEvent(String imageName, String path) {
        this.imageName = imageName;
        this.currentPhotoPath = path;
    }

    public PhotoTakenEvent(String imageName, String path, String amazon, boolean sendingToAmazon) {
        this.imageName = imageName;
        this.currentPhotoPath = path;
        this.amazonUrl = amazon;
        this.sendingToAmazon = sendingToAmazon;
    }

}
