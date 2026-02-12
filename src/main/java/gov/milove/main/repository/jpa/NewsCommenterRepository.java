package gov.milove.main.repository.jpa;

import gov.milove.main.domain.NewsCommenter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsCommenterRepository extends JpaRepository<NewsCommenter, String> {
}
