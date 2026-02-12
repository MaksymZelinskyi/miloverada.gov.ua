package gov.milove.main.service;

import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Liashenko Andrii
 * @since 2/26/2025
 */
public interface ImageService {

   /**
    * Saves image to storage
    * @param imageFile multipart file
    * @return Image id
    */
   String saveImage(MultipartFile imageFile);

   GridFsResource getImage(String id);

   void deleteImage(String imageUrl);
}
