package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DetallesFabricacionId implements Serializable {
    private static final long serialVersionUID = 4812962585707042868L;

    @Column(name = "fabricacion_id", nullable = false)
    private Long fabricacionId;

    @Column(name = "componente_id", nullable = false)
    private Long componenteId;

    // Constructor vacío
    public DetallesFabricacionId() {}

    // Constructor con parámetros
    public DetallesFabricacionId(Long fabricacionId, Long componenteId) {
        this.fabricacionId = fabricacionId;
        this.componenteId = componenteId;
    }

    public Long getFabricacionId() {
        return fabricacionId;
    }

    public void setFabricacionId(Long fabricacionId) {
        this.fabricacionId = fabricacionId;
    }

    public Long getComponenteId() {
        return componenteId;
    }

    public void setComponenteId(Long componenteId) {
        this.componenteId = componenteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetallesFabricacionId)) return false;
        DetallesFabricacionId that = (DetallesFabricacionId) o;
        return Objects.equals(fabricacionId, that.fabricacionId) &&
                Objects.equals(componenteId, that.componenteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fabricacionId, componenteId);
    }
}

