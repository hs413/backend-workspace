package hs.wdp.app.gd.spec.mapper;

import in.woowa.data.portal.app.core.gcp.spec.dto.DhSpecDto;
import in.woowa.data.portal.app.core.gcp.spec.model.*;
import in.woowa.data.portal.app.core.user.dto.UserMgmtResDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DhSpecMapper {
    /** 규격 조회 */
    List<DhSpecDto.SpecTargetRecord> selectDhSpecTargets(@Param("userRole") String userRole, @Param("userId") String userId);

    /** 규격 상세 조회 */
    DhSpecDto.SpecTargetInfoRecord selectDhSpecTargetInfo(@Param("targetId") String targetId);

    /** 전송 시스템 조회 */
    List<DhSpecDto.SpecSourceInfoRecord> selectDhSpecSources(@Param("targetId") String targetId);

    /** 규격 필드 조회*/
    List<DhSpecDto.SpecTargetFieldsRecord> selectDhSpecTargetFields(DhSpecDto.SpecTargetFieldsParams params);

    /** 수정 화면용 상세 정보 조회 */
    List<DhSpecDto.SpecTargetFieldsRecord> selectDhSpecTargetFieldsAll(@Param("targetId") String targetId);

    /** 필드 count */
    int selectDhSpecTargetFieldsCount(DhSpecDto.SpecTargetFieldsParams params);

    /**  */
    DhSpecDto.SpecSourceInfoRecord selectDhSpecSourceInfo(@Param("sourceId") String sourceId);

    /** 타겟 생성, 수정, 삭제, 히스토리 */
    int dupCheck(@Param("targetNm") String targetNm, @Param("targetId") String targetId);

    DhSpecTargetModel selectTargetById(@Param("targetId") String targetId);

    int insertTarget(DhSpecTargetModel targetModel);

    int updateTarget(DhSpecTargetModel targetModel);

    int insertTargetHist(DhSpecTargetModel targetModel);


    /** 전송 시스템 생성, 수정, 삭제, 히스토리  */
    List<DhSpecSourceModel> selectSourcesByTargetId(@Param("targetId") String targetId);

    int selectMaxOrdSeq(@Param("category") String category);

    int insertSources(List<DhSpecSourceModel> sourceModels);

    int updateSources(List<DhSpecSourceModel> sourceModels);

    int deleteSources(List<DhSpecSourceModel> sourceModels);

    int insertSourcesHist(List<DhSpecSourceModel> sourceModels);

    List<DhSpecDto.SpecSource> selectSources(DhSpecDto dto);

    /** 전송 시스템 담당자 생성, 삭제 */
    int upsertSourceManagers(List<DhSpecSourceManagerModel> managers);

    int insertSourceManagersHist(List<DhSpecSourceManagerModel> managers);

    void deleteDeletedManagers(@Param("sourceId") String sourceId, @Param("userIds") List<String> userIds);


    /** 타겟 필드 생성, 수정, 삭제, 히스토리 */
    List<DhSpecTargetFieldModel> selectDhSpecTargetFieldsByTargetId(@Param("targetId") String targetId);

    int insertTargetFields(List<DhSpecTargetFieldModel> targetFieldModels);

    int updateTargetFields(List<DhSpecTargetFieldModel> targetFieldModels);

    int deleteTargetFields(List<DhSpecTargetFieldModel> targetFieldModels);

    int insertTargetFieldsHist(List<DhSpecTargetFieldModel> targetFieldModels);

    /** 전송 시스템 필드 조회 */
    List<DhSpecDto.SpecSourceFieldRecord> selectSourceFields(@Param("targetFieldId") String targetFieldId, @Param("sourceId") String sourceId);

    /** 전송 시스템 필드 수정용 조회 */
    DhSpecSourceFieldModel selectSourceFieldById(@Param("sourceId") String sourceId, @Param("targetFieldId") String targetFieldId);

    /** 전송 시스템 규격 필드 등록, 수정 */
    int insertSourceField(DhSpecSourceFieldModel sourceFieldModel);

    int updateSourceField(DhSpecSourceFieldModel sourceFieldModel);

    int countValidFields(@Param("targetFieldId") String targetFieldId);

    int selectNextWwOid(@Param("targetFieldId") String targetFieldId);

    int insertSourceFieldHist(DhSpecSourceFieldModel sourceFieldModel);

    int updateSourceFieldHist(DhSpecSourceFieldModel sourceFieldModel);


    List<DhSpecDto.TargetFieldHist> selectTargetFieldHist(DhSpecDto.HistParams params);

    List<DhSpecDto.SourceFieldHist> selectSourceFieldHist(DhSpecDto.HistParams params);

    int selectMaxOidByTargetId(@Param("targetId") String targetId);

    void deleteSpec(DhSpecTargetModel target);

    DhSpecSourceModel selectSourceById(@Param("sourceId") String sourceId);

    void updateSourceNm(DhSpecSourceModel source);

    List<UserMgmtResDto.User> selectDhSpecManagers();
}

