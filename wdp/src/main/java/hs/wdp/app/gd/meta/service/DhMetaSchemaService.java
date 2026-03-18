package hs.wdp.app.gd.meta.service;

import hs.wdp.app.gd.meta.entity.DhMetaSchema;
import hs.wdp.app.gd.meta.mapper.DhMetaSchemaMapper;
import hs.wdp.app.gd.meta.repository.DhMetaSchemaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DhMetaSchemaService {

    private final DhMetaSchemaMapper mapper;
    private final DhMetaSchemaRepository repository;

    public List<DhMetaSchema> selectByProjectId(String projectId) {
        return mapper.selectByProjectId(projectId);
    }

    public List<DhMetaSchema> findAll() {
        return repository.findAll();
    }
}
