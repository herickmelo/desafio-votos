package com.example.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

/**
 * Representa um associado na votação
 * @author Herick
 *
 */

@Table
@Entity
public class Associado extends AbstractEntity{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Cada associado possui um identificador
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ASSOCIADO")
	@SequenceGenerator(name = "SEQ_ASSOCIADO", sequenceName = "seq_associado", allocationSize = 1)
	@Expose(serialize = true)
	private Long id;

	/**
	 * Cada associado tem direito a um voto
	 */
	private Boolean voto;
	
    @ManyToOne
    @JoinColumn(name="pauta_id", nullable=false)
    private Pauta pauta;
	
	/**
	 * CONSTRUTORES
	 */
	public Associado() {
		super();
	}

	public Associado(Long id, boolean vote) {
		super();
		this.id = id;
		this.voto = vote;
	}

	/**
	 * GETTERS AND SETTERS
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean isVoto() {
		return voto;
	}

	public void setVoto(Boolean voto) {
		this.voto = voto;
	}

	public Pauta getPauta() {
		return pauta;
	}

	public void setPauta(Pauta pauta) {
		this.pauta = pauta;
	}
}
