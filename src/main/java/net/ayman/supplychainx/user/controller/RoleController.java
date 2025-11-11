package net.ayman.supplychainx.user.controller;

import net.ayman.supplychainx.common.security.RequiredRole;
import net.ayman.supplychainx.user.model.Role;
import net.ayman.supplychainx.user.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @RequiredRole({"ADMIN"})
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @RequiredRole({"ADMIN"})
    public ResponseEntity<Role> getById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(roleService.findById(id), HttpStatus.OK);
    }

    @PostMapping
//    @RequiredRole({"ADMIN"})
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return new ResponseEntity<>(roleService.createRole(role), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @RequiredRole({"ADMIN"})
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role updatedRole) {
        return new ResponseEntity<>(roleService.updateRole(id, updatedRole), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @RequiredRole({"ADMIN"})
    public void deleteRole(@PathVariable("id") Long id) {
        roleService.delete(id);
    }
}

