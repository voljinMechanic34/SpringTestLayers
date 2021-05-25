package ru.digitalhabbits.homework3.web;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.model.PersonResponse;
import ru.digitalhabbits.homework3.service.PersonService;

import java.net.URI;
import java.util.List;

@Tag(name = "Person operations")
@RestController
@RequestMapping(value = "/api/v1/persons", consumes = "application/json", produces = "application/json")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @ApiResponse(responseCode = "200", description = "Get all persons")
    @GetMapping
    public List<PersonResponse> persons() {
        return personService.findAllPersons();
    }

    @ApiResponse(responseCode = "200", description = "Get person info by Id")
    @GetMapping("/{id}")
    public PersonResponse person(@PathVariable Integer id) {
        return personService.getPerson(id);
    }

    @ApiResponse(responseCode = "201", description = "Create new person")
    @PostMapping
    public ResponseEntity<Void> createPerson(@RequestBody PersonRequest request) {
        final Integer id = personService.createPerson(request);
        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @ApiResponse(responseCode = "200", description = "Update existing person")
    @PatchMapping("/{id}")
    public PersonResponse updatePerson(@PathVariable Integer id, @RequestBody PersonRequest request) {
        return personService.updatePerson(id, request);
    }

    @ApiResponse(responseCode = "204", description = "Delete person")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletePerson(@PathVariable Integer id) {
        personService.deletePerson(id);
    }
}
