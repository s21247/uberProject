package com.example.app.User;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/customer")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<?> getCustomerById(){
        Optional<UserEntity> customer = userService.getCustomerInformation();
        return customer.map(userEntity -> new ResponseEntity<>(userEntity, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteCustomerInformation(){
        userService.DeleteCustomerInformation();
        return ResponseEntity.ok().build();
    }

    @PutMapping()
    public ResponseEntity<?> updateCustomerInformation(
        @RequestBody UpdateInformationRequest request
    ) {
        userService.updateCustomerInformation(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(request);
        return ResponseEntity.ok().build();
    }
}
