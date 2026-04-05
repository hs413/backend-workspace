package hs.wdp.app.req.mapper;

import in.woowa.data.portal.app.core.dw.stdterm.model.StdTermModel;
import in.woowa.data.portal.app.core.dw.stdword.model.StdWordModel;
import in.woowa.data.portal.app.core.glue.model.GlueDbModel;
import in.woowa.data.portal.app.core.user.model.UserModel;
import in.woowa.data.portal.common.dto.ColInfoDto;
import in.woowa.data.portal.common.dto.DpReqDto;
import in.woowa.data.portal.common.dto.DpReqStatsDto;
import in.woowa.data.portal.common.dto.TblFixDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * 권한신청 Mapper 
 *
 * @date 2025. 5. 9.
 * @author Clush
 */
@Mapper
public interface DpReqMapper {

	/**
	 * 전체 갯수
	 * 
	 * @param dpReqDto
	 * @return
	 */
	int selectCount(DpReqDto dpReqDto);

	/**
     * 권한신청 목록
     * 
     * @param dpReqDto
     * @return
     */
    List<DpReqDto> selectAll(DpReqDto dpReqDto);

	/**
	 * 대시보드 > 결재 현황 전체 갯수
	 *
	 * @param dpReqDto
	 * @return
	 */
	int selectAprvCount(DpReqDto dpReqDto);

	/**
	 * 대시보드 > 결재 현황 목록
	 *
	 * @param dpReqDto
	 * @return
	 */
	List<DpReqDto> selectAprvAll(DpReqDto dpReqDto);

	/**
	 * 대시보드 > 결재 현황 목록 엑셀다운로드
	 *
	 * @param dpReqDto
	 * @return
	 */
	List<DpReqDto> excelDownload(DpReqDto dpReqDto);
    
    /**
	 * 미등록 단어 신청 내역 갯수
	 * 
	 * @param dpReqDto
	 * @return
	 */
	int selectUnregistWordCount(DpReqDto dpReqDto);

	/**
     * 미등록 단어 신청 내역
     * 
     * @param dpReqDto
     * @return
     */
    List<DpReqDto> selectUnregistWordReqList(DpReqDto dpReqDto);
    
    /**
     * 신청리스트 엑셀다운로드
     * 
     * @param dpReqDto
     * @return
     */
    List<DpReqDto> excelDownloadDpReqList(DpReqDto dpReqDto);
    
    /**
     * 상세 조회
     * @param dpReqDto
     * @return
     */
    DpReqDto selectDetail(@Param("reqId") String reqId);
    
    /**
     * 단어신청 건 등록완료 금일처리 건
     * @return
     */
    List<DpReqDto> selectTodayCompletionWordTarget();
    
    /**
     * 용어신청 건 등록완료 금일처리 건
     * @return
     */
    List<DpReqDto> selectTodayCompletionTermTarget();
    
    /**
     * 표준화검토 신청 건 등록완료 금일처리 건
     * @return
     */
    List<DpReqDto> selectTodayCompletionStdReviewTarget();
    
    /**
     *   스케줄러가 실행할 때 단어[단건]신청 건 등록완료 처리  
     * @return
     */
    int updateCompletionWordTarget();
    
    /**
     * 스케줄러가 실행할 때 용어[단건]신청 건 등록완료 처리
     * @return
     */
    int updateCompletionTermTarget();
    
    /**
     * 스케줄러가 실행할 때 표준화검토 신청 건 등록완료 처리
     * @return
     */
    int updateCompletionStdReviewTarget();
    
    /**
     * 신청 삭제
     * @param reqId
     * @return
     */
    void deleteDpReq(DpReqDto dpReqDto);
    
    /**
     * 신청 내역 메인 카운팅
     * @param reqId
     * @return
     */    
    DpReqStatsDto getDpReqCounts(DpReqDto dpReqDto);
    
    /**
     * 이미 신청한 단어 건인지 확인
     * 
     * @param dpReqDto
     * @return
     */
    DpReqDto checkWordDuplication(StdWordModel stdWordModel);

    /**
     * API검증시 미등록단어존재인 경우인데 신청 진행 중인 단어인지 확인
     *
     * @param dpReqDto
     * @return
     */
    DpReqDto checkUnregisteredWordDuplication(StdWordModel stdWordModel);
    
    /**
     * 이미 신청한 용어 건인지 확인
     * 
     * @param dpReqDto
     * @return
     */
    DpReqDto checkTermDuplication(StdTermModel stdTermModel);    
    
    /**
     * 권한신청 히스토리 조회
     * 
     * @param dpReqDto
     * @return
     */
    List<DpReqDto> selectHist(DpReqDto dpReqDto);
    
