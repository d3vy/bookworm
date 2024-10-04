package com.telegrambot.forfedos.repositories;

import com.telegrambot.forfedos.models.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentsRepository extends CrudRepository<Document, Integer> {

    Optional<Document> findByName(String name);
}
