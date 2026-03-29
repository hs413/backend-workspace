package hs.wdp.app.gd.spec.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.*;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

@Getter
@Setter
public class SpecTargetDto {

    @Getter
    @Setter
    public static class SpecTargetRecord {
        private String category;
        private JSONArray targets;

        public SpecTargetResult to() {
            List<SpecTarget> list = new ArrayList<>();
            for (int i = 0; i < targets.length(); i++) {
                JSONObject obj = targets.getJSONObject(i);
                list.add(new SpecTarget(obj.getString("targetNm"), obj.getString("targetId")));
            }
            return new SpecTargetResult(category, list);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class SpecTarget {
        private String targetNm;
        private String targetId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class SpecTargetResult {
        private String category;
        private List<SpecTarget> targets;
    }

    @Getter
    @Setter
    public static class SpecTargetInfoRecord {
        private String targetId;
        private String targetNm;
        private String purpose;
        private List<Map<String, Object>> refs;
        private String description;
        private String exam;
        private String category;
        private int ordSeq;
        private String status;
        private LocalDateTime rgstDt;
        private String rgstId;
        private String rgstNm;
        private String rgstEmail;
        private String deptNm;
        private String companyNm;
        private LocalDateTime modiDt;
        private String modiId;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class SpecTargetInfoResult {
        private SpecTargetInfoRecord targetInfo;
        private List<SpecSourceDto.SpecSourceInfoRecord> sourceInfos;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class SpecTargetAllResult {
        private SpecTargetInfoRecord targetInfo;
        private List<SpecSourceDto.SpecSourceInfoRecord> sources;
        private List<SpecTargetFieldsRecord> fields;
    }

    @Getter
    @Setter
    public static class SpecTargetFieldsParams extends BaseDto {
        private String targetId;
        private String sourceId;
        private String orderByTargetField;
        private String mngrYn;
        private boolean isManager;
    }

    @Getter
    @Setter
    public static class SpecTargetFieldsRecord {
        private String targetFieldId;
        private String targetFieldNm;
        private String dataType;
        private String description;
        private String useYn;
        private String requiredYn;
        private LocalDateTime rgstDt;
        private String rgstId;
        private String rgstNm;
        private String rgstEmail;
        private String deptNm;
        private String companyNm;
        private LocalDateTime modiDt;
        private String modiId;
        private int sourceFieldCount;
        private int ordSeq;
        private String wwEvent;
        private String wwField;
        private String wwFieldDesc;
        private String wwDataType;
        private String wwTransferLogic;
        private String wwSendYn;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class SpecTargetFieldsResult {
        private List<SpecTargetFieldsRecord> contents;
        private PagingDto page;
    }

    @Getter
    @Setter
    public static class DeleteParams {
        private String targetId;
    }
}