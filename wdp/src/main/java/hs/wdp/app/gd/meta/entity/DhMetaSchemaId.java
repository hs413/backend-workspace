package hs.wdp.app.gd.meta.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class DhMetaSchemaId implements Serializable {
    private String projectId;
    private String schemaId;


}
