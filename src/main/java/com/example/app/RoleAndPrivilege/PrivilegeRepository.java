package com.example.app.RoleAndPrivilege;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Long> {

    PrivilegeEntity findByName(String name);

}
