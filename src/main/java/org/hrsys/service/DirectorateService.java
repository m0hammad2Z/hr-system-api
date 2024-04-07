package org.hrsys.service;

import org.hrsys.model.Directorate;
import org.hrsys.repository.DirectorateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hrsys.exception.InvalidRequestException;
import org.hrsys.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Objects;

@Service
public class DirectorateService {

    private final DirectorateRepository directorateRepository;

    public DirectorateService(DirectorateRepository directorateRepository) {
        this.directorateRepository = directorateRepository;
    }


    // create a new directorate
    @Transactional
    public Directorate createDirectorate(Directorate directorate) {
        validateDirectorate(directorate); // Validate the directorate

        // Check if a directorate with the same name already exists
        if (directorateRepository.existsByName(directorate.getName())) {
            throw new InvalidRequestException("A directorate with this name already exists.");
        }
        return directorateRepository.save(directorate);
    }

    // Get all directorates in the database
    @Transactional(readOnly = true)
    public List<Directorate> getAllDirectorates() {
        return directorateRepository.findAll();
    }

    // Get directorate by id
    @Transactional(readOnly = true)
    public Directorate getDirectorateById(Long id) {
        return directorateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Directorate", id));
    }


    @Transactional
    public Directorate updateDirectorate(Directorate directorate) {
        validateDirectorate(directorate); // Validate the directorate

        // Fetch the existing directorate from the database
        Directorate existingDirectorate = directorateRepository.findById(directorate.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Directorate", directorate.getId()));

        // Check if the provided version matches the current version
        if (!Objects.equals(directorate.getVersion(), existingDirectorate.getVersion())) {
            throw new InvalidRequestException("The provided version does not match the current version");
        }
        existingDirectorate.setName(directorate.getName()); // Update the directorate name

        // Save the updated directorate to the database
        return directorateRepository.save(existingDirectorate);
    }

    @Transactional
    public void deleteDirectorate(Long id, Long newDirectorateId) {
        if (id == null || newDirectorateId == null) {
            throw new InvalidRequestException("You must specify both the directorate");
        }

        if (id.equals(newDirectorateId)) {
            throw new InvalidRequestException("Directorate cannot be merged with itself");
        }

        // Fetch the existing directorate from the database
        Directorate existingDirectorate = directorateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Directorate", id));

        // Fetch the new directorate from the database
        Directorate newDirectorate = directorateRepository.findById(newDirectorateId)
                .orElseThrow(() -> new ResourceNotFoundException("Directorate", newDirectorateId));

        // Merge the departments of the existing directorate to the new directorate
        existingDirectorate.getDepartments().forEach(department -> department.setDirectorate(newDirectorate));

        directorateRepository.delete(existingDirectorate);
    }

    // Method to validate a directorate
    private void validateDirectorate(Directorate directorate) {
        if (directorate == null || directorate.getName() == null) {
            throw new IllegalArgumentException("Directorate or directorate name must not be null");
        }
    }

}