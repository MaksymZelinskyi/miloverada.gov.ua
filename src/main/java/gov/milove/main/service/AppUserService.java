package gov.milove.main.service;

import gov.milove.main.domain.AppUser;

/**
 * The {@code AppUserService} interface provides methods of work with {@code AppUser} entities,
 * using @{AppUserRepository} for database operations
 */
public interface AppUserService {

    /**
     * Extracts from the database the entity of the current user, using the name of the principal in Security context
     * @return current user
     */
    AppUser getCurrentUser();

}
