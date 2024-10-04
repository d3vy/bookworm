package com.telegrambot.forfedos.repositories;

import com.telegrambot.forfedos.models.BankService;
import com.telegrambot.forfedos.models.DigitalService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankServiceRepository extends CrudRepository<BankService, Integer> {
    Optional<BankService> findByName(String name);
}
