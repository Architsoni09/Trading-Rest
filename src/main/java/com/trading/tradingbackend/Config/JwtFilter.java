package com.trading.tradingbackend.Config;

import com.trading.tradingbackend.Exceptions.AuthenticationFailedException;
import com.trading.tradingbackend.Service.UserDetailService;
import com.trading.tradingbackend.Model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailService userDetailService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        if (servletPath.contains("/auth")
                || servletPath.contains("/users/forgot-password/")
                || servletPath.contains("/users/reset-password/otp-verification/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String jwt = header.substring(7);  // Extract token after "Bearer "
                String userEmail = jwtService.extractUserNameFromToken(jwt);

                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailService.loadUserByUsername(userEmail);
                    User user = (User) userDetails;  // Cast to User to access custom fields

                    if (user.isActivated()) {
                        if (!user.getTwoFactorAuth().getTwoFactorEnabled()
                                || (user.getTwoFactorAuth().getTwoFactorEnabled()
                                && user.getTwoFactorAuth().getIsUserVerified())) {

                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        } else {
                            throw new AuthenticationFailedException("Two-factor authentication is not verified.");
                        }
                    } else {
                        throw new AuthenticationFailedException("User is not activated.");
                    }
                }
            }
        } catch (Exception e) {
            throw new AuthenticationFailedException("Authentication failed: " + e.getMessage()+ e);
        }

        filterChain.doFilter(request, response);
    }
}
