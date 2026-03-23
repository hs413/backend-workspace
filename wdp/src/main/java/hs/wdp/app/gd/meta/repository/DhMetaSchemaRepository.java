package hs.wdp.app.gd.meta.repository;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import hs.wdp.app.gd.meta.entity.DhMetaSchema;
import hs.wdp.app.gd.meta.entity.DhMetaSchemaId;
import org.springframework.data.jpa.repository.Query;

public interface DhMetaSchemaRepository extends JpaRepository<DhMetaSchema, DhMetaSchemaId> {
    List<DhMetaSchema> findAllById(DhMetaSchemaId Id);

    // 1. 단건 조회
    @Query("SELECT s FROM DhMetaSchema s WHERE s.id = :id AND s.delYn = 'N'")
    Optional<DhMetaSchema> findActiveById(@Param("id") DhMetaSchemaId id);

    // 2. 목록 조회 (매니저용 - 삭제되지 않은 전체 스키마)
    @Query("SELECT s FROM DhMetaSchema s WHERE s.id.projectId = :projectId AND s.delYn = 'N' ORDER BY s.id.schemaId ASC")
    List<DhMetaSchema> findAllByProjectId(@Param("projectId") String projectId);

    // 3. 목록 조회 (일반 사용자용 - 사용 중인 스키마만)
    @Query("SELECT s FROM DhMetaSchema s WHERE s.id.projectId = :projectId AND s.delYn = 'N' AND s.useYn = 'Y' ORDER BY s.id.schemaId ASC")
    List<DhMetaSchema> findInUseByProjectId(@Param("projectId") String projectId);
}
