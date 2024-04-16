package com.example.app.GeoIP;

import com.example.app.User.UserEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Location")
public class GeoIP {

    @Id
    @Column(name = "customer_id")
    private Long id;

    private String ipAddress;
    private String device;
    private String city;
    private String fullLocation;
    private Double latitude;
    private Double longitude;
    private boolean isActive;

    @OneToOne
    @MapsId
    @JoinColumn(name = "customer_id")
    @JsonManagedReference
    private UserEntity customer;

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }
}
