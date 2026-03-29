package hs.wdp.app.gd.spec.dto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.*;

public class SpecSourceDto {

    @Getter @Setter
    public static class UpdateSourceNmParams {
        private String sourceId;
        private String sourceNm;
    }

    @Getter @Setter
    public static class SpecSource {
        private String sourceId;
        private String targetId;
        private String sourceNm;
        private int ordSeq;
        private String modiId;
        private String rgstDt;
        private String modiDt;
        private String userNm;
        private String deptNm;
        private String userEmail;
    }

    @Getter @Setter
    public static class SpecSourceInfoRecord {
        private String sourceId;
        private String sourceNm;
        private String coverage;
        private List<Map<String, Object>> manageItems;
        private List<Map<String, Object>> managers;
        private LocalDateTime rgstDt;
        // ... (이하 생략된 필드들)
        private int ordSeq;
    }

    @Getter @Setter
    public static class SpecSourceFieldRecord {
        String codeId;
        String codeNm;
        String sourceId;
        String sourceNm;
        String sourceFieldId;
        String targetFieldId;
        String sourceFieldCmt;
        int ordSeq;
    }

    @Getter @Setter
    public static class SpecSourceResult {
        List<SpecSourceFieldRecord> fields;
    }

    @Getter @Setter
    public static class CreateSourceFieldDto {
        String sourceId;
        String targetFieldId;
        String detail;
    }
}