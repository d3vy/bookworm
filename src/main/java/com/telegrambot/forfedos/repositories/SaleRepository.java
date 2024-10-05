package com.telegrambot.forfedos.repositories;

import com.telegrambot.forfedos.models.OtherService;
import com.telegrambot.forfedos.models.Sale;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaleRepository extends CrudRepository<Sale, Integer> {
    Optional<Sale> findByName(String name);
}
