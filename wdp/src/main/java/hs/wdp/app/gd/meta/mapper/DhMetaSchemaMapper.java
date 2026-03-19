package hs.wdp.app.gd.meta.mapper;

import hs.wdp.app.gd.meta.dto.DhMetaDto;
import hs.wdp.app.gd.meta.dto.DhMetaSchemaDto;
import hs.wdp.app.gd.meta.model.DhMetaSchemaModel;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DhMetaSchemaMapper {
    int upsertSchemas(List<DhMetaSchemaModel> schemas);

    List<DhMetaSchemaModel> selectSchemaIds(DhMetaDto dto);

    List<DhMetaSchemaModel> selectSchemas(DhMetaDto dto);

    DhMetaSchemaModel selectSchemaById(DhMetaDto dto);

    int updateSchemaList(@Param("list") List<DhMetaSchemaDto.SaveDto> list, @Param("modiId") String modiId);

    int updateSchema(DhMetaSchemaDto.SaveDto dto);
}
