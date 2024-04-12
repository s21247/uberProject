package com.example.app.GeoIP;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

@Service
@RequiredArgsConstructor
public class GeoIPLocationService implements GeoIPLocationRepository{

    private final DatabaseReader databaseReader;
    private static final String UNKNOWN = "UNKNOWN";

    /**
     * get user position by ip address
     *
     * @param ip String ip address
     * @return UserPositionDTO model
     * @throws IOException     if local database city not exist
     * @throws GeoIp2Exception if cannot get info by ip address
     */
    @Override
    public GeoIP getIpLocation(String ip, HttpServletRequest request) throws IOException, GeoIp2Exception {
        GeoIP position = new GeoIP();
        String location;

        InetAddress ipAddress = InetAddress.getByName(ip);

        CityResponse cityResponse = databaseReader.city(ipAddress);
        if(cityResponse != null && cityResponse.getCity() != null) {

            String continent = (cityResponse.getContinent() != null) ? cityResponse.getContinent().getName() : "";
            String country = (cityResponse.getCountry() != null) ? cityResponse.getCountry().getName() : "";

            location = String.format("%s, %s, %s", continent, country, cityResponse.getCity().getName());
            position.setCity(cityResponse.getCity().getName());
            position.setFullLocation(location);
            position.setLatitude((cityResponse.getLocation() != null) ? cityResponse.getLocation().getLatitude() : 0);
            position.setLongitude((cityResponse.getLocation() != null) ? cityResponse.getLocation().getLongitude() : 0);
            position.setIpAddress(ip);
        }
        return position;
    }
}
