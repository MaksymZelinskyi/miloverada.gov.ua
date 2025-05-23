package gov.milove.main.service.impl;

import gov.milove.main.domain.Document;
import gov.milove.main.domain.DocumentGroup;
import gov.milove.main.domain.MongoDocument;
import gov.milove.main.dto.DocumentWithGroupDto;
import gov.milove.main.dto.request.SaveDocumentRequestDto;
import gov.milove.main.exception.ServiceException;
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

@Service
@RequiredArgsConstructor
@Log4j2
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    private final MongoDocumentRepo mongoDocumentRepo;

    private final DocumentGroupRepository groupRepository;

    @Override
    public Document saveDocument(SaveDocumentRequestDto request) {
        Optional<Document> documentOpt = documentRepository.findByHashCode(request.file().hashCode());
        log.info("save or get document with filename - {}", request.file().getOriginalFilename());
        if (documentOpt.isPresent()) {
            log.info("document already exists");
            Document document = documentOpt.get();
            document.setTitle(request.title());
            document.setName(request.file().getOriginalFilename());
            addToGroupAndReturn(documentOpt.get(), request.groupId());
            return document;
        }

        return save(request);
    }

    private void addToGroupAndReturn(Document document, Long groupId) {
        DocumentGroup group = groupRepository.findById(groupId).orElseThrow(EntityNotFoundException::new);
        group.getDocuments().add(document);
        groupRepository.save(group);
    }

    @Override
    public void delete(Document document) {
        if (!documentRepository.documentUsedMoreThenOneTime(document.getName())) {
            log.info("delete document = {}", document);
            if (document.getMongoId() != null) {
                log.info("mongo id not null - {}", document.getMongoId());
                documentRepository.delete(document);
                mongoDocumentRepo.deleteById(document.getMongoId());
                return;
            }
            log.info("mongo is null, delete by filename - {}", document.getName());
            mongoDocumentRepo.deleteByFilename(document.getName());

        } else log.info("document used more than one time = {}", document);
    }

    @Override
    public void deleteAll(List<Document> documents) {
        for (Document document : documents) {
            delete(document);
        }
    }

    @Override
    public Document getDocument(Long id) {
        return documentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Document saveDocument(Document document) {
        return documentRepository.save(document);
    }

    @Override
    public List<DocumentWithGroupDto> searchDocument(String encodedString) {
        return documentRepository.searchDistinctByNameContainingIgnoreCaseOrTitleContainingIgnoreCase(encodedString, encodedString);
    }

    private Document save(SaveDocumentRequestDto request) {
        try {
            log.info("a document doesn't exist");
            byte[] bytes = request.file().getBytes();

            MongoDocument mongoDocument = new MongoDocument(request.file().getOriginalFilename(),
                    new Binary(bytes), request.file().getContentType());
            MongoDocument savedMongo = mongoDocumentRepo.save(mongoDocument);
            log.info("document saved to mongo = {}", savedMongo);

            Document document = Document.builder()
                    .mongoId(savedMongo.getId())
                    .documentGroup(groupRepository.getReferenceById(request.groupId()))
                    .name(request.file().getOriginalFilename())
                    .title(request.title())
                    .hashCode(Arrays.hashCode(bytes))
                    .build();
            Document savedDoc = documentRepository.save(document);
            log.info("a document is saved - {}", document);
            return savedDoc;
        } catch (IOException e) {
            log.info(e.getMessage());
            throw new ServiceException(e);
        }
    }

}
