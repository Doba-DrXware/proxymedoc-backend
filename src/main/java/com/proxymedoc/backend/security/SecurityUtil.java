package com.proxymedoc.backend.security;

import com.proxymedoc.backend.model.Utilisateur;
import com.proxymedoc.backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility component to extract current authenticated user from security context
 */
@Component
public class SecurityUtil {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * Get current authenticated user's email from security context
     */
    public String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String) {
            return (String) principal;
        }
        return null;
    }

    /**
     * Get current authenticated user entity
     */
    public Utilisateur getCurrentUser() {
        String email = getCurrentUserEmail();
        if (email != null) {
            return utilisateurRepository.findByEmail(email).orElse(null);
        }
        return null;
    }

    /**
     * Get current user ID from request attributes (set by JWT filter)
     */
    public Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String) {
            Utilisateur user = utilisateurRepository.findByEmail((String) principal).orElse(null);
            if (user != null) {
                return user.getId();
            }
        }
        return null;
    }
}
