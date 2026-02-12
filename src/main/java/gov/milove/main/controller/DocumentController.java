package gov.milove.main.controller;

import gov.milove.main.dto.DocumentWithGroupDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Document controller")
@RequestMapping("/api")
public interface DocumentController {

    @Operation(
            summary = "Update document title",
            description = "Updates the title of an existing document by its ID.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                            description = "The ID of the document to update"),
                    @Parameter(name = "name", in = ParameterIn.QUERY, required = true,
                            description = "The new title of the document", example = "Updated Contract")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Document title updated successfully",
                            content = @Content(schema = @Schema(type = "integer", format = "int64"))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Document not found")
            }
    )
    Long updateDocumentName(Long id, @NotBlank String name);

    @Operation(summary = "Delete document")
    ResponseEntity<Void> deleteDocument(Long id);

    @Operation(summary = "Search a document")
    List<DocumentWithGroupDto> searchDocs(String encodedString);

    @Operation(summary = "Add a document view",
            description = "Should be invoked when the document with the id specified is viewed for the sake of analytics"
    )
    void markAsViewed(@PathVariable Long id);
}
