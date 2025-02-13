package com.sena.ecommerce.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sena.ecommerce.model.DetalleOrden;
import com.sena.ecommerce.model.Orden;
import com.sena.ecommerce.model.Producto;
import com.sena.ecommerce.model.Usuario;
import com.sena.ecommerce.service.IDetalleOrdenService;
import com.sena.ecommerce.service.IOrdenService;
import com.sena.ecommerce.service.IProductoService;
import com.sena.ecommerce.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/") // la raiz del proyecto
public class HomeUserController {

	// Instancia de LOGGER para ver datos por consola
	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(HomeUserController.class);

	// Instancia de objeto - servicio
	@Autowired
	private IProductoService productoService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;

	@Autowired
	private IDetalleOrdenService detalleOrdenService;

	// List y Orden son dos variables propias de esta clase
	// Lista de detalles de la orden para guardarlos en la db
	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();

	// objeto que almacena los datos de la orden
	Orden orden = new Orden();

	// Metodo que mapea la vista de usuario en la raiz del proyecto
	@GetMapping("")
	public String home(Model model, HttpSession session) {
		LOGGER.info("Sesion de usuario: {}", session.getAttribute("idUsuario"));
		model.addAttribute("productos", productoService.findAll());
		// variable de sesion
		model.addAttribute("sesion", session.getAttribute("idUsuario"));
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

	// Metodo para enviar del boton prductoHome al carrito
	@PostMapping("/cart")
	public String addCart(@RequestParam Integer id, @RequestParam Double cantidad, Model model) {
		DetalleOrden detaorden = new DetalleOrden();
		Producto p = new Producto();
		// Variable que siempre que este en el metodo inicializa en 0 despues de cada
		// compra
		double sumaTotal = 0;
		Optional<Producto> op = productoService.get(id);
		LOGGER.info("Producto añadido: {}", op.get());
		LOGGER.info("Cantidad añadida", cantidad);
		p = op.get();
		detaorden.setCantidad(cantidad);
		detaorden.setPrecio(p.getPrecio());
		detaorden.setNombre(p.getNombre());
		detaorden.setTotal(p.getPrecio() * cantidad);
		detaorden.setProducto(p);
		// validacion para evidar duplicados de productos
		Integer idProducto = p.getId();
		// funcion lamda string y funcion anonima con predicado anyMatch
		boolean insertado = detalles.stream().anyMatch(prod -> prod.getProducto().getId() == idProducto);
		// si no es true añade el producto
		if (!insertado) {
			// detalles
			detalles.add(detaorden);
		}

		// suma de totales de la lista que el usuario añada al carrito
		// funcion de java8 lamda stream
		// funcion de java8 anonima dt
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		// pasar variables a la vista
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		return "usuario/carrito";
	}

	// metodo para quitar productos del carrito
	@GetMapping("/delete/cart/{id}")
	public String deleteProductoCart(@PathVariable Integer id, Model model) {
		// Lista nueva de producto
		List<DetalleOrden> ordenesNuevas = new ArrayList<DetalleOrden>();
		// quitar objeto de la lista de detalleorden
		for (DetalleOrden detalleOrden : detalles) {
			if (detalleOrden.getProducto().getId() != id) {
				ordenesNuevas.add(detalleOrden);

			}
		}
		// Poner la nueva lista con los productos restantes del carrito
		detalles = ordenesNuevas;
		// recalcular los productos del carrito
		double sumaTotal = 0;
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		// pasar variables a la vista
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		return "usuario/carrito";
	}

	// metodo para redirijir al carrito sin producto
	@GetMapping("/getCart")
	public String getCart(Model model, HttpSession session) {
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		return "/usuario/carrito";
	}

	// Este es el metodo para pasar a la vista del resumen de la orden
	@GetMapping("/order")
	public String order(Model model, HttpSession session) {
		Usuario u = usuarioService.findById(Integer.parseInt(session.getAttribute("idUsuario").toString())).get();
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("usuario", u);
		return "usuario/resumenorden";
	}

	@GetMapping("/saveOrder")
	public String saveOrder(HttpSession session) {
		// Guardar orden
		Date fechaCreacion = new Date();
		orden.setFechacreacion(fechaCreacion);
		orden.setNumero(ordenService.generarNumeroOrden());
		// Usuario que se referenci en esa compra previamente logeado
		Usuario u = usuarioService.findById(Integer.parseInt(session.getAttribute("idUsuario").toString())).get();
		orden.setUsuario(u);
		ordenService.save(orden);
		// Guardar detalles de la orden
		for (DetalleOrden dt : detalles) {
			dt.setOrden(orden);
			detalleOrdenService.save(dt);
		}
		// Limpiar valores que no se añadan a la orden recien guardada
		orden = new Orden();
		detalles.clear();
		return "redirect:/";
	}

	// Metodo post para buscar productos en la vista del home de usuarios
	@PostMapping("/search")
	public String searchProducto(@RequestParam String nombre, Model model) {
		LOGGER.info("nombre del producto: {}", nombre);
		List<Producto> productos = productoService.findAll().stream()
				.filter(p -> p.getNombre().toUpperCase().contains(nombre.toUpperCase())).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		return "usuario/home";
	}

}
