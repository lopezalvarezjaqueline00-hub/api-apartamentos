package com.api.apartamentos.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.api.apartamentos.models.PropiedadImagenModel;

// spring.jpa.open-in-view=false: las relaciones LAZY se cargan aquí con @EntityGraph
// para que Jackson pueda convertirlas a JSON sin LazyInitializationException.
public interface IPropiedadImagenRepository extends JpaRepository<PropiedadImagenModel, Long> {
    @Override
    @EntityGraph(attributePaths = "propiedad")
    List<PropiedadImagenModel> findAll();

    @Override
    @EntityGraph(attributePaths = "propiedad")
    Optional<PropiedadImagenModel> findById(Long id);

    @EntityGraph(attributePaths = "propiedad")
    List<PropiedadImagenModel> findByPropiedad_IdPropiedad(Long idPropiedad);
}
