package gov.milove.main.service.impl;

import gov.milove.main.domain.AppUser;
import gov.milove.main.exception.AppUserNotFoundException;
import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository repository;

    public AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken) {
            String email = ((JwtAuthenticationToken) auth).getToken().getClaim("email");
            return repository.findByEmail(email).orElseThrow(() -> new AppUserNotFoundException("Current user not found"));
        }
        return repository.findByEmail(auth.getName()).orElseThrow(() -> new AppUserNotFoundException("Current user not found"));
    }

}