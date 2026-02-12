package gov.milove.main.service.unittest;

import gov.milove.main.domain.AppUser;
import gov.milove.main.domain.LinkBanner;
import gov.milove.main.dto.AppUserDto;
import gov.milove.main.dto.LinkBannerDto;
import gov.milove.main.dto.request.LinkBannerCreateRequest;
import gov.milove.main.dto.request.LinkBannerUpdateRequest;
import gov.milove.main.exception.LinkBannerNotFoundException;
import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.repository.jpa.LinkBannerRepository;
import gov.milove.main.service.ImageService;
import gov.milove.main.service.impl.LinkBannerServiceImpl;
import gov.milove.main.util.mapper.LinkBannerMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.models.links.Link;
import org.flywaydb.core.internal.jdbc.TableLockingExecutionTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkBannerServiceUnitTest {

  private static final String EXAMPLE_URL = "https://www.linkedin.com";
  private static final String EXAMPLE_TEXT = "Mock text";

  @Mock
  private LinkBannerRepository linkBannerRepository;

  @Mock
  private LinkBannerMapper linkBannerMapper;

  @Mock
  private AppUserRepository appUserRepository;

  @Mock
  private ImageService imageService;

  @InjectMocks
  private LinkBannerServiceImpl underTest;

  @Test
  @DisplayName("Should return all banners sorted by createdOn descending")
  void shouldReturnAllBannersSorted() {
    List<LinkBanner> expectedBanners = List.of(new LinkBanner(), new LinkBanner());
    when(linkBannerRepository.findAll(Sort.by("createdOn").descending())).thenReturn(
            expectedBanners);

    Page<LinkBannerDto> actualBanners = underTest.findAllBanners(Pageable.ofSize(10));

    assertIterableEquals(expectedBanners, actualBanners);
    verify(linkBannerRepository).findAll(Sort.by("createdOn").descending());
  }

  @Test
  @DisplayName("Should save link banner when valid banner provided")
  void shouldSaveLinkBanner() {
    MockMultipartFile imageFile = new MockMultipartFile(
            "imageFile",
            "banner.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "Mock image content".getBytes()
    );

    AppUser user = new AppUser();
    LinkBanner linkBanner = new LinkBanner();
    LinkBannerCreateRequest linkBannerCreateRequest = new LinkBannerCreateRequest(EXAMPLE_URL, EXAMPLE_TEXT, EXAMPLE_URL, imageFile);
    LinkBannerDto dto = new LinkBannerDto(1L, EXAMPLE_URL, EXAMPLE_TEXT, EXAMPLE_TEXT, LocalDateTime.now(), LocalDate.now(), mock(AppUserDto.class));
    when(linkBannerRepository.save(any(LinkBanner.class))).thenReturn(linkBanner);
    when(appUserRepository.findById("user123")).thenReturn(Optional.of(user));
    when(imageService.saveImage(any(MultipartFile.class))).thenReturn("image1");
    when(linkBannerMapper.toLinkBanner(any(LinkBannerCreateRequest.class))).thenReturn(linkBanner);
    when(linkBannerMapper.toLinkBannerDto(any(LinkBanner.class))).thenReturn(dto);

    LinkBannerDto savedBanner = underTest.save(linkBannerCreateRequest, "user123");

    assertNotNull(savedBanner);
    verify(linkBannerRepository).save(linkBanner);
  }

  @Nested
  @DisplayName("Update Link Banner")
  class UpdateLinkBanner {

    private final LinkBannerUpdateRequest request = new LinkBannerUpdateRequest(1L,
        EXAMPLE_TEXT, EXAMPLE_URL, LocalDateTime.now(), LocalDate.now());

    @Test
    @DisplayName("Should update link banner when valid update request provided")
    void shouldUpdateLinkBanner() {
      LinkBanner linkBanner = new LinkBanner();
      LinkBannerDto dto = new LinkBannerDto(1L, EXAMPLE_URL, EXAMPLE_URL, EXAMPLE_TEXT, LocalDateTime.now(), LocalDate.now(), mock(AppUserDto.class));
      when(linkBannerRepository.findById(request.id())).thenReturn(Optional.of(linkBanner));
      when(linkBannerRepository.save(any(LinkBanner.class))).thenReturn(linkBanner);
      when(linkBannerMapper.toLinkBannerDto(any(LinkBanner.class))).thenReturn(dto);
      LinkBannerDto updatedBanner = underTest.update(request);

      assertNotNull(updatedBanner);
      verify(linkBannerMapper).updateLinkBannerFromDto(request, linkBanner);
      verify(linkBannerRepository).save(linkBanner);
    }

    @Test
    @DisplayName("Should throw exception when banner not found")
    void shouldThrowExceptionWhenBannerNotFound() {
      when(linkBannerRepository.findById(request.id())).thenReturn(Optional.empty());

      assertThrows(LinkBannerNotFoundException.class, () -> underTest.update(request));
      verify(linkBannerRepository, never()).save(any(LinkBanner.class));
    }
  }

  @Nested
  @DisplayName("Delete Link Banner")
  class DeleteLinkBanner {

    private final Long id = 1L;

    @Test
    @DisplayName("Should delete link banner when valid id provided")
    void shouldDeleteLinkBanner() {
      LinkBanner linkBanner = new LinkBanner();
      when(linkBannerRepository.findById(id)).thenReturn(Optional.of(linkBanner));

      underTest.deleteById(id);

      verify(linkBannerRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when banner to delete not found")
    void shouldThrowExceptionWhenBannerToDeleteNotFound() {
      when(linkBannerRepository.findById(id)).thenReturn(Optional.empty());

      assertThrows(LinkBannerNotFoundException.class, () -> underTest.deleteById(id));
      verify(linkBannerRepository, never()).deleteById(any());
    }
  }
}