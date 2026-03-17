package hs.wdp.gd.meta.service;

import hs.wdp.gd.meta.repository.MetaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetaService {

    private final MetaMapper metaMapper;

    public int selectCount() {
        return metaMapper.selectCount();
    }

//    public void insertMetaSchema(MetaSchema metaSchema) {
//        metaMapper.insert(metaSchema);
//    }
}

