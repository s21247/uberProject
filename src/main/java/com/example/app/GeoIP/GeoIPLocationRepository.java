package com.example.app.GeoIP;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public interface GeoIPLocationRepository {
    GeoIP getIpLocation(String ip, HttpServletRequest request) throws IOException, GeoIp2Exception;
}
