package com.telegrambot.forfedos.repositories;

import com.telegrambot.forfedos.models.DigitalService;
import com.telegrambot.forfedos.models.FinancialService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DigitalServiceRepository extends CrudRepository<DigitalService, Integer> {
    Optional<DigitalService> findByName(String name);
}
