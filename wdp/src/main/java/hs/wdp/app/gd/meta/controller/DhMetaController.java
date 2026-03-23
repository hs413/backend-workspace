package hs.wdp.app.gd.meta.controller;


import hs.wdp.app.gd.meta.dto.DhMetaDto;
import hs.wdp.app.gd.meta.entity.DhMetaSchema;
import hs.wdp.app.gd.meta.model.DhMetaSchemaModel;
import hs.wdp.app.gd.meta.service.DhMetaSchemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DhMetaController {

    private final DhMetaSchemaService schemaService;

    @RequestMapping("/meta/schema")
    public DhMetaSchemaModel selectSchema(DhMetaDto dto) {
        return schemaService.selectSchemaById(dto);
    }

    @RequestMapping("/meta/schema-jpa")
    public DhMetaSchema selectSchemaJpa(DhMetaDto dto) {
        return schemaService.selectSchemaByIdJpa(dto);
    }
}
