package com.reiniciar.blogpessoal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.reiniciar.blogpessoal.model.Postagem;

@Repository
public interface PostagemRepository extends JpaRepository<Postagem, Long>{

	public List <Postagem> findAllByTituloContainingIgnoreCase(@Param("titulo")
	String titulo);

	public Optional <Postagem> findByTitulo(String titulo);

}
