package gov.milove.main.controller.impl;

import gov.milove.main.controller.AboutCommunityController;
import gov.milove.main.domain.About;
import gov.milove.main.repository.mongo.AboutRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AboutCommunityControllerImpl implements AboutCommunityController {

    private final AboutRepo aboutRepo;

    @Override
    @GetMapping("/aboutCommunity")
    public About getPage() {
        return aboutRepo.findById("663f6e32388652544c7ff08f").orElseThrow(EntityNotFoundException::new);
    }

    @Override
    @PutMapping("/protected/aboutCommunity/update")
    public String getUpdatePage(String mainText) {
        About about = aboutRepo.findById("663f6e32388652544c7ff08f").orElseThrow(EntityNotFoundException::new);
        about.setMainText(mainText);
        aboutRepo.save(about);
        return about.getId();
    }

}
