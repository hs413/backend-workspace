package hs.wdp.gd.meta.controller;


import hs.wdp.gd.meta.service.MetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;

    @RequestMapping("/meta/count")
    public int selectCount() {
        return metaService.selectCount();
    }

}
