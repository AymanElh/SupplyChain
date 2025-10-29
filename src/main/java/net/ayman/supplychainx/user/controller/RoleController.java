package net.ayman.supplychainx.user.controller;

import jakarta.servlet.http.HttpSession;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.user.model.Role;
import net.ayman.supplychainx.user.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        String userRole = (String) session.getAttribute("userRole");

        Map<String, String> error = new HashMap<>();
        if (userEmail == null) {
            error.put("error", "unauthorized");
            error.put("status", "401");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(error);
        }

        if(!userRole.equals("ADMIN")) {
            error.put("error", "Forbidden");
            error.put("status", "403");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(roleService.findById(id), HttpStatus.OK);
    }

    @PostMapping
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

