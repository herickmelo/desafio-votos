package com.example.demo.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

/**
 * Representa uma pauta de votação
 * @author Herick
 *
 */
@Table
@Entity
public class Pauta extends AbstractEntity{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PAUTA")
	@SequenceGenerator(name = "SEQ_PAUTA", sequenceName = "seq_pauta", allocationSize = 1)
	@Expose(serialize = true)
	private Long id;


	/**
	 * Descrição da pauta em votação
	 */
	@JsonProperty("pautaVotacao")
	private String pautaVotacao;
	
	private Boolean sessaoAberta;
	
	/**
	 * CONSTRUTORES
	 */
	public Pauta() {
		super();
		this.sessaoAberta = false;
	}

	public Pauta(Long id, String pautaVotacao, List<Associado> listaDeAssociados) {
		super();
		this.id = id;
		this.pautaVotacao = pautaVotacao;
		this.sessaoAberta = false;
	}



	/**
	 * GETTERS AND SETTERS
	 */
	public String getPautaVotação() {
		return pautaVotacao;
	}

	public void setPautaVotação(String pautaVotação) {
		this.pautaVotacao = pautaVotação;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getSessaoAberta() {
		return sessaoAberta;
	}

	public void setSessaoAberta(Boolean sessaoAberta) {
		this.sessaoAberta = sessaoAberta;
	}
}
