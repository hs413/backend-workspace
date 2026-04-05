package in.woowa.data.portal.common.controller;

import in.woowa.data.portal.common.CommonConstants.DP_REQ;
import in.woowa.data.portal.common.CommonConstants.DP_REQ_STATUS;
import in.woowa.data.portal.common.dto.ApiResDto;
import in.woowa.data.portal.common.dto.ColInfoDto;
import in.woowa.data.portal.common.dto.DpReqDto;
import in.woowa.data.portal.common.mapper.DpReqMapper;
import in.woowa.data.portal.common.service.DpReqService;
import in.woowa.data.portal.common.util.IdUtil;
import in.woowa.data.portal.common.util.SessionScopeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 신청 공통 컨트롤러
 *
 * @date 2025. 5. 9.
 * @author Clush
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "dp")
public class DpReqRestController {

	@Autowired
	DpReqService dpReqService;

    @Autowired
    private IdUtil idUtil;

	@Autowired
	DpReqMapper dpReqMapper;

    /**
     * Default 시작일자/종료일자/userId 세팅
     * @param dpReqDto
     */
    private void setDefaultData(DpReqDto dpReqDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // startDt가 없으면 오늘 날짜 셋팅
        if (dpReqDto.getStartDt() == null || dpReqDto.getStartDt().isEmpty()) {
            String preMonth = LocalDate.now()
                    .minusMonths(1)
                    .format(formatter);
            dpReqDto.setStartDt(preMonth);
        }

        // endDt가 없으면 익월 동일 - 1 날짜 셋팅
        if (dpReqDto.getEndDt() == null || dpReqDto.getEndDt().isEmpty()) {
            // 익월 동일 - 1 날짜 계산
            String nextMonth = LocalDate.now()
                    .plusMonths(1)
                    .minusDays(1)
                    .format(formatter);
            dpReqDto.setEndDt(nextMonth);
        }

		// userId
		dpReqDto.setUserId(SessionScopeUtil.getUserId());
    }

