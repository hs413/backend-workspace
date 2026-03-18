package hs.wdp.app.gd.meta.mapper;

import hs.wdp.app.gd.meta.entity.DhMetaSchema;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DhMetaSchemaMapper {
    int selectCount();

    List<DhMetaSchema> selectAllByProjectId(String projectId);
}
