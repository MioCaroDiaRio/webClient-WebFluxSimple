package com.example.imagecontainer.repository;

import com.example.imagecontainer.entity.Employee;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface EmployeeRepository extends R2dbcRepository<Employee, Long> {
    Mono<Employee> findById(Long id);
}