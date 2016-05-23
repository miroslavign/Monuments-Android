
/*
 * TakePhotoEvent.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 4/5/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
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
