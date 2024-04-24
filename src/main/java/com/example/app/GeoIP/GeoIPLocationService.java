package com.example.app.GeoIP;

import com.example.app.User.UserEntity;
import com.example.app.User.UserRepository;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeoIPLocationService implements GeoIPLocationRepository{

    private final DatabaseReader databaseReader;
    private final UserRepository userRepository;
    private final GeoIPRepository geoIPRepository;

    /**
     * get user position by ip address
     *
     * @param ip String ip address
     * @return UserPositionDTO model
     * @throws IOException     if local database city not exist
     * @throws GeoIp2Exception if you cannot get info by ip address
     */
    @Transactional
    @Override
    public GeoIPEntity getIpLocation(Long id, String ip, HttpServletRequest request) throws IOException, GeoIp2Exception {
        GeoIPEntity position = null;
        String location;
        var optionalUser = userRepository.findById(id);

        InetAddress ipAddress = InetAddress.getByName(ip);

        CityResponse cityResponse = databaseReader.city(ipAddress);
        if(cityResponse != null && cityResponse.getCity() != null) {

            String continent = (cityResponse.getContinent() != null) ? cityResponse.getContinent().getName() : "";
            String country = (cityResponse.getCountry() != null) ? cityResponse.getCountry().getName() : "";

            location = String.format("%s, %s, %s", continent, country, cityResponse.getCity().getName());
            if (optionalUser.isPresent()) {
                UserEntity user = optionalUser.get();
                if(user.getGeoIPEntity() != null) {
                    position = user.getGeoIPEntity();
                }else {
                    position = new GeoIPEntity();
                }
                position.setCity(cityResponse.getCity().getName());
                position.setFullLocation(location);
                double latitude = (cityResponse.getLocation() != null) ? cityResponse.getLocation().getLatitude() : 0;
                double longitude = (cityResponse.getLocation() != null) ? cityResponse.getLocation().getLongitude() : 0;
                latitude = BigDecimal.valueOf(latitude).setScale(2, RoundingMode.HALF_UP).doubleValue();
                longitude = BigDecimal.valueOf(longitude).setScale(2, RoundingMode.HALF_UP).doubleValue();
                position.setLatitude(latitude);
                position.setLongitude(longitude);
                position.setIpAddress(ip);
                user.setGeoIPEntity(position);
                position.setCustomer(user);
            }
        }

        return position;
    }

    public GeoIPEntity getDriverById(Long id) {
        var optionalGeoIP = geoIPRepository.findById(id);
        GeoIPEntity geoIP = null;
        if (optionalGeoIP.isPresent()) {
             geoIP = optionalGeoIP.get();
        }
        return geoIP;
    }

    @Override
    public void updateIsActive(Long id) {
        var optionalGeoIP = geoIPRepository.findLocationById(id);

        if(optionalGeoIP.isPresent()){
            GeoIPEntity geoIP = optionalGeoIP.get();
            geoIP.setActive(!geoIP.isActive());
            geoIPRepository.save(geoIP);
        }
    }

    public List<GeoIPEntity> findAllActiveUsers() {
        return geoIPRepository.findAllActiveClientUsers();
    }

    public List<GeoIPEntity> findAllActiveDriverUsers() {
        return geoIPRepository.findAllActiveDriverUsers();
    }
}
