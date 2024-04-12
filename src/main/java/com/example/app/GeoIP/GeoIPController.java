package com.example.app.GeoIP;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/location")
public class GeoIPController {

    private final GeoIPLocationService geoIPLocationService;

    //todo In production mode, extract the IP address from HttpServletRequest without passing it as a parameter
    @GetMapping("/geoIP/{ipAddress}")
    public GeoIP getLocation (@PathVariable String ipAddress, HttpServletRequest request)
        throws IOException, GeoIp2Exception {
        return geoIPLocationService.getIpLocation(ipAddress,request);
    }
}
