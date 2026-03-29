package hs.wdp.app.gd.spec.dto;

import in.woowa.data.portal.common.dto.BaseDto;
import in.woowa.data.portal.common.dto.PagingDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * DH 규격(스펙)관리 DTO
 *  ~ Params 조회 API
 *  ~ Dto 등록/수정 API
 *  ~ Result API 결과 반환용
 *  ~ Record DB 결과 조회용
 * */
@Getter
@Setter
public class DhSpecDto {

    private String targetId;
    private String targetFieldId;
    private String sourceId;
    private String sourceFieldId;

    @Getter
    @Setter
    public static class SpecTargetRecord {
        private String category;
        private JSONArray targets;

        public SpecTargetResult to() {
            List<SpecTarget> list = new ArrayList<>();

            for (int i = 0; i < targets.length(); i++) {
                JSONObject obj = targets.getJSONObject(i);
                String targetNm = obj.getString("targetNm");
                String targetId = obj.getString("targetId");
                list.add(new SpecTarget(targetNm, targetId));
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

    @Setter
    @Getter
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
    public static class SpecSourceInfoRecord {
        private String sourceId;
        private String sourceNm;
        private String coverage;
        private List<Map<String, Object>> manageItems;
        private List<Map<String, Object>> managers;
        private LocalDateTime rgstDt;
        private String rgstId;
        private String rgstNm;
        private String rgstEmail;
        private String deptNm;
        private String companyNm;
        private LocalDateTime modiDt;
        private String modiId;
        private int ordSeq;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class RefInfo {
        private String url;
        private String linkName;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class SpecTargetInfoResult {
        private SpecTargetInfoRecord targetInfo;
        private List<SpecSourceInfoRecord> sourceInfos;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class SpecTargetAllResult {
        private SpecTargetInfoRecord targetInfo;
        private List<SpecSourceInfoRecord> sources;
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

    @Getter
    @Setter
    public static class SpecSourceResult {
        List<SpecSourceFieldRecord> fields;
    }

    @Getter
    @Setter
    public static class CreateSourceFieldDto {
        String sourceId;
        String targetFieldId;
        String detail;
    }

    @Getter
    @Setter
    public static class SaveDto {
        private String targetId;
        private String targetNm;
        private String category;
        private String purpose;
        private String description;
        private String exam;
        private List<RefInfo> refs;
        private String status;
        private int ordSeq;

        private List<SourceInfo> sources;
        private List<FieldInfo> fields;
    }


    @Getter
    @Setter
    public static class SourceInfo {
        private String sourceId;
        private String sourceNm;
        private String coverage;
        private List<String> manageItems;
        private List<ManagerInfo> managers;
        private int ordSeq;
    }


    @Getter
    @Setter
    public static class ManagerInfo {
        private String userId;
        private String userNm;
        private String userEmail;

        private String deptCode;
        private String deptNm;
        private String upDeptNm;
        private String companyCode;
        private String companyNm;
    }

    @Getter
    @Setter
    public static class FieldInfo {
        private String targetFieldId;
        private String targetFieldNm;
        private String dataType;
        private String description;
        private String requiredYn;
        private String useYn;
        private int ordSeq;
    }


    @Getter
    @Setter
    public static class HistParams {
        private String targetId;
        private String startDt;
        private String endDt;
    }

    @Getter
    @Setter
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

    @Getter
    @Setter
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

    @Getter
    @Setter
    public static class DeleteParams {
        private String targetId;
    }

    @Getter
    @Setter
    public static class UpdateSourceNmParams {
        private String sourceId;
        private String sourceNm;
    }

    @Getter
    @Setter
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
}
