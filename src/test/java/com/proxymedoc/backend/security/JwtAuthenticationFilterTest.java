package com.proxymedoc.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateUserWithGrantedAuthorityFromJwt() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        ReflectionTestUtils.setField(filter, "tokenProvider", tokenProvider);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/pharmacies/me");
        when(request.getHeader("Authorization")).thenReturn("Bearer jwt-token");
        when(tokenProvider.validateToken("jwt-token")).thenReturn(true);
        when(tokenProvider.getUserIdFromToken("jwt-token")).thenReturn(7L);
        when(tokenProvider.getEmailFromToken("jwt-token")).thenReturn("pharma@example.com");
        when(tokenProvider.getRoleFromToken("jwt-token")).thenReturn("PHARMACIE");

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertTrue(authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_PHARMACIE")));
        verify(filterChain).doFilter(request, response);
    }
}
