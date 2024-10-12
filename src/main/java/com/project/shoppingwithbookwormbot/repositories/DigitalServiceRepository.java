package com.project.shoppingwithbookwormbot.repositories;

import com.project.shoppingwithbookwormbot.models.DigitalService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DigitalServiceRepository extends CrudRepository<DigitalService, Integer> {
    Optional<DigitalService> findByName(String name);
}
