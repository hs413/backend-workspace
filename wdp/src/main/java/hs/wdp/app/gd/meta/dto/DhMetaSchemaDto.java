package hs.wdp.app.gd.meta.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class DhMetaSchemaDto {

    @Getter
    @Setter
    public static class SaveDto {
        private String projectId;
        private String schemaId;
        private String wwDscr;
        private String useYn;
        private String modiId;
    }

    @Getter
    @Setter
    @Builder
    public static class Result {
        private String projectId;
        private String schemaId;
    }
}
