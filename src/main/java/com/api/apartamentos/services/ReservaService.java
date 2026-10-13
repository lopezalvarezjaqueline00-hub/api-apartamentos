package com.api.apartamentos.services;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.api.apartamentos.models.ReservaModel;
import com.api.apartamentos.repositories.IClienteRepository;
import com.api.apartamentos.repositories.IReservaRepository;
import com.api.apartamentos.repositories.IUnidadRentableRepository;

@Service
public class ReservaService extends BaseCrudService<ReservaModel> {
    private final IReservaRepository reservaRepository;
    private final IUnidadRentableRepository unidadRepository;
    private final IClienteRepository clienteRepository;
    public ReservaService(IReservaRepository repository, IUnidadRentableRepository unidadRepository, IClienteRepository clienteRepository) {
        super(repository);
        reservaRepository = repository;
        this.unidadRepository = unidadRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public ReservaModel save(ReservaModel reserva) {
        if (reserva.getUnidad() == null || reserva.getUnidad().getIdUnidad() == null) throw new IllegalArgumentException("La unidad es obligatoria");
        if (reserva.getCliente() == null || reserva.getCliente().getId() == null) throw new IllegalArgumentException("El cliente es obligatorio");
        // Se cargan la unidad y el cliente reales: el JSON solo trae sus IDs y se necesita el precio por noche
        reserva.setUnidad(unidadRepository.findById(reserva.getUnidad().getIdUnidad()).orElseThrow(() -> new IllegalArgumentException("La unidad no existe")));
        reserva.setCliente(clienteRepository.findById(reserva.getCliente().getId()).orElseThrow(() -> new IllegalArgumentException("El cliente no existe")));
        if (reserva.getFechaEntrada() == null || reserva.getFechaSalida() == null || !reserva.getFechaSalida().isAfter(reserva.getFechaEntrada())) throw new IllegalArgumentException("El rango de fechas no es válido");
        if (!"CANCELADA".equals(reserva.getEstado()) && reservaRepository.existeSolapamiento(reserva.getUnidad().getIdUnidad(), reserva.getIdReservacion(), reserva.getFechaEntrada(), reserva.getFechaSalida())) throw new IllegalArgumentException("La unidad ya está reservada en ese rango");
        if (reserva.getImporteTotal() == null && reserva.getUnidad().getPrecioNoche() != null) {
            // Se cuentan noches por fecha de calendario (entrada 15:00 y salida 12:00 de 3 días después = 3 noches)
            long noches = Math.max(1, ChronoUnit.DAYS.between(reserva.getFechaEntrada().toLocalDate(), reserva.getFechaSalida().toLocalDate()));
            reserva.setImporteTotal(reserva.getUnidad().getPrecioNoche().multiply(BigDecimal.valueOf(noches)));
        }
        return reservaRepository.save(reserva);
    }
}
