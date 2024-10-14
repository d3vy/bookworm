package com.project.shoppingwithbookwormbot.repositories;

import com.project.shoppingwithbookwormbot.models.BankService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankServiceRepository extends CrudRepository<BankService, Integer> {
    Optional<BankService> findByName(String name);
}
