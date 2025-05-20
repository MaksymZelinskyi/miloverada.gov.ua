package gov.milove.main.controller;

import gov.milove.main.domain.ContactEmployee;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="Contact employee controller")
@RestController
public interface ContactEmployeeController {

    @Operation(summary = "Get the available employees")
    List<ContactEmployee> getAll();

    @Operation(summary = "Add an employee")
    ContactEmployee create(@RequestBody ContactEmployee employee);

    @Operation(summary = "Update an employee")
    Long update(
            @RequestParam("id") Long id,
            @RequestBody ContactEmployee updatedEmployee);

    @Operation(summary = "Delete an employee")
    Long delete(@PathVariable Long id);
}
