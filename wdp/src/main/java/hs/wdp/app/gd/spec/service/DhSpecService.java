package hs.wdp.app.gd.spec.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.woowa.data.portal.app.core.gcp.spec.dto.DhSpecDto;
import in.woowa.data.portal.app.core.gcp.spec.mapper.DhSpecMapper;
import in.woowa.data.portal.app.core.gcp.spec.model.*;
import in.woowa.data.portal.app.core.user.dto.UserMgmtResDto;
import in.woowa.data.portal.app.core.user.service.UserMgmtService;
import in.woowa.data.portal.common.CommonConstants;
import in.woowa.data.portal.common.dto.PagingDto;
import in.woowa.data.portal.common.util.IdUtil;
import in.woowa.data.portal.common.util.SessionScopeUtil;
import in.woowa.data.portal.common.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class DhSpecService {

    private final DhSpecMapper dhSpecMapper;
    private final IdUtil idUtil;
    private final ObjectMapper mapper;
    private final UserMgmtService userMgmtService;

    /** 트리 조회 */
    public List<DhSpecDto.SpecTargetResult> selectDhSpecTargets(String mngrYn) {
        UserMgmtResDto.User user = userMgmtService.getUser(SessionScopeUtil.getUserId());

        String userRole = "USER";

        if ("Y".equals(mngrYn)) {
            if (isSystemManager(user.getApldUserAuthId())) {
                userRole = "ADMIN";
            } else if (isSpecManager(user.getApldUserAuthId())) {
                userRole = "MANAGER";
            }
        }

        List<DhSpecDto.SpecTargetRecord> targetList = dhSpecMapper.selectDhSpecTargets(userRole, user.getUserId());

        return targetList.stream().map(DhSpecDto.SpecTargetRecord::to).collect(Collectors.toList());
    }

    /**
     * 상세 정보 조회
     *  - 필드를 포함하지 않음
     * */
    public DhSpecDto.SpecTargetInfoResult selectDhSpecTargetInfo(String targetId) {

        return DhSpecDto.SpecTargetInfoResult.builder()
                .targetInfo(dhSpecMapper.selectDhSpecTargetInfo(targetId))
                .sourceInfos(dhSpecMapper.selectDhSpecSources(targetId))
                .build();
    }

    /**
     * 필드 조회 (페이징)
     *  - 필드를 페이징하여 반환
     * */
    public DhSpecDto.SpecTargetFieldsResult selectDhSpecTargetFields(DhSpecDto.SpecTargetFieldsParams params) {
        UserMgmtResDto.User user = userMgmtService.getUser(SessionScopeUtil.getUserId());

        if (isSystemManager(user.getApldUserAuthId()) || isSpecManager(user.getApldUserAuthId())) {
            params.setManager(true);
        }

        List<DhSpecDto.SpecTargetFieldsRecord> fields = dhSpecMapper.selectDhSpecTargetFields(params);
        int totalCount = dhSpecMapper.selectDhSpecTargetFieldsCount(params);

        PagingDto pagingDto = PagingDto.builder()
                .page(params.getPage())
                .pageSize(params.getPageSize())
                .build();

        pagingDto.setPaging(totalCount);

        return DhSpecDto.SpecTargetFieldsResult.builder()
                .contents(fields)
                .page(pagingDto)
                .build();
    }

    /**
     * 수정 화면용 상세 정보 조회
     *  - 필드를 포함하여 반환
     *  - 필드를 페이징하지 않음
     * */
    public DhSpecDto.SpecTargetAllResult selectDhSpecTarget(String targetId) {

        return DhSpecDto.SpecTargetAllResult.builder()
                .targetInfo(dhSpecMapper.selectDhSpecTargetInfo(targetId))
                .sources(dhSpecMapper.selectDhSpecSources(targetId))
                .fields(dhSpecMapper.selectDhSpecTargetFieldsAll(targetId))
                .build();
    }

    /** 규격 이름 중복 확인 */
    public boolean targetNmDupCheck(String targetNm, String targetId) {
        int exist = dhSpecMapper.dupCheck(targetNm, targetId);

        return exist == 0;
    }


    /** 규격 관리 등록 */
    @Transactional
    public void saveSpec(DhSpecDto.SaveDto dto) {
        String userId = SessionScopeUtil.getUserId();

        LocalDateTime now = LocalDateTime.now();

        String targetId = StringUtils.isEmpty(dto.getTargetId())
                ? idUtil.getSecureUUID()
                : dto.getTargetId();

        processTarget(dto, targetId, userId, now);

        List<DhSpecSourceModel> processedSources = processSources(dto, targetId, userId, now);

        processManagers(processedSources, userId);

        processFields(dto, targetId, userId, now);
    }

    /** 규격 저장 */
    private void processTarget(DhSpecDto.SaveDto dto, String targetId, String userId, LocalDateTime now) {
        boolean isNew = StringUtils.isEmpty(dto.getTargetId());

        DhSpecTargetModel oldModel = isNew ? null : dhSpecMapper.selectTargetById(targetId);

        String finalRgstId = (oldModel != null) ? oldModel.getRgstId() : userId;
        LocalDateTime finalRgstDt = (oldModel != null) ? oldModel.getRgstDt() : now;

        int ordSeq = (oldModel != null) ? oldModel.getOrdSeq() : dhSpecMapper.selectMaxOrdSeq(dto.getCategory());

        DhSpecTargetModel newModel = DhSpecTargetModel.builder()
                .targetId(targetId)
                .targetNm(dto.getTargetNm())
                .category(dto.getCategory())
                .status(dto.getStatus())
                .purpose(dto.getPurpose())
                .refs(dto.getRefs())
                .description(dto.getDescription())
                .exam(dto.getExam())
                .ordSeq(ordSeq)
                .delYn("N")
                .rgstId(finalRgstId)
                .rgstDt(finalRgstDt)
                .modiId(userId)
                .modiDt(now)
                .build();

        if (isNew || oldModel == null) {
            dhSpecMapper.insertTarget(newModel);
            dhSpecMapper.insertTargetHist(newModel);
        } else if (isTargetChanged(newModel, oldModel)) {
            dhSpecMapper.updateTarget(newModel);
            dhSpecMapper.insertTargetHist(newModel);
        }
    }

    /** 전송 시스템 저장 */
    private List<DhSpecSourceModel> processSources(DhSpecDto.SaveDto dto, String targetId, String userId, LocalDateTime now) {
        List<DhSpecSourceModel> currentSources = dhSpecMapper.selectSourcesByTargetId(targetId);
        Map<String, DhSpecSourceModel> currentSourceMap = currentSources.stream()
                .collect(Collectors.toMap(DhSpecSourceModel::getSourceId, Function.identity()));

        List<String> survivingSourceIds = new ArrayList<>();
        List<DhSpecSourceModel> insertList = new ArrayList<>();
        List<DhSpecSourceModel> updateList = new ArrayList<>();
        List<DhSpecSourceModel> deleteList = new ArrayList<>();
        List<DhSpecSourceModel> historyList = new ArrayList<>();

        List<DhSpecSourceModel> resultList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(dto.getSources())) {
            for (DhSpecDto.SourceInfo source : dto.getSources()) {
                boolean isNew = StringUtils.isEmpty(source.getSourceId());
                String sourceId = isNew ? idUtil.getSecureUUID() : source.getSourceId();

                survivingSourceIds.add(sourceId);

                DhSpecSourceModel oldModel = isNew ? null : currentSourceMap.get(sourceId);

                String finalRgstId = (oldModel != null) ? oldModel.getRgstId() : userId;
                LocalDateTime finalRgstDt = (oldModel != null) ? oldModel.getRgstDt() : now;

                DhSpecSourceModel newModel = DhSpecSourceModel.builder()
                        .targetId(targetId)
                        .sourceId(sourceId)
                        .sourceNm(source.getSourceNm())
                        .coverage(source.getCoverage())
                        .manageItems(source.getManageItems())
                        .ordSeq(source.getOrdSeq())
                        .managers(source.getManagers())
                        .rgstId(finalRgstId)
                        .rgstDt(finalRgstDt)
                        .modiId(userId)
                        .modiDt(now)
                        .delYn("N")
                        .build();

                resultList.add(newModel);

                if (isNew || oldModel == null) {
                    insertList.add(newModel);
                    historyList.add(newModel);
                } else if (isSourceChanged(newModel, oldModel)) {
                    updateList.add(newModel);
                    historyList.add(newModel);
                }
            }
        }


        for (DhSpecSourceModel oldModel : currentSources) {
            if (!survivingSourceIds.contains(oldModel.getSourceId())) {
                DhSpecSourceModel deletedModel = oldModel.toBuilder()
                        .modiId(userId)
                        .modiDt(now)
                        .delYn("Y")
                        .build();

                deleteList.add(deletedModel);
                historyList.add(deletedModel);
            }
        }

        if (!CollectionUtils.isEmpty(deleteList)) dhSpecMapper.deleteSources(deleteList);
        if (!CollectionUtils.isEmpty(insertList)) dhSpecMapper.insertSources(insertList);
        if (!CollectionUtils.isEmpty(updateList)) dhSpecMapper.updateSources(updateList);
        if (!CollectionUtils.isEmpty(historyList)) dhSpecMapper.insertSourcesHist(historyList);

        return resultList;
    }

    /** 규격 필드 저장 */
    private void processFields(DhSpecDto.SaveDto dto, String targetId, String userId, LocalDateTime now) {
        List<DhSpecTargetFieldModel> currentFields = dhSpecMapper.selectDhSpecTargetFieldsByTargetId(targetId);

        Map<String, DhSpecTargetFieldModel> currentFieldMap = currentFields.stream()
                .collect(Collectors.toMap(DhSpecTargetFieldModel::getTargetFieldId, Function.identity()));

        int maxOid = dhSpecMapper.selectMaxOidByTargetId(targetId);

        List<String> survivingFieldIds = new ArrayList<>();
        List<DhSpecTargetFieldModel> insertList = new ArrayList<>();
        List<DhSpecTargetFieldModel> updateList = new ArrayList<>();
        List<DhSpecTargetFieldModel> deleteList = new ArrayList<>();
        List<DhSpecTargetFieldModel> historyList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(dto.getFields())) {
            for (DhSpecDto.FieldInfo field : dto.getFields()) {
                boolean isNew = StringUtils.isEmpty(field.getTargetFieldId());
                String fieldId = isNew ? idUtil.getSecureUUID() : field.getTargetFieldId();

                survivingFieldIds.add(fieldId);

                DhSpecTargetFieldModel oldModel = isNew ? null : currentFieldMap.get(fieldId);

                int currentOid;
                if (isNew) {
                    maxOid++;
                    currentOid = maxOid;
                } else {
                    currentOid = oldModel != null ? oldModel.getDhOid() : 0;
                }

                String finalRgstId = (oldModel != null && oldModel.getRgstId() != null) ? oldModel.getRgstId() : userId;
                LocalDateTime finalRgstDt = (oldModel != null && oldModel.getRgstDt() != null) ? oldModel.getRgstDt() : now;

                DhSpecTargetFieldModel newModel = DhSpecTargetFieldModel.builder()
                        .targetFieldId(fieldId)
                        .targetId(targetId)
                        .targetFieldNm(field.getTargetFieldNm())
                        .dataType(field.getDataType())
                        .description(field.getDescription())
                        .requiredYn(field.getRequiredYn())
                        .useYn(field.getUseYn())
                        .ordSeq(field.getOrdSeq())
                        .rgstId(finalRgstId)
                        .rgstDt(finalRgstDt)
                        .modiId(userId)
                        .modiDt(now)
                        .delYn("N")
                        .dhOid(currentOid)
                        .build();

                if (isNew || oldModel == null) {
                    insertList.add(newModel);
                    historyList.add(newModel);
                } else if (isFieldChanged(newModel, oldModel)) {
                    updateList.add(newModel);
                    historyList.add(newModel);
                }
            }
        }


        for (DhSpecTargetFieldModel oldModel : currentFields) {
            if (!survivingFieldIds.contains(oldModel.getTargetFieldId())) {
                DhSpecTargetFieldModel deletedModel = oldModel.toBuilder()
                        .modiId(userId)
                        .modiDt(now)
                        .delYn("Y")
                        .useYn("N")
                        .build();

                deleteList.add(deletedModel);
                historyList.add(deletedModel);
            }
        }

        if (!CollectionUtils.isEmpty(deleteList)) dhSpecMapper.deleteTargetFields(deleteList);
        if (!CollectionUtils.isEmpty(insertList)) dhSpecMapper.insertTargetFields(insertList);
        if (!CollectionUtils.isEmpty(updateList)) dhSpecMapper.updateTargetFields(updateList);
        if (!CollectionUtils.isEmpty(historyList)) dhSpecMapper.insertTargetFieldsHist(historyList);
    }

    /**
     * 변경 감지 헬퍼 메서드 (히스토리)
     * - 신규 생성이거나, 주요 필드 값이 변경된 경우 true 반환
     */
    private boolean isTargetChanged(DhSpecTargetModel newItem, DhSpecTargetModel oldItem) {
        if (oldItem == null) {
            return true;
        }

        // oldItem의 리스트를 강제로 List<RefInfo> 규격으로 변환
        List<DhSpecDto.RefInfo> normalizedOldRefs = mapper.convertValue(
                oldItem.getRefs(),
                new TypeReference<List<DhSpecDto.RefInfo>>() {}
        );

        if (!Objects.equals(newItem.getTargetNm(), oldItem.getTargetNm())) return true;
        if (!Objects.equals(newItem.getCategory(), oldItem.getCategory())) return true;
        if (!Objects.equals(newItem.getStatus(), oldItem.getStatus())) return true;
        if (!Objects.equals(newItem.getPurpose(), oldItem.getPurpose())) return true;
        if (!Objects.equals(newItem.getRefs(), normalizedOldRefs)) return true;
        if (!Objects.equals(newItem.getDescription(), oldItem.getDescription())) return true;
        if (!Objects.equals(newItem.getExam(), oldItem.getExam())) return true;
        if (!Objects.equals(newItem.getOrdSeq(), oldItem.getOrdSeq())) return true;

        return false;
    }

    private boolean isSourceChanged(DhSpecSourceModel newItem, DhSpecSourceModel oldItem) {
        if (oldItem == null) {
            return true;
        }

        if (!Objects.equals(newItem.getSourceNm(), oldItem.getSourceNm())) return true;
        if (!Objects.equals(newItem.getCoverage(), oldItem.getCoverage())) return true;
        if (!Objects.equals(newItem.getManageItems(), oldItem.getManageItems())) return true;
        if (!Objects.equals(newItem.getOrdSeq(), oldItem.getOrdSeq())) return true;

        return false;
    }

    private boolean isFieldChanged(DhSpecTargetFieldModel newItem, DhSpecTargetFieldModel oldItem) {
        if (oldItem == null) {
            return true;
        }

        // 2. 주요 컬럼 값 비교
        if (!Objects.equals(newItem.getTargetFieldNm(), oldItem.getTargetFieldNm())) return true;
        if (!Objects.equals(newItem.getDataType(), oldItem.getDataType())) return true;
        if (!Objects.equals(newItem.getRequiredYn(), oldItem.getRequiredYn())) return true;
        if (!Objects.equals(newItem.getUseYn(), oldItem.getUseYn())) return true;
        if (!Objects.equals(newItem.getOrdSeq(), oldItem.getOrdSeq())) return true;
        if (!Objects.equals(newItem.getDescription(), oldItem.getDescription())) return true;

        return false;
    }

    /** 전송 시스템 매니저 저장 */
    private void processManagers(List<DhSpecSourceModel> sources, String userId) {
        if (CollectionUtils.isEmpty(sources)) {
            return;
        }

        List<DhSpecSourceManagerModel> allManagerModels = new ArrayList<>();

        for (DhSpecSourceModel source : sources) {
            List<String> survivingUserIds = new ArrayList<>();

            if (!CollectionUtils.isEmpty(source.getManagers())) {
                for (DhSpecDto.ManagerInfo mgr : source.getManagers()) {
                    survivingUserIds.add(mgr.getUserId());

                    allManagerModels.add(DhSpecSourceManagerModel.builder()
                            .sourceId(source.getSourceId())
                            .userId(mgr.getUserId())
                            .rgstId(userId)
                            .build());
                }
            }

            dhSpecMapper.deleteDeletedManagers(source.getSourceId(), survivingUserIds);
        }

        if (!CollectionUtils.isEmpty(allManagerModels)) {
            dhSpecMapper.upsertSourceManagers(allManagerModels);
        }
    }



    /** 전송 규격 필드 조회 */
    public DhSpecDto.SpecSourceResult selectDhSpecSourceFields(String targetFieldId, String sourceId) {
        List<DhSpecDto.SpecSourceFieldRecord> fields = dhSpecMapper.selectSourceFields(targetFieldId, sourceId);

        DhSpecDto.SpecSourceResult result = new DhSpecDto.SpecSourceResult();
        result.setFields(fields);

        return result;
    }

    @Transactional
    public int saveSourceField(DhSpecDto.CreateSourceFieldDto dto) {
        String userId = SessionScopeUtil.getUserId();
        LocalDateTime now = LocalDateTime.now();

        DhSpecSourceFieldModel existing = dhSpecMapper.selectSourceFieldById(dto.getSourceId(), dto.getTargetFieldId());
        String id = existing == null ? idUtil.getSecureUUID() : existing.getSourceFieldId();

        if (existing == null) {
            int nextWwOid = dhSpecMapper.selectNextWwOid(dto.getTargetFieldId());

            DhSpecSourceFieldModel sourceFieldModel = DhSpecSourceFieldModel.builder()
                    .detail(dto.getDetail())
                    .sourceId(dto.getSourceId())
                    .targetFieldId(dto.getTargetFieldId())
                    .sourceFieldId(id)
                    .wwOid(nextWwOid)
                    .rgstId(userId)
                    .rgstDt(now)
                    .modiId(userId)
                    .modiDt(now)
                    .build();

            dhSpecMapper.insertSourceField(sourceFieldModel);
            dhSpecMapper.insertSourceFieldHist(sourceFieldModel);
        } else {
            DhSpecSourceFieldModel updateReq = existing.toBuilder()
                    .detail(dto.getDetail())
                    .modiId(userId)
                    .modiDt(now)
                    .build();

            dhSpecMapper.updateSourceField(updateReq);

            DhSpecSourceFieldModel updated = dhSpecMapper.selectSourceFieldById(dto.getSourceId(), dto.getTargetFieldId());

            boolean isFieldExisted = isFieldAlreadyInDetail(existing.getDetail(), dto.getDetail());

            if (isFieldExisted) {
                dhSpecMapper.insertSourceFieldHist(updated);
            } else {
                dhSpecMapper.updateSourceFieldHist(updated);
            }
        }

        int newCount = dhSpecMapper.countValidFields(dto.getTargetFieldId());
        return newCount;
    }

    private boolean isFieldAlreadyInDetail(String oldDetailJson, String newDetailJson) {
        if (StringUtils.isEmpty(oldDetailJson)) return false;

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode oldNode = mapper.readTree(oldDetailJson);
            JsonNode newNode = mapper.readTree(newDetailJson);

            Iterator<String> fieldNames = newNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();

                // 1. 기존 JSON에 해당 키가 있는지 확인
                if (oldNode.has(fieldName)) {
                    JsonNode oldValue = oldNode.get(fieldName);

                    // 2. 키는 있지만 값이 비어있는지(null 이거나 빈 문자열) 체크
                    boolean isValueEmpty = oldValue == null
                            || oldValue.isNull()
                            || oldValue.asText().trim().isEmpty();

                    // 3. 값이 비어있지 않고 '실제 데이터'가 들어있을 때만 이력 추가(true) 대상
                    if (!isValueEmpty) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public List<DhSpecDto.TargetFieldHist> selectDhSpecTargetFieldHist(DhSpecDto.HistParams params) {
        return dhSpecMapper.selectTargetFieldHist(params);
    }

    public List<DhSpecDto.SourceFieldHist> selectDhSpecSourceFieldHist(DhSpecDto.HistParams params) {
        return dhSpecMapper.selectSourceFieldHist(params);
    }


    @Transactional
    public void deleteSpec(DhSpecDto.DeleteParams params) {
        DhSpecTargetModel target = dhSpecMapper.selectTargetById(params.getTargetId());

        if (target != null) {
            DhSpecTargetModel deleted = target.toBuilder()
                    .delYn("Y")
                    .modiId(SessionScopeUtil.getUserId())
                    .modiDt(LocalDateTime.now())
                    .build();

            dhSpecMapper.deleteSpec(deleted);
            dhSpecMapper.insertTargetHist(deleted);
        }
    }

    @Transactional
    public void updateSourceNm(DhSpecDto.UpdateSourceNmParams params) {
        DhSpecSourceModel source = dhSpecMapper.selectSourceById(params.getSourceId());

        if (source != null) {
            DhSpecSourceModel updated = source.toBuilder()
                    .sourceNm(params.getSourceNm())
                    .modiId(SessionScopeUtil.getUserId())
                    .modiDt(LocalDateTime.now())
                    .build();

            dhSpecMapper.updateSourceNm(updated);
            dhSpecMapper.insertSourcesHist(List.of(updated));
        }
    }

    public List<UserMgmtResDto.User> selectDhSpecManagers() {

        List<UserMgmtResDto.User> managers = dhSpecMapper.selectDhSpecManagers();
        return managers;
    }

    private boolean isSystemManager(String authId) {
        return CommonConstants.SYSAUTH.MNGR.equals(authId) || CommonConstants.SYSAUTH.DEVELOPER.equals(authId);
    }

    private boolean isSpecManager(String authId) {
        return CommonConstants.SYSAUTH.WW_SPEC_MANAGER.equals(authId);
    }

    public List<DhSpecDto.SpecSource> selectSources(DhSpecDto dto) {
        List<DhSpecDto.SpecSource> list = dhSpecMapper.selectSources(dto);
        return list;
    }
}
