package net.ayman.supplychainx.common.security.local;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import net.ayman.supplychainx.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Profile({"dev", "test"})
public class LocalJwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    public String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        List<String> roles;
        String roleName = (user.getRole() != null && user.getRole().getName() != null)
                ? user.getRole().getName().toUpperCase()
                : "GUEST";

        if ("ADMIN".equals(roleName)) {
            roles = List.of(
                    "ADMIN",
                    "GESTIONNAIRE_COMMERCIAL",
                    "SUPERVISEUR_LIVRAISONS",
                    "CHEF_PRODUCTION",
                    "SUPERVISEUR_PRODUCTION",
                    "PLANIFICATEUR",
                    "RESPONSABLE_ACHATS",
                    "SUPERVISEUR_LOGISTIQUE",
                    "GESTIONNAIRE_APPROVISIONNEMENT"
            );
        } else if ("COMMERCIAL".equals(roleName) || "GESTIONNAIRE_COMMERCIAL".equals(roleName)) {
            roles = List.of("GESTIONNAIRE_COMMERCIAL");
        } else if ("PRODUCTION".equals(roleName) || "CHEF_PRODUCTION".equals(roleName)) {
            roles = List.of("CHEF_PRODUCTION", "SUPERVISEUR_PRODUCTION", "PLANIFICATEUR");
        } else if ("LOGISTIQUE".equals(roleName) || "LOGISTICS".equals(roleName) || "SUPERVISEUR_LOGISTIQUE".equals(roleName)) {
            roles = List.of("SUPERVISEUR_LOGISTIQUE", "SUPERVISEUR_LIVRAISONS", "RESPONSABLE_ACHATS");
        } else {
            roles = List.of(roleName);
        }

        Map<String, Object> realmAccess = Map.of("roles", roles);

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer("supplychainx-local")
                .claim("email", user.getEmail())
                .claim("name", user.getEmail())
                .claim("realm_access", realmAccess)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }
}
