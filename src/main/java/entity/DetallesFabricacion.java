package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "detalles_fabricacion")
public class DetallesFabricacion {
    @EmbeddedId
    private DetallesFabricacionId id= new DetallesFabricacionId();

    @MapsId("fabricacionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fabricacion_id", nullable = false)
    private Fabricacion fabricacion;

    @MapsId("componenteId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "componente_id", nullable = false)
    private Producto componente;

    @Column(name = "cantidad_utilizada", nullable = false)
    private Integer cantidadUtilizada;

    public DetallesFabricacionId getId() {
        return id;
    }

    public void setId(DetallesFabricacionId id) {
        this.id = id;
    }

    public Fabricacion getFabricacion() {
        return fabricacion;
    }

    public void setFabricacion(Fabricacion fabricacion) {
        this.fabricacion = fabricacion;
    }

    public Producto getComponente() {
        return componente;
    }

    public void setComponente(Producto componente) {
        this.componente = componente;
    }

    public Integer getCantidadUtilizada() {
        return cantidadUtilizada;
    }

    public void setCantidadUtilizada(Integer cantidadUtilizada) {
        this.cantidadUtilizada = cantidadUtilizada;
    }
}
