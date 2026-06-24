package cl.vetnova.agenda.repository;

import cl.vetnova.agenda.model.Box;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoxRepository extends JpaRepository<Box, Long> {

}