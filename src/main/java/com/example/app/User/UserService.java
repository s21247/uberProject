package com.example.app.User;

import com.example.app.Validation.ValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ValidationService validationService;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserEntity> getCustomerInformation(){
        var user = findAuthenticatedUser();

        if (user == null) {
            throw new IllegalArgumentException("Customer does not exist");
        }
        return userRepository.findById(user.getId());
    }


    @Transactional
    public void updateCustomerInformation(UpdateInformationRequest request){
        var user = findAuthenticatedUser();

        if (request.getEmail() != null && validationService.isValidEmail(request.getEmail())
                && !Objects.equals(user.getEmail(), request.getEmail())){
            Optional<UserEntity> customerByEmail = userRepository.findCustomerByEmail(request.getEmail());
            if (customerByEmail.isPresent()){
                throw new IllegalArgumentException("email taken");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPhoneNumber() != null && validationService.isValidPhoneNumber(request.getPhoneNumber())
                && !Objects.equals(user.getPhoneNumber(), request.getPhoneNumber())){
            Optional<UserEntity> customerByPhoneNumber = userRepository.findCustomerByPhoneNumber(request.getPhoneNumber());
            if (customerByPhoneNumber.isPresent()){
                throw new IllegalArgumentException("phone number taken");
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getFirstName() != null && !Objects.equals(user.getFirstName(), request.getFirstName())) {
            user.setFirstName(request.getFirstName());
        }

        if(request.getLastName() != null && !Objects.equals(user.getLastName(), request.getLastName())){
            user.setLastName(request.getLastName());
        }

    }

    public void changePassword(ChangePasswordRequest request) {
        var user = findAuthenticatedUser();

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void DeleteCustomerInformation(){
        var user = findAuthenticatedUser();
        if (user == null){
            throw new IllegalArgumentException("Customer does not exist");
        }
        userRepository.deleteById(user.getId());
    }

    private UserEntity findAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail);
    }
}
