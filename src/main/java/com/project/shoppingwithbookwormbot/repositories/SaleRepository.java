package com.project.shoppingwithbookwormbot.repositories;

import com.project.shoppingwithbookwormbot.models.Sale;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaleRepository extends CrudRepository<Sale, Integer> {
    Optional<Sale> findByName(String name);
}
