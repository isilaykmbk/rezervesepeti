package com.rezerve_sepeti;

public class Place {
    private String name;
    private String uuid;
    private Double latitude;
    private Double longitude;
    public Place(String name,String uuid, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.uuid = uuid;
    }

}
