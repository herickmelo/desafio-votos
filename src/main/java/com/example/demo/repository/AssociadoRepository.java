package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Associado;

/**
 * 
 * @author Herick
 *
 */
@Repository
public interface AssociadoRepository extends GenericRepository<Associado> {
	
	public Optional<List<Associado>> findByPautaId(Long idPauta);
}
