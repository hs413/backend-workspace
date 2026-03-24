package hs.wdp.app.gd.meta.service;

import hs.wdp.app.gd.meta.dto.DhMetaDto;
import hs.wdp.app.gd.meta.dto.DhMetaSchemaDto;
import hs.wdp.app.gd.meta.entity.DhMetaSchema;
import hs.wdp.app.gd.meta.entity.DhMetaSchemaId;
import hs.wdp.app.gd.meta.model.DhMetaSchemaModel;
import hs.wdp.app.gd.meta.mapper.DhMetaSchemaMapper;
import hs.wdp.app.gd.meta.repository.DhMetaSchemaRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DhMetaSchemaService {

    private final DhMetaSchemaMapper mapper;
    private final DhMetaSchemaRepository repository;

    public DhMetaSchemaModel selectSchemaById(DhMetaDto dto) {
        return mapper.selectSchemaById(dto);
    }

    public List<DhMetaSchemaModel> selectSchemas(DhMetaDto dto) {
        return mapper.selectSchemas(dto);
    }

    // JPA
    public DhMetaSchema selectSchemaByIdJpa(DhMetaDto dto) {
        DhMetaSchemaId id = new DhMetaSchemaId(dto.getProjectId(), dto.getSchemaId());

        return repository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("스키마를 찾을 수 없습니다."));
    }

    public List<DhMetaSchema> selectSchemasJpa(DhMetaDto dto) {
        if (dto.isManager()) {
            return repository.findAllByProjectId(dto.getProjectId());
        }
        return repository.findInUseByProjectId(dto.getProjectId());
    }

    @Transactional
    public void upsertSchemasJpa(List<DhMetaSchema> schemas) {
        repository.saveAll(schemas);
    }

    @Transactional
    public void updateSchemaListJpa(List<DhMetaSchemaDto.SaveDto> list, String modiId) {
        for (DhMetaSchemaDto.SaveDto dto : list) {
            DhMetaSchemaId id = new DhMetaSchemaId(dto.getProjectId(), dto.getSchemaId());

            DhMetaSchema schema = repository.findActiveById(id)
                    .orElseThrow(() -> new IllegalArgumentException("스키마를 찾을 수 없습니다. ID: " + dto.getSchemaId()));

            schema.updateInfo(dto.getUseYn(), dto.getWwDscr(), modiId);
        }
    }

    @Transactional
    public void updateSchemaJpa(DhMetaSchemaDto.SaveDto dto) {
        DhMetaSchemaId id = new DhMetaSchemaId(dto.getProjectId(), dto.getSchemaId());

        DhMetaSchema schema = repository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("스키마를 찾을 수 없습니다."));

        schema.updateInfo(dto.getUseYn(), dto.getWwDscr(), dto.getModiId());
    }
}
