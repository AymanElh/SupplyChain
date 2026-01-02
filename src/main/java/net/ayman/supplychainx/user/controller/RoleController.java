package net.ayman.supplychainx.user.controller;

import net.ayman.supplychainx.common.security.RequiredRole;
import net.ayman.supplychainx.user.model.Role;
import net.ayman.supplychainx.user.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Role> getById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(roleService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return new ResponseEntity<>(roleService.createRole(role), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role updatedRole) {
        return new ResponseEntity<>(roleService.updateRole(id, updatedRole), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable("id") Long id) {
        roleService.delete(id);
    }
}

