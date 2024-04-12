package com.example.app.GeoIP;

import lombok.Data;

@Data
public class GeoIP {

    private String ipAddress;
    private String device;
    private String city;
    private String fullLocation;
    private Double latitude;
    private Double longitude;
}
