package com.api.apartamentos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.api.apartamentos.models.ClienteModel;
import com.api.apartamentos.models.PropiedadModel;
import com.api.apartamentos.repositories.IClienteRepository;
import com.api.apartamentos.repositories.IPropiedadRepository;

@Service
public class PropiedadService extends BaseCrudService<PropiedadModel> {

    private final IPropiedadRepository propiedadRepository;
    private final IClienteRepository clienteRepository;

    public PropiedadService(IPropiedadRepository propiedadRepository, IClienteRepository clienteRepository) {
        super(propiedadRepository);
        this.propiedadRepository = propiedadRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<PropiedadModel> getAllPropiedades() {
        return findAll();
    }

    public Optional<PropiedadModel> getPropiedadById(Long id) {
        return findById(id);
    }

    // Guarda la propiedad validando que el propietario exista (se carga el cliente real desde la BD)
    public PropiedadModel savePropiedad(PropiedadModel propiedad) {
        if (propiedad.getPropietario() == null || propiedad.getPropietario().getId() == null) {
            throw new IllegalArgumentException("El propietario es obligatorio");
        }
        ClienteModel propietario = clienteRepository.findById(propiedad.getPropietario().getId())
                .orElseThrow(() -> new IllegalArgumentException("El propietario no existe"));
        propiedad.setPropietario(propietario);
        return save(propiedad);
    } 
    
    public void deletePropiedad(Long id) {
        deleteById(id);
    }

    // ACTIVIDAD: Recuperar propiedades por tipo
    public List<PropiedadModel> getPropiedadesByTipo(String tipo) {
        return propiedadRepository.findByTipoIgnoreCase(tipo);
    }

    // ACTIVIDAD: Recuperar todas las propiedades de un propietario
    public List<PropiedadModel> getPropiedadesByPropietario(Long idPropietario) {
        return propiedadRepository.findByPropietario_Id(idPropietario);
    }
}
