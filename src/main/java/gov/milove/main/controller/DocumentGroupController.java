package gov.milove.main.controller;

import gov.milove.main.domain.Document;
import gov.milove.main.dto.DocumentGroupWithGroupsDtoAndDocumentsDto;
import gov.milove.main.dto.response.DocumentGroupDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Tag(name = "Document group controller")
public interface DocumentGroupController {

    @Operation(summary = "Get the document list")
    List<DocumentGroupDto> findAll();

    @Operation(summary = "Create a subgroup of documents")
    DocumentGroupWithGroupsDtoAndDocumentsDto createNewSubGroup(Long groupId, @NotBlank String name);

    @Operation(summary = "Edit a subgroup of documents")
    Long editSubGroup(Long id, @NotBlank String name);

    @Operation(summary = "Delete a subgroup of documents")
    ResponseEntity<Void> deleteSubGroup(Long id);

    @Operation(
            summary = "Create a new document",
            description = "Uploads a new document to the specified document group.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Document created successfully",
                            content = @Content(schema = @Schema(implementation = Document.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "404", description = "Document group not found")
            }
    )
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                    description = "ID of the document group"),
            @Parameter(name = "file", description = "The file to upload", required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary"))),
            @Parameter(name = "title", description = "Title of the document", required = true,
                    schema = @Schema(type = "string", example = "My Contract")),
    })
    Document newDoc(Long id, MultipartFile file, String title, Principal principal);

    @Operation(summary = "Find document group by id")
    DocumentGroupWithGroupsDtoAndDocumentsDto findById(Long id);
}
