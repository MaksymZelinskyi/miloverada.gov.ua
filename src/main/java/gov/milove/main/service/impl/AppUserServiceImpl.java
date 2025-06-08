package gov.milove.main.service.impl;

import gov.milove.main.domain.AppUser;
import gov.milove.main.exception.AppUserNotFoundException;
import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository repository;

    public AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return repository.findByEmail(username).orElseThrow(() -> new AppUserNotFoundException("Current user not found"));
    }

}
