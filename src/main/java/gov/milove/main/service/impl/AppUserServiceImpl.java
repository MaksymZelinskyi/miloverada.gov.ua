package gov.milove.main.service.impl;

import gov.milove.main.domain.AppUser;
import gov.milove.main.exception.AppUserNotFoundException;
import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository repository;

    public AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof OAuth2User oauthUser) {
            String email = oauthUser.getAttribute("email");
            String googleId = oauthUser.getAttribute("sub");
            Optional<AppUser> user = repository.findById(googleId);
            if (user.isPresent()) {
                return user.get();
            }
            else {
                return repository.findByEmail(email).orElseThrow(() -> new AppUserNotFoundException("Current user not found"));
            }
        } else {
            throw new AppUserNotFoundException("Current user not found");
        }
    }

}