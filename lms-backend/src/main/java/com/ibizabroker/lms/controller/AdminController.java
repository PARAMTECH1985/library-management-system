package com.ibizabroker.lms.controller;

import com.ibizabroker.lms.dao.UsersRepository;
import com.ibizabroker.lms.dao.RoleRepository;
import com.ibizabroker.lms.entity.Users;
import com.ibizabroker.lms.entity.Role;
import com.ibizabroker.lms.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

@CrossOrigin("http://localhost:4200/")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleRepository roleRepository;    // new

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/users")
    // @PreAuthorize("hasRole('Admin')")
    public Users addUserByAdmin(@RequestBody Users user) {
        // 1) password safe encode (if provided)
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // 2) if frontend did not send role, assign default 'User'
        if (user.getRole() == null || user.getRole().isEmpty()) {
            Role defaultRole = roleRepository.findFirstByRoleName("User")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setRoleName("User");
                        return roleRepository.save(r);
                    });
            user.setRole(Collections.singleton(defaultRole));
        } else {
            // 3) If frontend sent role(s), convert transient Role -> DB-managed Role
            Set<Role> managed = new HashSet<>();
            for (Role r : user.getRole()) {
                if (r == null) continue;
                if (r.getRoleId() != null) {
                    roleRepository.findById(r.getRoleId()).ifPresent(managed::add);
                } else if (r.getRoleName() != null && !r.getRoleName().trim().isEmpty()) {
                    roleRepository.findFirstByRoleName(r.getRoleName()).ifPresent(managed::add);
                }
            }
            if (managed.isEmpty()) {
                Role defaultRole = roleRepository.findFirstByRoleName("User")
                        .orElseGet(() -> roleRepository.save(new Role(){{
                            setRoleName("User");
                        }}));
                user.setRole(Collections.singleton(defaultRole));
            } else {
                user.setRole(managed);
            }
        }

        // 4) save user (Hibernate will create entry in users and user_role)
        return usersRepository.save(user);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('Admin')")
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/users/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Integer id) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id "+ id +" does not exist."));
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('Admin')")
    @PutMapping("/users/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Integer id, @RequestBody Users userDetails) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id "+ id +" does not exist."));

        user.setName(userDetails.getName());
        user.setRole(userDetails.getRole());
        user.setUsername(userDetails.getUsername()); // fix: use username

        Users updatedUser = usersRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }
}
