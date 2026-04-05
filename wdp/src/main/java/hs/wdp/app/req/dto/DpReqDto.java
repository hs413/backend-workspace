package hs.wdp.app.req.dto;

import in.woowa.data.portal.common.model.DpReqModel;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * 권한신청 DTO
 *
 * @date 2025. 5. 9.
 * @author Clush
 */
@Getter
@Setter
public class DpReqDto extends DpReqModel {
	/* 조회할 신청분류 */
    private List<String> lstCategory;

	/* 조회 제외할 상태 */
	private List<String> exceptLstStatus;
	
    /* 검색 기준 */
    private String searchStandard;

    /* 포함 */
    private String[] includeWords;

    /* 제외 */
    private String[] excludeWords;

    /* 기간 기준 */
    private String periodCondition;

    /* 시작일 */
	private String startDt;

    /* 종료일 */
    private String endDt;

    /* 표준신청 - 표준분류명 */
    private String stdAreaNm;
    
    /* 표준신청 - 용어에 종속된 단어 ID */
    private String wordReqId;
    private String refIdCnt;

	/* 신청 - 내 신청 필터 */
	private String myReqFilter;
    
    /* 표준신청 필터/정렬조건 */
    private String categoryFilter;
    
    /* 표준화검토신청 필터/정렬조건 */
    private String typeFilter;
    
    /* 재식별화 필터/정렬조건 */
    private String statusFilter;
    private String orderByRgstNm;
    private String orderByDeptCode;
    private String orderByRgstDt;
    private String orderByGrantStartDt;
    private String orderByStatusModiDt;

    /* 태블로 필터/정렬조건 */
    private String authFilter;

	/* 스키마 생성 신청 필터/정렬조건 */
	private String layerFilter;
	private String orderByLayer;

    /* 결재함 여부 */
    private Boolean isAprv;
    
    /*대시보드 > 나의 업무현황 - 결재대기 카운트 리스트 유무 */
    private Boolean isApprReady;

    /* 대시보드 > 결재 현황 - 페이징 처리 유무 */
    private Boolean usePaging;

    /* 표준신청 필터/정렬조건 */
    private String orderByModiDt;

    /* 종속 표준사전 신청건 상세데이터(표준사전 리스트) */
    private String stdReqContent;

    /* 종속 표준화검토 신청건 상세데이터(표준사전 리스트) */
    private String reviewReqContent;

    /* 신청내역 특정 요소 수정용 */
    private String objId;

    /* 신청내역 특정 요소의 단어/용어 수정용 */
    private String originLogNm; //변경전 한글명
    private String changeLogNm; //변경후 한글명

	/* 미등록 용|단어용 */
	private String logNm;       //한글명
	private String usageCase;   //단어 - 활용예시

    /* 신청SEQ */
    private Integer seqNo;

    /* 신청번호 */
    private String reqNo;

    /* 신청제목*/
    private String title;

    /* 글로벌 데이터 필터/정렬조건 */
    private String orderByHopeDt;
    private String deptInputValue;
    private String integrationDirection;
    private String integrationMethod;

	@Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class Result {

        /* 컨텐츠 정보 */
        private List<DpReqDto> contents;
        
        /* history 정보 */
        private List<DpReqDto> hist;
        
        /* 상세 정보 */
        private DpReqDto detail;

        /* 검색 정보 */
        private DpReqDto search;

        /* 페이지 정보 */
        private PagingDto page;
    }
}
