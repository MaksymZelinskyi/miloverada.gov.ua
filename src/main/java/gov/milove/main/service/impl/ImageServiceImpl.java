package gov.milove.main.service.impl;

import com.mongodb.client.gridfs.model.GridFSFile;
import gov.milove.main.exception.ImageNotFoundException;
import gov.milove.main.exception.ImageProcessingException;
import gov.milove.main.exception.ValidationException;
import gov.milove.main.service.ImageService;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Liashenko Andrii
 * @since 2/26/2025
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ImageServiceImpl implements ImageService {

  @Autowired
  private GridFsTemplate gridFsTemplate;

  @Autowired
  private GridFsOperations gridFsOperations;

  /**
   * Saves image file
   * @param file image
   * @return mongo id
   */
  @Override
  public String saveImage(MultipartFile file) {
    String filename = file.getOriginalFilename();
    try {

      ObjectId objectId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(),
          file.getContentType());
      log.info("Image with name: {}, saved with id: {}", filename, objectId.toHexString());
      return objectId.toHexString();

    } catch (IOException e) {
      log.error("Error occupied during image saving", e);
      throw new ImageProcessingException("Failed to process image file with name: %s"
          .formatted(filename));
    }
  }

  /**
   * Gets image from storage as a resource
   * @param id image id
   * @return mongo resource
   */
  @Override
  public GridFsResource getImage(String id) {
    GridFSFile gridFSFile = gridFsTemplate.findOne(
        new org.springframework.data.mongodb.core.query.Query(
            org.springframework.data.mongodb.core.query.Criteria.where("_id").is(id)
        )
    );

    if (Objects.isNull(gridFSFile)) {
      log.warn("Image not found, id: {}", id);
      return null;
    }

    return gridFsOperations.getResource(gridFSFile);
  }

  @Override
  public void deleteImage(String imageUrl) {
    String imageId = parseImageId(imageUrl);

    gridFsTemplate.delete(new Query(
        Criteria.where("_id").is(imageId)
    ));
  }

  /**
   * Parses id from url
   * <p>
   *  Example: /getimage/432 - method returns '432'
   * </p>
   * @param url image download url
   * @return image id
   */
  private String parseImageId(String url) {
    String id = url.substring(url.lastIndexOf("/") + 1);

    if (!ObjectId.isValid(id)) {
      throw new ValidationException("Image id in url is not valid, url %s".formatted(url));
    }

    return id;
  }
}
