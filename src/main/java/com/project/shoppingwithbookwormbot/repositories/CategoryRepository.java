package com.project.shoppingwithbookwormbot.repositories;

import com.project.shoppingwithbookwormbot.models.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {
}
