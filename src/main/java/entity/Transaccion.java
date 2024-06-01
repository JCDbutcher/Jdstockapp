package entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import utils.ShippingStatusConverter;

import java.time.LocalDate;

@Entity
@Table(name = "transaccion")
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "fecha_pedido")
    private LocalDate fechaPedido;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @ColumnDefault("0")
    @Column(name = "esPerfecto", nullable = false)
    private Boolean esPerfecto = false;

    @Convert(converter = ShippingStatusConverter.class)
    @Column(name = "estado_envio")
    private ShippingStatus estadoEnvio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Cliente getCliente() {
        return cliente;
    }
    public String getClienteNombre() {
        return cliente != null ? cliente.getNombre() : "Unknown";
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDate fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Boolean getEsPerfecto() {
        return esPerfecto;
    }

    public void setEsPerfecto(Boolean esPerfecto) {
        this.esPerfecto = esPerfecto;
    }

    public ShippingStatus getEstadoEnvio() {
        return estadoEnvio;
    }

    public void setEstadoEnvio(ShippingStatus estadoEnvio) {
        this.estadoEnvio = estadoEnvio;
    }

}