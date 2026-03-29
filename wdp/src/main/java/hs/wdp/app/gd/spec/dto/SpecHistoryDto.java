package hs.wdp.app.gd.spec.dto;

import java.time.LocalDateTime;
import lombok.*;

public class SpecHistoryDto {

    @Getter @Setter
    public static class HistParams {
        private String targetId;
        private String startDt;
        private String endDt;
    }

    @Getter @Setter
    public static class TargetFieldHist {
        private String targetFieldId;
        private String targetFieldNm;
        private String dataType;
        private String requiredYn;
        private String description;
        private String useYn;
        private String delYn;
        private String dhOid;
        private String modiNm;
        private String modiDeptNm;
        private String modiId;
        private LocalDateTime modiDt;
    }

    @Getter @Setter
    public static class SourceFieldHist {
        private String sourceId;
        private String sourceNm;
        private int dhOid;
        private String targetFieldId;
        private String targetFieldNm;
        private String sourceFieldId;
        private int wwOid;
        private String detail;
        private String modiNm;
        private String modiDeptNm;
        private String modiId;
        private LocalDateTime modiDt;
    }
}