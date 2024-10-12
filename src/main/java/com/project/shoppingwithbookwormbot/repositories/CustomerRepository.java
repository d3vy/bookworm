package com.project.shoppingwithbookwormbot.repositories;

import com.project.shoppingwithbookwormbot.models.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
