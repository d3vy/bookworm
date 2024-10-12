package com.project.shoppingwithbookwormbot.repositories;

import com.project.shoppingwithbookwormbot.models.OtherService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtherServiceRepository extends CrudRepository<OtherService, Integer> {
    Optional<OtherService> findByName(String name);
}
