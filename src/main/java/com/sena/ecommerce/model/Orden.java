package com.sena.ecommerce.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ordenes")
public class Orden {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String numero;
	private Date fehchacreacion;
	private Date fehcharecibida;
	private Double total;

	@ManyToOne
	private Usuario usuario;

	@OneToMany(mappedBy = "orden")
	private List<DetalleOrden> detalle;

	public Orden() {

	}

	public Orden(Integer id, String numero, Date fehchacreacion, Date fehcharecibida, Double total) {
		super();
		this.id = id;
		this.numero = numero;
		this.fehchacreacion = fehchacreacion;
		this.fehcharecibida = fehcharecibida;
		this.total = total;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getFehchacreacion() {
		return fehchacreacion;
	}

	public void setFehchacreacion(Date fehchacreacion) {
		this.fehchacreacion = fehchacreacion;
	}

	public Date getFehcharecibida() {
		return fehcharecibida;
	}

	public void setFehcharecibida(Date fehcharecibida) {
		this.fehcharecibida = fehcharecibida;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "Orden [id=" + id + ", numero=" + numero + ", fehchacreacion=" + fehchacreacion + ", fehcharecibida="
				+ fehcharecibida + ", total=" + total + "]";
	}

}
