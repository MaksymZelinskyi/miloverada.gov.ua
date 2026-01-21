package gov.milove.main.service.impl;

import gov.milove.main.domain.AppUser;
import gov.milove.main.domain.Document;
import gov.milove.main.domain.DocumentGroup;
import gov.milove.main.domain.MongoDocument;
import gov.milove.main.exception.DocumentNotFoundException;
import gov.milove.main.exception.ServiceException;
import gov.milove.main.exception.ValidationException;
import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.repository.jpa.DocumentGroupRepository;
import gov.milove.main.repository.jpa.DocumentRepository;
import gov.milove.main.repository.mongo.MongoDocumentRepo;
import gov.milove.main.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Log4j2
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    private final MongoDocumentRepo mongoDocumentRepo;

    private final DocumentGroupRepository groupRepository;

    private final AppUserRepository appUserRepository;

    @Override
    public void deleteById(Long id) {
        log.info("Delete document by id {}", id);
        Document document = documentRepository.findById(id).orElseThrow(
                () -> new DocumentNotFoundException("Document with id: %s not found".formatted(id)));

        delete(document);
    }

    @Override
    public Document saveDocument(Long groupId, MultipartFile file, String title, String userId) {
        try {
            validateDocumentFile(file);
            Optional<Document> documentOpt = documentRepository.findByHashCode(Arrays.hashCode(file.getBytes()));
            log.info("save or get document with filename - {}", file.getOriginalFilename());
            if (documentOpt.isPresent()) {
                log.info("document already exists");
                Document document = documentOpt.get();
                document.setTitle(title);
                document.setName(file.getOriginalFilename());
                addToGroupAndReturn(documentOpt.get(), groupId);
                return document;
            }

            return save(groupId, file, title, userId);
        } catch (IOException e) {
            log.info("Document save error: {}", e.getMessage());
            throw new ServiceException("Document save error", e);
        }
    }

    private void validateDocumentFile(MultipartFile file) {
        String message = "Invalid document file. ";
        if (file.getSize() == 0) {
            throw new ValidationException(message + "File is empty");
        }

        if (isNull(file.getOriginalFilename())) {
            throw new ValidationException(message + "File is not defined or not available");
        }

        if (file.getOriginalFilename().isEmpty()) {
            throw new ValidationException(message + "File name is empty");
        }
    }

    private void addToGroupAndReturn(Document document, Long groupId) {
        DocumentGroup group = groupRepository.findById(groupId).orElseThrow(EntityNotFoundException::new);
        group.getDocuments().add(document);
        groupRepository.save(group);
    }

    public void delete(Document document) {
        if (!documentRepository.documentUsedMoreThenOneTime(document.getName())) {
            log.info("Document: {}", document.getName());

            document.setDocumentGroup(null);

            if (document.getMongoId() != null) {
                log.info("Mongo id is not null - {}", document.getMongoId());
                documentRepository.deleteById(document.getId());
                mongoDocumentRepo.deleteById(document.getMongoId());
                return;
            }
            log.info("Mongo id is null, delete by filename - {}", document.getName());
            mongoDocumentRepo.deleteByFilename(document.getName());

        } else {
            log.info("Document with id: {} used more than one time, record only will be deleted",
                    document.getId());
            documentRepository.deleteById(document.getId());
        }
    }

    @Override
    public void deleteAll(List<Document> documents) {
        log.info("Delete documents with id: {}",
                documents.stream().map(Document::getId).toList());

        documents.forEach(this::delete);
    }


    private Document save(Long groupId, MultipartFile file, String title, String userId) {
        try {
            byte[] bytes = file.getBytes();

            MongoDocument mongoDocument = new MongoDocument(file.getOriginalFilename(), new Binary(bytes), file.getContentType());
            MongoDocument savedMongo = mongoDocumentRepo.save(mongoDocument);
            log.info("document saved to mongo = {}", savedMongo);
            AppUser user = null;
            Optional<AppUser> userOptional = appUserRepository.findById(userId);
            if (userOptional.isPresent()) {
                user = userOptional.get();
            } else {
                log.error("Current user not found");
            }

            Document document = Document.builder()
                    .mongoId(savedMongo.getId())
                    .documentGroup(groupRepository.getReferenceById(groupId))
                    .name(file.getOriginalFilename())
                    .title(title)
                    .hashCode(Arrays.hashCode(bytes))
                    .addedBy(user)
                    .build();
            Document savedDoc = documentRepository.save(document);
             log.info("Document saved - {}", document);
            return savedDoc;
        } catch (IOException e) {
            log.info("Document save error: {}", e.getMessage());
            throw new ServiceException("Document save error", e);
        }
    }

    public Document getById(Long id) {
        return documentRepository.findById(id).orElseThrow(() -> new DocumentNotFoundException("Document with id " + id + " not found"));
    }

    public Document getByName(String name) {
        return documentRepository.findByName(name).orElseThrow(() -> new DocumentNotFoundException("Document with name " + name + "not found"));
    }
}
