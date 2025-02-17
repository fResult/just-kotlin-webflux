package dev.fResult.justKotlinWebflux.controllers

import dev.fResult.justKotlinWebflux.entities.Employee
import dev.fResult.justKotlinWebflux.repositories.EmployeeRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.net.URI

@RestController
@RequestMapping("/api/employees")
class EmployeeController(private val employeeRepository: EmployeeRepository) {
  @GetMapping
  fun all(): Mono<ResponseEntity<List<Employee>>> = employeeRepository.findAll()
    .collectList().map { ResponseEntity.ok(it) }

  @GetMapping("/{id}")
  fun byId(@PathVariable id: Long): Mono<ResponseEntity<Employee>> = employeeRepository.findById(id)
    .map {
      ResponseEntity.ok(it)
    }
    .switchIfEmpty { Mono.just(ResponseEntity.notFound().build()) }

  @PostMapping
  fun create(body: Mono<Employee>): Mono<ResponseEntity<Employee>> =
    body.doOnNext(employeeRepository::save)
      .map { ResponseEntity.created(URI.create("/api/employees/${it.id}")).body(it) }
}
