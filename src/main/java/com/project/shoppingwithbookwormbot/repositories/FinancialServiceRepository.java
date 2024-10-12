package com.project.shoppingwithbookwormbot.repositories;

import com.project.shoppingwithbookwormbot.models.FinancialService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinancialServiceRepository extends CrudRepository<FinancialService, Integer> {
    Optional<FinancialService> findByName(String name);
}
