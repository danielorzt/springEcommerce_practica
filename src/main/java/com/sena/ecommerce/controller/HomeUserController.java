package com.sena.ecommerce.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sena.ecommerce.model.Producto;
import com.sena.ecommerce.service.IProductoService;

@Controller
@RequestMapping("/") // la raiz del proyecto
public class HomeUserController {

	// Instancia de LOGGER para ver datos por consola
	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(HomeUserController.class);

	// Instancia de objeto - servicio
	@Autowired
	private IProductoService productoService;

	// Metodo que mapea la vista de usuario en la raiz del proyecto
	@GetMapping("")
	public String home(Model model) {
		model.addAttribute("productos", productoService.findAll());
		return "usuario/home";
	}

	// Metodo que carga el producto de usuario con el id
	@GetMapping("productoHome/{id}")
	public String productoHome(@PathVariable Integer id, Model model) {
		LOGGER.info("ID producto enviado como parametro {}", id);
		// Variable de clase producto
		Producto p = new Producto();
		// objeto de tipo optional
		Optional<Producto> op = productoService.get(id);
		p = op.get();
		// Enviar a la vista con el model los detalles del producto con el id
		model.addAttribute("producto", p);
		return "usuario/productoHome";
	}
}
