package gov.milove.main.controller;

import gov.milove.main.domain.Document;
import gov.milove.main.dto.DocumentGroupWithGroupsDto;
import gov.milove.main.dto.DocumentGroupWithGroupsDtoAndDocumentsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Document group controller")
@Validated
public interface DocumentGroupController {

    @Operation(summary = "Get the document list")
    List<DocumentGroupWithGroupsDto> findAll();

    @Operation(summary = "Create a subgroup of documents")
    DocumentGroupWithGroupsDtoAndDocumentsDto createNewSubGroup(@RequestParam(required = false) Long groupId, @NotBlank @RequestParam String name);

    @Operation(summary = "Edit a subgroup of documents")
    Long editSubGroup(@PathVariable Long id, @NotBlank @RequestParam String name);

    @Operation(summary = "Delete a subgroup of documents")
    Long deleteSubGroup(@PathVariable Long id);

    @Operation(summary = "Create a new document")
    Document newDoc(@PathVariable Long id, @RequestParam MultipartFile file, @RequestParam String title);

    @Operation(summary = "Find document group by id")
    DocumentGroupWithGroupsDtoAndDocumentsDto findById(@PathVariable Long id);
}
