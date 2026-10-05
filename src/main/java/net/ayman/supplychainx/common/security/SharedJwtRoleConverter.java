package net.ayman.supplychainx.common.security;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SharedJwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    public static final List<String> ALL_ROLES = List.of(
            "ADMIN",
            "GESTIONNAIRE_APPROVISIONNEMENT",
            "RESPONSABLE_ACHATS",
            "SUPERVISEUR_LOGISTIQUE",
            "CHEF_PRODUCTION",
            "SUPERVISEUR_PRODUCTION",
            "PLANIFICATEUR",
            "GESTIONNAIRE_COMMERCIAL",
            "SUPERVISEUR_LIVRAISONS"
    );

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || realmAccess.get("roles") == null) return List.of();

        List<String> roles = (List<String>) realmAccess.get("roles");

        log.debug("Roles from JWT: {}", roles);

        Set<String> upperRoles = roles.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (upperRoles.contains("ADMIN") || upperRoles.contains("ROLE_ADMIN")) {
            upperRoles.addAll(ALL_ROLES);
        }

        return upperRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .collect(Collectors.toList());
    }
}

