package hs.wdp.app.gd.meta.controller;


import hs.wdp.app.gd.meta.service.DhMetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DhMetaController {

    private final DhMetaService metaService;

    @RequestMapping("/meta/count")
    public int selectCount() {
        return metaService.selectCount();
    }

}
