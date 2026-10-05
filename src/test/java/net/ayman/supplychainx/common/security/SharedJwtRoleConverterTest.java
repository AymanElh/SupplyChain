package net.ayman.supplychainx.common.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SharedJwtRoleConverterTest {

    private final SharedJwtRoleConverter converter = new SharedJwtRoleConverter();

    @Test
    @DisplayName("Should expand ADMIN role to all system authorities")
    void shouldExpandAdminRoleToAllSystemAuthorities() {
        Jwt jwt = new Jwt(
                "token_val",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of("realm_access", Map.of("roles", List.of("offline_access", "ADMIN")))
        );

        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).isNotNull();

        Set<String> authorityNames = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertThat(authorityNames).contains(
                "ROLE_ADMIN",
                "ROLE_RESPONSABLE_ACHATS",
                "ROLE_SUPERVISEUR_LOGISTIQUE",
                "ROLE_CHEF_PRODUCTION",
                "ROLE_SUPERVISEUR_PRODUCTION",
                "ROLE_PLANIFICATEUR",
                "ROLE_GESTIONNAIRE_COMMERCIAL",
                "ROLE_SUPERVISEUR_LIVRAISONS",
                "ROLE_GESTIONNAIRE_APPROVISIONNEMENT"
        );
    }

    @Test
    @DisplayName("Should not expand non-admin role")
    void shouldNotExpandNonAdminRole() {
        Jwt jwt = new Jwt(
                "token_val",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of("realm_access", Map.of("roles", List.of("RESPONSABLE_ACHATS")))
        );

        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).isNotNull();

        Set<String> authorityNames = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertThat(authorityNames).containsExactly("ROLE_RESPONSABLE_ACHATS");
    }

    @Test
    @DisplayName("Should return empty list when realm_access is missing")
    void shouldReturnEmptyListWhenRealmAccessIsMissing() {
        Jwt jwt = new Jwt(
                "token_val",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of("sub", "user-123")
        );

        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).isEmpty();
    }
}
