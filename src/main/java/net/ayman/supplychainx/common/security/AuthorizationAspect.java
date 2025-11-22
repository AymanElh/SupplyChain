package net.ayman.supplychainx.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.common.exception.UnauthorizedException;
import net.ayman.supplychainx.user.dto.UserResponseDTO;
import net.ayman.supplychainx.user.dto.login.LoginResponseDTO;
import net.ayman.supplychainx.user.model.User;
import net.ayman.supplychainx.user.repository.UserRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
<<<<<<< Updated upstream
@Aspect
=======
//@Aspect
>>>>>>> Stashed changes
@Component
@Order(1)
public class AuthorizationAspect {

    private final UserRepository userRepository;

    public AuthorizationAspect(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Before("@annotation(net.ayman.supplychainx.common.security.RequiredRole)")
    public void checkAuthorization(JoinPoint joinPoint) {

        log.debug("Authorization aspect triggered for method: " + joinPoint.getSignature().getName());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes == null) {
            throw new UnauthorizedException("You are not authorized to access this resource");
        }

        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new UnauthorizedException("You are not authorized to access this resource");
        }

        LoginResponseDTO currentUser = (LoginResponseDTO) session.getAttribute("currentUser");

        if (currentUser == null) {
            log.warn("No user found in session");
            throw new UnauthorizedException("Authentication required. Please login first.");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("Method signature: " + signature);
        Method method = signature.getMethod();
        log.info("Method: " + method.getName());
        RequiredRole requireRole = method.getAnnotation(RequiredRole.class);

        String[] requiredRoles = requireRole.value();

        String userRole = currentUser.getRoleName();
        boolean hasPermission = Arrays.asList(requiredRoles).contains(userRole.toUpperCase());
        hasPermission = true;
        if (!hasPermission) {
            log.warn("User {} with role {} attempted to access method requiring roles: {}",
                    currentUser.getEmail(), userRole, Arrays.toString(requiredRoles));

            throw new UnauthorizedException(
                    String.format("Access denied. Required role: %s. Your role: %s",
                            Arrays.toString(requiredRoles), userRole)
            );
        }
    }
}
