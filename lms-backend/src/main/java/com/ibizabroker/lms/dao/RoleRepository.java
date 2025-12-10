package com.ibizabroker.lms.dao;

import com.ibizabroker.lms.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findFirstByRoleName(String roleName); // ⭐ required method

}
