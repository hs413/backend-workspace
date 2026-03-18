package hs.wdp.app.gd.meta.repository;

import hs.wdp.app.gd.meta.entity.DhMetaSchema;
import hs.wdp.app.gd.meta.entity.DhMetaSchemaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DhMetaRepository extends JpaRepository<DhMetaSchema, DhMetaSchemaId> {

}
