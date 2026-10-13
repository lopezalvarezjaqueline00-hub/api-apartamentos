package com.api.apartamentos.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.api.apartamentos.models.PropiedadModel;

// spring.jpa.open-in-view=false: las relaciones LAZY se cargan aquí con @EntityGraph
// para que Jackson pueda convertirlas a JSON sin LazyInitializationException.
public interface IPropiedadRepository extends JpaRepository<PropiedadModel, Long> {
    @Override
    @EntityGraph(attributePaths = "propietario")
    List<PropiedadModel> findAll();

    @Override
    @EntityGraph(attributePaths = "propietario")
    Optional<PropiedadModel> findById(Long id);

    // ACTIVIDAD: Recuperar propiedades por tipo (Departamento, Casa, Loft...) sin importar mayúsculas
    @EntityGraph(attributePaths = "propietario")
    List<PropiedadModel> findByTipoIgnoreCase(String tipo);

    // ACTIVIDAD: Recuperar todas las propiedades de un propietario (cliente)
    @EntityGraph(attributePaths = "propietario")
    List<PropiedadModel> findByPropietario_Id(Long idPropietario);
}
