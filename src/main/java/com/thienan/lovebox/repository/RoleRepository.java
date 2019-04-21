package com.thienan.lovebox.repository;

import com.thienan.lovebox.entity.RoleEntity;
import com.thienan.lovebox.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    RoleEntity findByName(RoleName roleName);
}
