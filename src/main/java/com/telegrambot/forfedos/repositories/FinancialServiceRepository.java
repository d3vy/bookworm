package com.telegrambot.forfedos.repositories;

import com.telegrambot.forfedos.models.FinancialService;
import com.telegrambot.forfedos.models.OtherService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinancialServiceRepository extends CrudRepository<FinancialService, Integer> {
    Optional<FinancialService> findByName(String name);
}
