package com.example.app.GeoIP;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/location")
public class GeoIPController {

    private final GeoIPLocationService geoIPLocationService;

    //todo In production mode, extract the IP address from HttpServletRequest without passing it as a parameter
    @GetMapping("/geoIP/{id}/{ipAddress}")
    public GeoIPEntity getLocation (@PathVariable Long id, @PathVariable String ipAddress, HttpServletRequest request)
        throws IOException, GeoIp2Exception {
        return geoIPLocationService.getIpLocation(id, ipAddress,request);
    }

    @PutMapping("/updateIsActive/{id}")
    public ResponseEntity<?> updateIsActive(@PathVariable Long id) {
        geoIPLocationService.updateIsActive(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/driver/getAllActiveClients")
    public List<GeoIPEntity> getAllActiveClientUsers() {
        return geoIPLocationService.findAllActiveUsers();
    }

    @GetMapping("/client/getAllActiveDrivers")
    public List<GeoIPEntity> getAllActiveDriverUsers() {
        return geoIPLocationService.findAllActiveDriverUsers();
    }
}
