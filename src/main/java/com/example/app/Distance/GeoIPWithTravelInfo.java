package com.example.app.Distance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeoIPWithTravelInfo {
    private Long id;
    private Double latitude;
    private Double longitude;
    private Double duration;
    private Double snapped_distance;
}
