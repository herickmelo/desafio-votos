package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Associado;
import com.example.demo.repository.AssociadoRepository;

@Service
public class AssociadoService extends GenericService<Associado>{
	
	@Autowired
	public AssociadoRepository repository;
	
	public List<Associado> findByPautaId(Long idPauta){
		Optional<List<Associado>> optionalList = repository.findByPautaId(idPauta);
		if (optionalList.isPresent()) {		
			return optionalList.get();
		}
		return null;
	}

}
