package hs.wdp.gd.meta.repository;

import hs.wdp.gd.meta.entity.DhMetaSchema;
import hs.wdp.gd.meta.entity.DhMetaSchemaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetaRepository extends JpaRepository<DhMetaSchema, DhMetaSchemaId> {

}
