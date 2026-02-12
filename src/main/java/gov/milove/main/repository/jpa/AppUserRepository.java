package gov.milove.main.repository.jpa;

import gov.milove.main.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, String> {

    Optional<AppUser> findByEmail(String email);

    void deleteAllByEmail(String email);
}
