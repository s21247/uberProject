package com.example.app.RoleAndPrivilege;

import com.example.app.User.UserEntity;
import com.example.app.User.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;
    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup)
            return;
        final PrivilegeEntity canDrive = createPrivilegeIfNotFound(Privileges.CAN_RIDE.name());
        final PrivilegeEntity canBookARide = createPrivilegeIfNotFound(Privileges.CAN_BOOK_RIDE.name());

        final List<PrivilegeEntity> clientPrivileges = new ArrayList<>(Arrays.asList(canBookARide));
        final List<PrivilegeEntity> driverPrivileges = new ArrayList<>(Arrays.asList(canBookARide, canDrive));
        final RolesEntity driverRole = createRoleIfNotFound(Roles.ROLE_DRIVER.name(), driverPrivileges);
        createRoleIfNotFound(Roles.ROLE_CLIENT.name(), clientPrivileges);

        createUserIfNotFound("test", "test", "test@test.com",
                "test", "123123123", new ArrayList<>(Arrays.asList(driverRole)));
        alreadySetup = true;
    }

    @Transactional
    public PrivilegeEntity createPrivilegeIfNotFound(String name) {
        PrivilegeEntity privilege= privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new PrivilegeEntity(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    public RolesEntity createRoleIfNotFound(String name, Collection<PrivilegeEntity> privileges) {
        RolesEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RolesEntity(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }

    @Transactional
    public void createUserIfNotFound(final String firstName, final String lastName, final String email,
                                           final String password, final String phoneNumber, final Collection<RolesEntity> roles) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            user = UserEntity.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .phoneNumber(phoneNumber)
                    .build();
        }
        user.setRoles(roles);
        userRepository.save(user);
    }

}
