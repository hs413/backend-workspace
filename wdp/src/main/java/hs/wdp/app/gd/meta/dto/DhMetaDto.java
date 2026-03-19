package hs.wdp.app.gd.meta.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DhMetaDto  {

    private String projectId;
    private String schemaId;
    private String tableId;
    private String columnId;
    private String mngrYn;

    private String keyword;
    private String tableSearchCondition;
    private String tableSearchKeyword;

    public boolean isManager() {
        return "Y".equals(mngrYn);
    }

    @Getter
    @Setter
    public static class ProjectSaveDto {
        private List<DhMetaSchemaDto.SaveDto> schemas;
    }

//    @Getter
//    @Setter
//    public static class SchemaSaveDto {
//        private DhMetaSchemaDto.SaveDto schema;
//        private List<DhMetaTableDto.SaveDto> tables;
//    }

//    @Getter
//    @Setter
//    public static class TableSaveDto {
//        private DhMetaTableDto.SaveDto table;
//        private List<DhMetaColumnDto.SaveDto> columns;
//    }

    @Getter
    @Setter
    @Builder
    public static class SearchResult {
        private String id;
        private String name;
        private String type;
        private String projectId;
        private String schemaId;
        private String tableId;
        private Boolean isLeaf;

        @Builder.Default
        private List<SearchResult> children = new ArrayList<>();
    }
}
