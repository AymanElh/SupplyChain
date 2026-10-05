package net.ayman.supplychainx.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class RoleHierarchyConfig {

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMIN").implies(
                        "GESTIONNAIRE_APPROVISIONNEMENT",
                        "RESPONSABLE_ACHATS",
                        "SUPERVISEUR_LOGISTIQUE",
                        "CHEF_PRODUCTION",
                        "SUPERVISEUR_PRODUCTION",
                        "PLANIFICATEUR",
                        "GESTIONNAIRE_COMMERCIAL",
                        "SUPERVISEUR_LIVRAISONS"
                )
                .build();
    }
}
