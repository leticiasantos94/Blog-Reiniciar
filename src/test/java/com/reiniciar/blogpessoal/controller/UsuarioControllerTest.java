package com.reiniciar.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.reiniciar.blogpessoal.model.Usuario;
import com.reiniciar.blogpessoal.repository.UsuarioRepository;
import com.reiniciar.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplace;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start() {
		
		usuarioRepository.deleteAll();
	}
	
	@Test
	@Order(1)
	@DisplayName("Cadastrar Um Usuário")
	public void deveCriarUmUsuario() {
		
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(new Usuario (0L,
				"Paulo Antunes", "paulo_antunes@email.com.br", "13465278", "http://i.imgur.com/FETvs2O.jpg", "Advogado"));
		
		ResponseEntity<Usuario> resposta = testRestTemplace
				.exchange("/usuarios/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertEquals(requisicao.getBody().getNome(), resposta.getBody().getNome());
		assertEquals(requisicao.getBody().getFoto(), resposta.getBody().getFoto());
		assertEquals(requisicao.getBody().getUsuario(), resposta.getBody().getUsuario());
			
	}
	
	@Test
	@Order(2)
	@DisplayName("Não deve permirtir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {
	
		usuarioService.cadastrarUsuario(new Usuario(0L,
			"Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/NtyGeo.jpg", "Delegada"));
		
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(new Usuario(0L,
			"Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/NtyGeo.jpg", "Delegada"));
		
		ResponseEntity<Usuario> resposta = testRestTemplace
			.exchange("/usuarios/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		
	}
	
	
	@Test
	@Order(3)
	@DisplayName("Alterar um Usuário")
	public void deveAtualizarUmUsuario() {
		
		Optional<Usuario> usuarioCreate = usuarioService.cadastrarUsuario(new Usuario(0L,
				"Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", 
				"https://i.imgur.com/yDRVeK7.jpg", "Modelo"));
		
		Usuario usuarioUpdate = new Usuario(usuarioCreate.get().getId(), "Juliana Andrews Ramos",
			"juliana_ramos@email.com.br", "juliana123", "https://i.imgur.com/T12NIp9.jpg", "Modelo");
		
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> resposta = testRestTemplace
				.withBasicAuth("root", "root")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, requisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals(usuarioUpdate.getNome(), resposta.getBody().getNome());
		assertEquals(usuarioUpdate.getFoto(), resposta.getBody().getFoto());
		assertEquals(usuarioUpdate.getUsuario(), resposta.getBody().getUsuario());
	}
		
		@Test
		@Order(4)		
		@DisplayName("Listar todos os Usuários")
		public void deveMostrarTodosUsuarios() {
			
			usuarioService.cadastrarUsuario(new Usuario(0L,
					"Sabrina Sanches","sabrina_sanches@email.com.br", "sabrina123", 
					"https://i.imgur.com/EcJG8kB", "Desenvolvedora"));
			
			usuarioService.cadastrarUsuario(new Usuario(0L,
					"Ricardo Marques","ricardo_marques@email.com.br", "ricardo123", 
					"https://i.imgur.com/Sk5SjWE", "Dentista"));
			
			ResponseEntity<String> resposta = testRestTemplace
					.withBasicAuth("root", "root")
					.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
			
			assertEquals(HttpStatus.OK, resposta.getStatusCode());
				
		
	}
	
	
	
}