    /**
     * 권한신청 상세 히스토리 조회
     * 
     * @param dpReqDto
     * @return
     */
    List<DpReqDto> selectHistDtl(DpReqDto dpReqDto);

    /**
     * 권한신청 등록
     *  
     * @param dpReqDto
     * @return
     */
    int insertMst(DpReqDto dpReqDto);
    
    /**
     * 권한신청 상세 등록
     * 
     * @param dpReqDto
     * @return
     */
    int insertDetail(DpReqDto dpReqDto);
    
    /**
     * 권한신청 수정
     * 
     * @param dpReqDto
     * @return
     */
    int updateMst(DpReqDto dpReqDto);
    
    /**
     * 권한신청 상세 수정
     * 
     * @param dpReqDto
     * @return
     */
    int updateDetail(DpReqDto dpReqDto);

    /**
     * 신청내역 특정 요소 수정시 row 락(동시성보장)
     *
     * @param dpReqDto
     * @return
     */
    int lockReqDtlRow(@Param("reqId") String reqId);

    /**
     * 신청내역 특정 요소 수정
     *
     * @param dpReqDto
     * @return
     */
    int updateDtlByObjId(DpReqDto dpReqDto);

    /**
     * 표준화검토 신청내역 단어 변경
     *
     * @param dpReqDto
     * @return
     */
    int updateReviewDtlWord(DpReqDto dpReqDto);

    /**
     * 표준화검토 신청내역 용어 변경
     *
     * @param dpReqDto
     * @return
     */
    int updateReviewDtlTerm(DpReqDto dpReqDto);

    /**
     * 권한신청 히스토리 등록
     * 
     * @param dpReqDto
     * @return
     */
    int insertMstHist(DpReqDto dpReqDto);
    
    /**
     * 권한신청 상세 히스토리 등록
     * 
     * @param dpReqDto
     * @return
     */
    int insertDetailHist(DpReqDto dpReqDto);

    /**
     * 권한신청 메인 개별 조회 only Id
     * 
     * @param dpReqDto
     * @return
     */
    DpReqDto selectOneToOnlyId(DpReqDto dpReqDto);

    /**
     * 처리지연 상태인 목록 조회
     *
     * @param dpReqDto
     * @return
     */
    List<DpReqDto> selectAdStatusList(DpReqDto dpReqDto);
    
    /**
     * 테이블 prefix/suffix 코드 정보 조회
     * @param TblFixDto
     * @return
     */    
	List<TblFixDto> getTblFixList();
	
	/**
     * 표준화검토 컬럼 정보 조회
     *
     * @param dpReqDto
     * @return
     */
    List<DpReqDto> getDpColInfoList(ColInfoDto colInfoDto);
    List<DpReqDto> getDpDelColInfoList(ColInfoDto colInfoDto);

    /**
     * 권한에 대한 사용자 목록 조회
     * @param authIdsList
     * @return
     */
    List<UserModel> getUserLIstByAuth(@Param("authIdsList") List<String> authIdsList);

	/**
	 * 표준화검토 용|단어 refID 조회
	 * @param dpReqDto
	 * @return
	 */
    DpReqDto selectReviewRefId(DpReqDto dpReqDto);

	/**
	 * 미등록 용|단어 추가|삭제 merge
	 * @param dpReqDto
	 * @return
	 */
	void updateUnRegMerge(DpReqDto dpReqDto);

	/**
	 * 표준화검토 검증패스 처리
	 * @param dpReqDto
	 */
	void updateReviewPass(DpReqDto dpReqDto);

	/**
	 * 표준화검토 검증패스 취소 처리
	 * @param dpReqDto
	 */
	void updateReviewPassCancel(DpReqDto dpReqDto);

	/**
	 * 표준화검토 - 미등록 용|단어 update
	 * @param dpReqDto
	 */
	void updateMstByReview(DpReqDto dpReqDto);

	/**
	 * 표준화검토 - 미등록 용어|단어 데이터 조회
	 * @param reqId
	 * @return
	 */
	List<DpReqDto> getUnRegListById(@Param("reqId") String reqId);

	/**
	 * 표준화검토 - 스키마 리스트 조회
	 * @param GlueDbModel
	 * @return
	 */
	List<GlueDbModel> getSchemaList();

	/**
	 * 스키마 생성 신청 - 스키마명 중복 체크
	 * @param schemaNm
	 * @return
	 */
	Integer getSchemaNmDuplChk(@Param("schemaNm") String schemaNm);

	/**
	 * 스키마 생성 신청 - 스키마명 중복 체크(테스트)
	 * @param schemaNm
	 * @return
	 */
	Integer getSchemaNmDuplChkTest(@Param("schemaNm") String schemaNm);
}
