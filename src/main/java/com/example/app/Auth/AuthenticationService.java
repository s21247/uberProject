package com.example.app.Auth;

import com.example.app.Config.JwtService;
import com.example.app.RoleAndPrivilege.RoleRepository;
import com.example.app.RoleAndPrivilege.Roles;
import com.example.app.User.UserEntity;
import com.example.app.User.UserRepository;
import com.example.app.Validation.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ValidationService validationService;
    private final RoleRepository roleRepository;

    public AuthenticationResponse register(RegisterRequest request){
        Optional<UserEntity> customerByEmail = userRepository.findCustomerByEmail(request.getEmail());
        if (customerByEmail.isPresent()){
            throw new IllegalArgumentException("email taken");
        }
        if (!validationService.isValidEmail(request.getEmail())){
            throw new IllegalArgumentException("invalid email");
        }
        if (!validationService.isValidPhoneNumber(request.getPhoneNumber())){
            throw new IllegalArgumentException("invalid phone number");
        }

        UserEntity user = UserEntity.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phoneNumber(request.getPhoneNumber())
                    .isDriver(request.isDriver())
                    .build();
        if (request.isDriver()) {
            user.setRoles(Arrays.asList(roleRepository.findByName(Roles.ROLE_DRIVER.name())));
        }else {
            user.setRoles(Arrays.asList(roleRepository.findByName(Roles.ROLE_CLIENT.name())));
        }

        userRepository.save(user);
        return generateToken(user);
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findCustomerByEmail(request.getEmail())
                .orElseThrow(() -> new AccessDeniedException("User not found"));
        return generateToken(user);

    }

    private AuthenticationResponse generateToken(UserEntity user){
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
