package hs.wdp.app.req.model;

import in.woowa.data.portal.common.dto.BaseDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * 권한신청 Model
 *
 * @date 2025. 5. 9.
 * @author Clush
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper=false)
public class DpReqModel extends BaseDto {

    /* 현황(관리자) 여부 */
    private String mngrYn = "N";

	/* 권한신청 ID */
	private String reqId;
	
	/* 신청분류 */
	private String category; 
	
	/* 신청타입 */
	private String type; 
	
	/* 신청상태 */
	private String status; 
	
	/* 신청내용 */
	private String reqCmt; 
	
	/* 수정사유 or 반려사유 */
	private String procCmt; 
	
	/* 대리자ID */
	private String agentId; 
	
	/* 종속 신청건 */
	private String refId; 
	
	/* 결재라인 데이터 */
	private String apprLine; 
	
	/* 결재상태 */
	private String appr; 
	
	/* 1차 결재자 */
	private String appr1Id; 
	
	/* 1차 결재일 */
	private String appr1Dt; 
	
	/* 2차 결재자 */
	private String appr2Id; 
	
	/* 2차 결재일 */
	private String appr2Dt;
	
	/* 상세데이터 */
	private String reqContent;
	
	/* 권한신청 히스토리 날짜 */
	private String histDt;
	
	/* 권한신청 상세 히스토리 날짜 */
	private String histDtlDt;

	/* 외부결재 연동 결재선 ID */
	private String apprLineId;
	
	/* 상위 부서명 */
	private String upDeptNm;
    private String upDeptCode;

    /* 겸직 부서 */
    private String userConcurrent;
	
	/* 회사명 */
	private String companyNm;
    private String companyCode;
	
	/* 이메일 */
	private String userEmail;
	
	/* 종속 표준화검토 신청건 */
	private String reviewRefId;

    /* 표준화검토 종속 단어/용어 신청ID */
    private String reviewWordReqId;
    private String reviewTermReqId;

    /* 수정자명 */
    private String modiNm;

    /**
     * 모델 담당자
     */
    /* 모델 담당자 dp ID */
    private String dpMdlUserId;

    /* 모델 담당자 ID */
    private String mdlUserId;

    /* 모델 담당자명 */
    private String mdlUserNm;

    /* 모델 담당자 이메일 */
    private String mdlUserEmail;

    /* 모델 담당자 부서명 */
    private String mdlUserDeptNm;

    /* 모델 담당자 부서코드 */
    private String mdlUserDeptCode;

    /* 모델 담당자 회사코드 */
    private String mdlUserCompanyCode;

    /**
     * 표준 담당자
     */
    /* 표준 담당자 dp ID */
    private String dpStdUserId;

    /* 표준 담당자 ID */
    private String stdUserId;

    /* 표준 담당자명 */
    private String stdUserNm;

    /* 표준 담당자 이메일 */
    private String stdUserEmail;

    /* 표준 담당자 부서명 */
    private String stdUserDeptNm;

    /* 표준 담당자 부서코드 */
    private String stdUserDeptCode;

    /* 표준 담당자 회사코드 */
    private String stdUserCompanyCode;

    /**
     * 의견
     */
    /* 신청서 의견수 */
    private Integer reqCnt;

    /* 테이블 의견수 */
    private Integer tblCnt;

    /* 컬럼 의견수 */
    private Integer colCnt;

    /* 용어 의견수 */
    private Integer termCnt;

    /* 단어 의견수 */
    private Integer wordCnt;

    /* 오늘 신청서 의견수 */
    private Integer todayReqCnt;

    /* 오늘 테이블 의견수 */
    private Integer todayTblCnt;

    /* 오늘 컬럼 의견수 */
    private Integer todayColCnt;

    /* 오늘 용어 의견수 */
    private Integer todayTermCnt;

    /* 오늘 단어 의견수 */
    private Integer todayWordCnt;

    /* 업데이트 type(표준사전 승인신청용) */
    private String uptType;

    /* 표준화검토 현황 - 적용 List */
    private String applyList;

    /* 결재 현황 - 탭 구분 */
    private String aprvType;

}
