package hs.wdp.app.gd.meta.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class DhMetaColumnDto {
    @Getter
    @Setter
    @Builder
    public static class ListResult {
        private List<Result> list;
        private int totalCount;
    }

    @Getter
    @Setter
    @Builder
    public static class SaveDto {
        private String projectId;
        private String schemaId;
        private String tableId;
        private String columnId;
        private String wwDscr;
        private List<ColumnMap> columnMap;
        private List<ColumnMap> deletedMap;

    }

    @Getter
    @Setter
    public static class Result {
        private String projectId;
        private String schemaId;
        private String tableId;
        private String columnId;
        private int colOrd;
        private String dataType;
        private String dhDscr;
        private String wwDscr;
        private String colKorNm;
        private String colEngNm;
        private int clusterOrd;
        private String clusterYn;
        private String partitionYn;
        private String useYn;
        private String delYn;
        private String rgstId;
        private String rgstDt;
        private String modiId;
        private String modiDt;
        private boolean isDic;
    }

    @Getter
    @Setter
    @Builder
    public static class ColumnMap {
        private String projectId;
        private String schemaId;
        private String tableId;
        private String columnId;
        private String mapId;
        private String mapType;
        private String stdAreaId;
        private String rgstId;

        private String korNm;
        private String engNm;
        private String mapPhyNm;
        private String mapPhyFullNm;
        private String mapLogNm;
        private String mapDesc;
    }

}
