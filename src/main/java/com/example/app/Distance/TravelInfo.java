package com.example.app.Distance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TravelInfo {
    private Double duration;
    private Double longitude;
    private Double latitude;
    private Double snappedDistance;
}
