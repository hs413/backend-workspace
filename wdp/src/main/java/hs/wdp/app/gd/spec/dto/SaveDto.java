package hs.wdp.app.gd.spec.dto;

import hs.wdp.app.gd.spec.dto.common.SpecCommonDto;
import java.util.List;
import lombok.*;

public class SpecSaveDto {

    @Getter @Setter
    public static class SaveRequest {
        private String targetId;
        private String targetNm;
        private String category;
        private String purpose;
        private String description;
        private String exam;
        private List<SpecCommonDto.RefInfo> refs;
        private String status;
        private int ordSeq;

        private List<SourceInfo> sources;
        private List<FieldInfo> fields;
    }

    @Getter @Setter
    public static class SourceInfo {
        private String sourceId;
        private String sourceNm;
        private String coverage;
        private List<String> manageItems;
        private List<SpecCommonDto.ManagerInfo> managers;
        private int ordSeq;
    }

    @Getter @Setter
    public static class FieldInfo {
        private String targetFieldId;
        private String targetFieldNm;
        private String dataType;
        private String description;
        private String requiredYn;
        private String useYn;
        private int ordSeq;
    }
}