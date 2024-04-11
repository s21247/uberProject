package com.example.app.RoleAndPrivilege;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "privileges")
public class PrivilegeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    public PrivilegeEntity(final String name) {
        this.name = name;
    }

    @ManyToMany(mappedBy = "privileges")
    private Collection<RolesEntity> roles;

}
