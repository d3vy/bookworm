package com.telegrambot.forfedos.repositories;

import com.telegrambot.forfedos.models.Document;
import com.telegrambot.forfedos.models.OtherService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtherServiceRepository extends CrudRepository<OtherService, Integer> {
    Optional<OtherService> findByName(String name);
}
