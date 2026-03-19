package hs.wdp.app.gd.meta.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import hs.wdp.app.gd.meta.entity.DhMetaSchema;
import hs.wdp.app.gd.meta.entity.DhMetaSchemaId;

public interface DhMetaSchemaRepository extends JpaRepository<DhMetaSchema, DhMetaSchemaId> {
    List<DhMetaSchema> findAllById(DhMetaSchemaId Id);
}