	/**
	 * 신청내역 리스트
	 * 
	 * @param dpReqDto
	 * @return
	 */
	@GetMapping(value = "/v1/req", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDpReqList(DpReqDto dpReqDto) {
        setDefaultData(dpReqDto);

		List<DpReqDto> lst = dpReqService.selectAll(dpReqDto);
		
        return ResponseEntity.ok(ApiResDto.success(
        	DpReqDto.Result.builder()
        		.page(dpReqDto)
        		.search(dpReqDto)
        		.contents(lst)
        		.build()
        ));
    }

	/**
	 * 대시보드 > 결재 현황 팝업 - 탭별 카운트
	 */
	@GetMapping(value = "/v1/req/aprv/summary", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDpReqAprvSummary(DpReqDto dpReqDto) {
		// 각 탭별 카운트만 조회
		Map<String, Integer> summary = new LinkedHashMap<>();

		dpReqDto.setAprvType("inProgress");
		summary.put("inProgress", dpReqMapper.selectAprvCount(dpReqDto));

		dpReqDto.setAprvType("aprvWait");
		summary.put("aprvWait", dpReqMapper.selectAprvCount(dpReqDto));

		dpReqDto.setAprvType("processing");
		summary.put("processing", dpReqMapper.selectAprvCount(dpReqDto));

		return ResponseEntity.ok(ApiResDto.success(summary));
	}

	/**
	 * 대시보드 > 결재 현황 팝업 - 탭별 목록
	 */
	@GetMapping(value = "/v1/req/aprv/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDpReqAprvList(DpReqDto dpReqDto) {
		List<DpReqDto> lst = dpReqService.selectAprvAll(dpReqDto);

		return ResponseEntity.ok(ApiResDto.success(
				DpReqDto.Result.builder()
						.page(dpReqDto)
						.search(dpReqDto)
						.contents(lst)
						.build()
		));
	}

	/**
	 * 대시보드 > 결재 현황 팝업 - 탭별 엑셀다운로드
	 */
	@GetMapping(value = "/v1/req/aprv/list/excel", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> excelDpReqAprvList(DpReqDto dpReqDto) {
		return ResponseEntity.ok(ApiResDto.success(dpReqService.excelDpReqAprvList(dpReqDto)));
	}
	
	/**
	 * 미등록 단어 신청 내역 리스트
	 * 
	 * @param dpReqDto
	 * @return
	 */
	@GetMapping(value = "/v1/req/unregist", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUnregistWordReqList(DpReqDto dpReqDto) {
		List<DpReqDto> lst = dpReqService.selectUnregistWordReqList(dpReqDto);
		
        return ResponseEntity.ok(ApiResDto.success(
        	DpReqDto.Result.builder()
        		.page(dpReqDto)
        		.search(dpReqDto)
        		.contents(lst)
        		.build()
        ));
    }
	
	/**
	 * 신청내역 리스트 엑셀다운로드
	 * 
	 * @param dpReqDto
	 * @return
	 */
	@GetMapping(value = "/v1/req/excel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> excelDownloadDpReqList(DpReqDto dpReqDto) {
        setDefaultData(dpReqDto);
        return ResponseEntity.ok(ApiResDto.success(dpReqService.excelDownloadDpReqList(dpReqDto)));
    }

	/**
	 * 재식별/비식별 신청리스트 엑셀다운로드
	 *
	 * @param dpReqDto
	 * @return
	 */
	@GetMapping(value = "/v1/req/detail/excel", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> excelDownloadDetailList(DpReqDto dpReqDto) {
        setDefaultData(dpReqDto);
        return ResponseEntity.ok(ApiResDto.success(dpReqService.excelDownloadDetailList(dpReqDto)));
	}
	
	/**
	 * 신청내역 상세(사용자)
	 * 
	 * @param dpReqDto
	 * @return
	 */
	@GetMapping(value = "/v1/req/{reqId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDpReqDetail(@PathVariable String reqId) {
		return ResponseEntity.ok(ApiResDto.success(dpReqService.selectDetail(reqId)));
	}
	
	/**
	 * 신청내역 상세(관리자)
	 * 
	 * @param dpReqDto
	 * @return
	 */
	@GetMapping(value = "/v1/req/detail/mngr/{reqId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMngrDpReqDetail(@PathVariable String reqId) {
		DpReqDto paramDto	= new DpReqDto();
		paramDto.setReqId(reqId);
		
		DpReqDto detail 	= dpReqService.selectDetail(reqId);
		List<DpReqDto> hist = dpReqService.selectHist(paramDto);
		
		return ResponseEntity.ok(ApiResDto.success(
        	DpReqDto.Result.builder()
        		.detail(detail)
        		.hist(hist)
        		.build()
        ));
	}
	
	/**
	 * 신청내역 등록
	 * 
	 * @param dpReqDto
	 * @return
	 */
	@PostMapping(value = "/v1/req", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createDpReq(@RequestBody DpReqDto dpReqDto) {
		dpReqDto.setRgstId(SessionScopeUtil.getUserId());
		String reqId = "";
		reqId = dpReqService.insertMst(dpReqDto);
		unregistGateway(dpReqDto);
		
		return ResponseEntity.ok(ApiResDto.success(reqId));
	}	
	
	/**
	 * 표준신청 / 표준화검토신청 미등록 처리여부판단
	 * @param dpReqDto
	 */
	private void unregistGateway(DpReqDto dpReqDto) {
		/* 용어신청 - 신청일 경우 */
		if( DP_REQ.STD_TERM.equals(dpReqDto.getCategory())
				&& "0".equals(dpReqDto.getStatus())
					&& ! "w".equals(dpReqDto.getAppr())
		) {
			//미등록 단어 신청
			if (dpReqDto.getReqId() == null || dpReqDto.getReqId().isEmpty()) {
				unRegistWordReq(dpReqDto);
			} else {
				// ref_id로 미등록 단어 신청 내역 조회
				DpReqDto searchDto = new DpReqDto();
				searchDto.setRefId(dpReqDto.getReqId());
				searchDto.setCategory(DP_REQ.STD_WORD);

				List<DpReqDto> existingWordReqs = dpReqService.selectUnregistWordReqList(searchDto);

				if (existingWordReqs == null || existingWordReqs.isEmpty()) {
					unRegistWordReq(dpReqDto);
				}
			}
		}

		/* 표준화검토신청 - 신청일 경우
		if( DP_REQ.STD_REVIEW.equals(dpReqDto.getCategory())
				&& "0".equals(dpReqDto.getStatus())
					&& ! "w".equals(dpReqDto.getAppr())
		) {
			unregistStdReviewGateway(dpReqDto);
		}
		*/
	}
	
	/**
	 * 표준신청 미등록 단어 신청
	 * 
	 * @param dpReqDto
	 * @return
	 */
	private void unRegistWordReq(DpReqDto dpReqDto) {
		JSONObject reqContObj = new JSONObject(dpReqDto.getReqContent());		//req_content
		
		if(reqContObj == null) {
			return;
		}
		
		//단건
		if("1".equals(dpReqDto.getType())) {
			//미등록 신청 리스트 있을 경우
			if( reqContObj.optBoolean("isNewWordRegistration", false) && reqContObj.has("unregisteredWordList") ) {
				JSONArray reqWordList = (JSONArray) reqContObj.get("unregisteredWordList");	//미등록 단어 리스트
				DpReqDto unRegDto 		= new DpReqDto();
				
				JSONObject wordObj		= new JSONObject();						//다건 등록 multi 객체
				JSONArray  wordList		= new JSONArray();						//다건 단어 Array
				
				int len = 0;
				if(reqWordList != null) {
					len = reqWordList.length();
				}
				
				for(int i = 0; i < len; i++) {
					JSONObject wordMap = reqWordList.getJSONObject(i);
					
					if(wordMap != null) {
						//단어등록 여부
						if( wordMap.optBoolean("isNewWordSelected", false) ) {
							String termLogNm 		= reqContObj.optString("termLogNm");	//신청용어
							JSONObject tempObj		= new JSONObject();

                            unRegDto.setCategory(DP_REQ.STD_WORD);							//신청분류 : 'w'(단어신청)
							unRegDto.setType("2");											//신청타입 : '2'(다건)
							unRegDto.setStatus(DP_REQ_STATUS.TEMP_SAVE);					//상태 : 't'(임시저장)
							unRegDto.setReqCmt(termLogNm + " 용어신청 중 미등록 단어신청");	//신청내용
							unRegDto.setRgstId(SessionScopeUtil.getUserId());				//신청자
							unRegDto.setRefId(dpReqDto.getReqId());							//종속 신청 건 : 용어신청ID
							
							tempObj.put("category", DP_REQ.STD_WORD);						//신청분류 : 'w'(단어신청)
							tempObj.put("status", 	DP_REQ_STATUS.TEMP_SAVE);				//상태 : 't'(임시저장)
							tempObj.put("wordLogNm", wordMap.get("wordLogNm"));				//단어 한글명
							tempObj.put("stdAreaId", wordMap.get("stdAreaId"));				//표준분류
							tempObj.put("usageCase", termLogNm);							//활용 예시
                            tempObj.put("objId", idUtil.getSecureUUID());                   //신청댓글정보 UUID
							
							wordList.put(tempObj);											//multi 단어 리스트
						}
					}
				}
				wordObj.put("multi", wordList);
				unRegDto.setReqContent(wordObj.toString());							//req_content
				
				dpReqService.insertMst(unRegDto);
			}
		}
		//다건
		else {
			JSONArray multiArray = (JSONArray) reqContObj.get("multi");		//multi
			DpReqDto unRegDto 		= new DpReqDto();
			JSONObject wordObj		= new JSONObject();						//다건 등록 multi 객체
			JSONArray  wordList		= new JSONArray();						//다건 단어 Array
			
			String termLogNms	 = "";										//신청 용어들
			int unregistCnt	 	 = 0;
			
			int len = 0;
			if(multiArray != null) {
				len = multiArray.length();
			}
			
			for(int i = 0; i < len; i++) {
				JSONObject multiMap = multiArray.getJSONObject(i);
				
				if(multiMap != null) {
					if( multiMap.optBoolean("isNewWordRegistration", false) && multiMap.has("unregisteredWordList") ) {
						JSONArray reqWordList = (JSONArray) multiMap.get("unregisteredWordList");	//미등록 단어 리스트
						
						int jLen = 0;
						if(reqWordList != null) {
							jLen = reqWordList.length();
						}
						
						for(int j = 0; j < jLen; j++) {
							JSONObject wordMap = reqWordList.getJSONObject(j);
							
							if(wordMap != null) {
								//단어등록 여부
								if( wordMap.optBoolean("isNewWordSelected", false) ) {
									String termLogNm 		= multiMap.optString("termLogNm");	//신청용어
									JSONObject tempObj		= new JSONObject();
									
									termLogNms += termLogNm + ", ";
									
									tempObj.put("category", DP_REQ.STD_WORD);						//신청분류 : 'w'(단어신청)
									tempObj.put("status", 	DP_REQ_STATUS.TEMP_SAVE);				//상태 : 't'(임시저장)
									tempObj.put("wordLogNm", wordMap.get("wordLogNm"));				//단어 한글명
									tempObj.put("stdAreaId", wordMap.get("stdAreaId"));				//표준분류
									tempObj.put("usageCase", termLogNm);							//활용 예시
                                    tempObj.put("objId", idUtil.getSecureUUID());                   //신청댓글정보 UUID
									
									wordList.put(tempObj);											//multi 단어 리스트
								}
							}
							
							unregistCnt++;
						}
					}
				}
			}
			
			if(unregistCnt > 0) {
				termLogNms = termLogNms.substring(0, termLogNms.length() -2);
			
				unRegDto.setCategory(DP_REQ.STD_WORD);							//신청분류 : 'w'(단어신청)
				unRegDto.setType("2");											//신청타입 : '2'(다건)
				unRegDto.setStatus(DP_REQ_STATUS.TEMP_SAVE);					//상태 : 't'(임시저장)
				unRegDto.setReqCmt(termLogNms + " 용어신청 중 미등록 단어신청");		//신청내용
				unRegDto.setRgstId(SessionScopeUtil.getUserId());				//신청자
				unRegDto.setRefId(dpReqDto.getReqId());							//종속 신청 건 : 용어신청ID
				
				wordObj.put("multi", wordList);
				unRegDto.setReqContent(wordObj.toString());							//req_content
				
				dpReqService.insertMst(unRegDto);
			}
		}
		
	}
	
	/**
	 * 표준화검토신청 미등록용어/단어
	 * @param dpReqDto
	 */
	private void unregistStdReviewGateway(DpReqDto dpReqDto) {
		// req_content
		JSONArray reqContent = new JSONArray(dpReqDto.getReqContent());
		
		// 신청 DTO
		DpReqDto unregistWordDto = new DpReqDto();
		DpReqDto unregistTermDto = new DpReqDto();
		
		// 다건 등록 multi 객체
		JSONObject wordObj = new JSONObject();
		JSONObject termObj = new JSONObject();
		
		// 미등록 리스트
		JSONArray wordList = new JSONArray();
		JSONArray termList = new JSONArray();
		
		String tableLogNms = "";	//테이블명
		String termLogNms = "";		//용어명

		if(reqContent != null) {
			JSONObject col = null;
						
			for(int i = 0, j = 0, len = reqContent.length(), len2 = 0; i < len; i++) {
				JSONObject tObj = reqContent.getJSONObject(i);
				JSONArray  cols = reqContent.getJSONObject(i).getJSONArray("colInfo");
				
				// 테이블명
				if(tObj != null) {
					if("C".equalsIgnoreCase(tObj.optString("tblType"))) {
						
						// 단어
						if(tObj.optBoolean("isNewWordRegistration", false) && tObj.has("unregisteredWordList")) {
							termLogNms += appendUnregistWordList(wordList, tObj);
						}
						
						// 용어
						if(tObj.optBoolean("isReqTermTarget", false)) {
							tableLogNms += appendUnregistTermList(termList, tObj, true);
						}
					}
				}

				// 컬럼명
				if(cols != null) {
					for(j = 0, len2 = cols.length(); j < len2; j++) {
						col = cols.getJSONObject(j);
						
						if(col != null) {
							if("C".equalsIgnoreCase(col.optString("colType"))) {
								
								// 단어
								if(col.getBoolean("isNewWordRegistration") && col.has("unregisteredWordList")) {
									termLogNms += appendUnregistWordList(wordList, col);	
								}

								// 용어
								if(col.optBoolean("isReqTermTarget", false)) {
									tableLogNms += appendUnregistTermList(termList, col, false);
								}
							}
						}
					}
				}
			}
		}
		
		String reviewReqId = dpReqDto.getReqId();
		
		//미등록 용어 신청
		try {
			tableLogNms = tableLogNms.substring(0, tableLogNms.length() -2);
		}
		catch(StringIndexOutOfBoundsException e) {
			
		}
		
		unregistTermDto.setCategory(DP_REQ.STD_TERM);							//신청분류 : 't'(용어신청)
		unregistTermDto.setType("2");											//신청타입 : '2'(다건)
		unregistTermDto.setStatus(DP_REQ_STATUS.TEMP_SAVE);						//상태 : 't'(임시저장)
		unregistTermDto.setReqCmt(tableLogNms + " 표준화검토 신청 중 미등록 용어신청");	//신청내용
		unregistTermDto.setRgstId(SessionScopeUtil.getUserId());				//신청자
		unregistTermDto.setRefId(reviewReqId);									//종속 신청 건 : 표준화검토 신청ID
		unregistTermDto.setReviewRefId(reviewReqId);							//종속 표준화검토 신청 건 : 표준화검토 신청ID

        String termReqId = null;

        // 용어 리스트가 있을 때만 저장
        if(termList.length() > 0) {
            termObj.put("multi", termList);
            unregistTermDto.setReqContent(termObj.toString());						//req_content
            //dpReqService.insertMst(unregistTermDto);
            termReqId = dpReqService.insertMst(unregistTermDto);
        }
		
		//미등록 단어 신청
		try {
			termLogNms = termLogNms.substring(0, termLogNms.length() -2);
		}
		catch(StringIndexOutOfBoundsException e) {
			
		}
		
		unregistWordDto.setCategory(DP_REQ.STD_WORD);						//신청분류 : 'w'(단어신청)
		unregistWordDto.setType("2");										//신청타입 : '2'(다건)
		unregistWordDto.setStatus(DP_REQ_STATUS.TEMP_SAVE);					//상태 : 't'(임시저장)
		unregistWordDto.setReqCmt(termLogNms + " 용어신청 중 미등록 단어신청");	//신청내용
		unregistWordDto.setRgstId(SessionScopeUtil.getUserId());			//신청자
        // tblInfo에서 단어 등록이 있는 경우 null, colInfo인 경우 termReqId 설정
		unregistWordDto.setRefId(termReqId);		                        //종속 신청 건 : 용어신청ID
		unregistWordDto.setReviewRefId(reviewReqId);						//종속 표준화검토 신청 건 : 표준화검토 신청ID

        // 단어 리스트가 있을 때만 저장
        if(wordList.length() > 0) {
            wordObj.put("multi", wordList);
            unregistWordDto.setReqContent(wordObj.toString());                    //req_content
            dpReqService.insertMst(unregistWordDto);
        }
	}
	
	/**
	 * 미등록단어 리스트 생성
	 * 
	 * @param wordList
	 * @param obj
	 */
	private String appendUnregistWordList(JSONArray wordList, JSONObject obj) {
		if(obj == null || ! obj.has("unregisteredWordList")) {
			return "";
		}
		
		String termLogNms = "";
		JSONArray reqWordList = obj.getJSONArray("unregisteredWordList");
		
		for(int j = 0, len = reqWordList.length(); j < len; j++) {
			JSONObject wordMap = reqWordList.getJSONObject(j);
			
			if(wordMap != null) {
				//단어등록 여부
				if( wordMap.optBoolean("isNewWordSelected", false) ) {
					String termLogNm = obj.optString("termLogNm");	//신청용어
					JSONObject tempObj = new JSONObject();
					
					termLogNms += termLogNm + ", ";
					
					tempObj.put("category", DP_REQ.STD_WORD);						//신청분류 : 'w'(단어신청)
					tempObj.put("status", 	DP_REQ_STATUS.TEMP_SAVE);				//상태 : 't'(임시저장)
					tempObj.put("wordLogNm", wordMap.get("wordLogNm"));				//단어 한글명
					tempObj.put("stdAreaId", "");									//표준분류
					tempObj.put("usageCase", termLogNm);							//활용 예시
                    tempObj.put("objId", idUtil.getSecureUUID());                   //신청댓글정보 UUID
					
					wordList.put(tempObj);											//multi 단어 리스트
				}
			}
		}
		
		return termLogNms;
	}
	
	/**
	 * 미등록용어 리스트 생성
	 * 
	 * @param wordList
	 * @param obj
	 */
	private String appendUnregistTermList(JSONArray termList, JSONObject obj, boolean isTable) {
		if(obj == null) {
			return "";
		}
		
		String tableLogNms = "";
		String termLogNm	 = obj.optString("termLogNm");
        if (termLogNm == null || termLogNm.trim().isEmpty()) {
            termLogNm = obj.optString("pxTypeLogNm", "") + " " + obj.optString("sxTypeLogNm", "");
        }
        String termPhyNm = "";
        if (isTable) {
            String originTermPhyNm = obj.optString("originTermPhyNm");
            termPhyNm = (originTermPhyNm == null || originTermPhyNm.trim().isEmpty())
                    ? obj.optString("pxType", "") + "_" + obj.optString("sxType", "")
                    : originTermPhyNm;
        } else {
            termPhyNm = obj.optString("termPhyNm");
        }
        String termPhyFullNm = obj.optString("termPhyFullNm");
        if (termPhyFullNm == null || termPhyFullNm.trim().isEmpty()) {
            termPhyFullNm = obj.optString("pxPhyFullNm", "") + " " + obj.optString("sxPhyFullNm", "");
        }
		String termDesc 	 = obj.optString("termDesc");
		
		JSONObject tempObj = new JSONObject();
		
		tableLogNms += termLogNm + ", ";
		
		tempObj.put("category", DP_REQ.STD_TERM);
		tempObj.put("status", 	DP_REQ_STATUS.TEMP_SAVE);		
		tempObj.put("termLogNm", termLogNm);
		tempObj.put("termPhyNm", termPhyNm);
		tempObj.put("termPhyFullNm", termPhyFullNm);
		tempObj.put("termDesc", termDesc);
		tempObj.put("stdAreaId", "");
        tempObj.put("objId", idUtil.getSecureUUID());   //신청댓글정보 UUID

        // 미등록 단어 리스트 추가
        if (obj.has("unregisteredWordList")) {
            JSONArray unregisteredWordList = obj.getJSONArray("unregisteredWordList");
            JSONArray filteredWordList = new JSONArray();

            // 신규 단어 등록이 있는 경우(isNewWordSelected가 true)만 추가
            for (int i = 0, len = unregisteredWordList.length(); i < len; i++) {
                JSONObject wordItem = unregisteredWordList.getJSONObject(i);
                if (wordItem != null && wordItem.optBoolean("isNewWordSelected", false)) {
                    JSONObject newWordItem = new JSONObject();
                    newWordItem.put("stdAreaId", wordItem.optString("stdAreaId", ""));
                    newWordItem.put("wordLogNm", wordItem.optString("wordLogNm", ""));
                    newWordItem.put("isNewWordSelected", true);
                    filteredWordList.put(newWordItem);
                }
            }

            // 신규 단어 목록이 있는 경우에만 추가
            if (filteredWordList.length() > 0) {
                tempObj.put("unregisteredWordList", filteredWordList);
            }
        }
				
		termList.put(tempObj);
		
		return tableLogNms;
	}
	
	/**
	 * 신청내역 수정
	 * 
	 * @param reqId
	 * @param dpReqDto
	 * @return
	 */
	@PutMapping(value = "/v1/req/{reqId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDpReq(@PathVariable String reqId, @RequestBody DpReqDto dpReqDto) {
		dpReqDto.setReqId(reqId);
		dpReqDto.setModiId(SessionScopeUtil.getUserId());
		dpReqService.updateDpReq(dpReqDto);

		unregistGateway(dpReqDto);
		
        return ResponseEntity.ok(ApiResDto.success());
    }
	
	/**
	 * 신청내역 상세 수정
	 * 
	 * @param reqId
	 * @param dpReqDto
	 * @return
	 */
	@PutMapping(value = "/v1/req/dtl/{reqId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDpReqDtl(@PathVariable String reqId, @RequestBody DpReqDto dpReqDto) {
		dpReqDto.setReqId(reqId);
		dpReqDto.setModiId(SessionScopeUtil.getUserId());
        
		dpReqService.updateMst(dpReqDto);
		dpReqService.updateDetail(dpReqDto);
		
		unregistGateway(dpReqDto);
		
        return ResponseEntity.ok(ApiResDto.success());
    }

    /**
     * 신청내역 특정 요소 수정
     *
     * @param reqId
     * @param dpReqDto
     * @return
     */
    @PutMapping(value = "/v1/req/dtl/{reqId}/{objId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateReqDtlByObjId(@PathVariable String reqId, @PathVariable String objId, @RequestBody DpReqDto dpReqDto) {
        dpReqDto.setReqId(reqId);
        dpReqDto.setObjId(objId);
        dpReqDto.setModiId(SessionScopeUtil.getUserId());

        dpReqService.updateReqDtlByObjId(dpReqDto);

        return ResponseEntity.ok(ApiResDto.success());
    }
	
	/**
     * 신청내역 삭제
     * @param reqId
     * @return
     */
    @DeleteMapping(value = "/v1/req/{reqId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteDpReq(@PathVariable String reqId) {
    	DpReqDto dpReqDto = new DpReqDto();
    	
    	dpReqDto.setReqId(reqId);
		dpReqDto.setModiId(SessionScopeUtil.getUserId());
		
    	dpReqService.deleteDpReq(dpReqDto);

        return ResponseEntity.ok(ApiResDto.success());
    }
    
	/**
	 * 신청내역 메인(사용자) 통계
	 * 
	 * @param dpReqDto
	 * @return
	 */
	@GetMapping(value = "/v1/req/main/counts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDpReqCounts(DpReqDto dpReqDto) {
		return ResponseEntity.ok(ApiResDto.success(dpReqService.getDpReqCounts(dpReqDto)));
	}    

	/**
	 * 결재선 생성
	 * 
	 * @param dpReqDto
	 * @return
	 */
	@PostMapping(value = "/v1/req/apprcreate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> apprCreate(@RequestBody DpReqDto dpReqDto) {
		dpReqDto.setRgstId(SessionScopeUtil.getUserId());
		return ResponseEntity.ok(ApiResDto.success(dpReqService.dHApprovalCreateApi(dpReqDto, true)));
	}

	/**
	 * 권한 즉시 반영
	 *
	 * @param reqId
	 * @param dpReqDto
	 * @return
	 */
	@PutMapping(value = "/v1/req/retry/authorize/{reqId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> dpReqRetryAuthorize(@PathVariable String reqId, @RequestBody DpReqDto dpReqDto) {
		dpReqDto.setReqId(reqId);
		dpReqDto.setModiId(SessionScopeUtil.getUserId());
		dpReqService.dpReqRetryAuthorize(dpReqDto);

		return ResponseEntity.ok(ApiResDto.success());
	}
	
	/**
	 * 태블로 추가권한 가져오기
	 * @throws Exception 
	 * 
	 * */
	@GetMapping(value = "/v1/req/tableau", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTableauAuth() throws Exception {
		return ResponseEntity.ok(ApiResDto.success(dpReqService.getVerticalProjectList()));
	}
	
	/**
     * 테이블 prefix/suffix 코드 정보 조회
     * @return
     */
    @GetMapping(value = "/v1/req/tblfix", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTblFixList() {
        return ResponseEntity.ok(ApiResDto.success(dpReqService.getTblFixList()));
    }
    
    /**
	 * 표준화검토 컬럼 정보 조회
	 * 
	 * @param colInfoDto
	 * @return
	 */
	@GetMapping(value = "/v1/colinfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDpColInfoList(ColInfoDto colInfoDto) {
		return ResponseEntity.ok(ApiResDto.success(dpReqService.getDpColInfoList(colInfoDto)));
    }

	/**
	 * 시스템 AD 계정 유효성 검사
	 * @param accountList
	 * @return 존재 여부
	 */
	@PostMapping(value ="/v1/req/ldap/valid-check", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> validCheckAccount(@RequestBody List<String> accountList) {
		Map<String, Boolean> validationResults = dpReqService.isValidCheckAccount(accountList);
		return ResponseEntity.ok(ApiResDto.success(Map.of("result", validationResults)));
	}

    /**
     * 권한에 대한 사용자 목록 조회
     *
     * @param authIds
     * @return
     */
    @GetMapping(value = "/v1/req/userbyauth/{authIds}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserLIstByAuth(@PathVariable String authIds) {
        List<String> authIdsList = null;
        if(!"".equals(authIds)) {
            authIdsList = Arrays.asList(authIds.split(","));
        }
        return ResponseEntity.ok(ApiResDto.success(dpReqService.getUserLIstByAuth(authIdsList)));
    }

    /**
     * 표준화검토 현황 - 적용
     *
     * @param reqId
     * @param dpReqDto
     * @return
     */
    @PutMapping(value = "/v1/req/apply/{reqId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateApply(@PathVariable String reqId, @RequestBody DpReqDto dpReqDto) {
        dpReqDto.setReqId(reqId);
        dpReqDto.setModiId(SessionScopeUtil.getUserId());
        dpReqService.updateApply(dpReqDto);

        return ResponseEntity.ok(ApiResDto.success());
    }

	/**
	 * 표준화검토 - 미등록 용어|단어 데이터 조회
	 *
	 * @param reqId
	 * @return
	 */
	@GetMapping(value = "/v1/req/unreg-list/{reqId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUnRegListById(@PathVariable String reqId) {
		return ResponseEntity.ok(ApiResDto.success(dpReqService.getUnRegListById(reqId)));
	}

	/**
	 * 표준화검토 - 스키마 리스트 조회
	 * @return
	 */
	@GetMapping(value = "/v1/req/schema-list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSchemaList() {
		return ResponseEntity.ok(ApiResDto.success(dpReqService.getSchemaList()));
	}

	/**
	 * 스키마 생성 신청 - 스키마명 중복 체크
	 *
	 * @param schemaNm
	 * @return
	 */
	@GetMapping(value = "/v1/req/schema-duplchk/{schemaNm}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSchemaNmDuplChk(@PathVariable String schemaNm) {
		return ResponseEntity.ok(ApiResDto.success(dpReqService.getSchemaNmDuplChk(schemaNm)));
	}
}
