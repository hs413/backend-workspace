package hs.wdp.app.req.service;

import in.woowa.data.portal.app.core.dw.stdterm.model.StdTermModel;
import in.woowa.data.portal.app.core.dw.stdword.model.StdWordModel;
import in.woowa.data.portal.app.core.glue.model.GlueDbModel;
import in.woowa.data.portal.app.core.ranger.service.RangerService;
import in.woowa.data.portal.app.core.user.model.UserModel;
import in.woowa.data.portal.common.CommonConstants;
import in.woowa.data.portal.common.CommonConstants.ALRM;
import in.woowa.data.portal.common.CommonConstants.DP_REQ;
import in.woowa.data.portal.common.CommonConstants.DP_REQ_STATUS;
import in.woowa.data.portal.common.dto.ColInfoDto;
import in.woowa.data.portal.common.dto.DpReqDto;
import in.woowa.data.portal.common.dto.DpReqStatsDto;
import in.woowa.data.portal.common.dto.TblFixDto;
import in.woowa.data.portal.common.mapper.DpReqMapper;
import in.woowa.data.portal.common.mapper.IdMapper;
import in.woowa.data.portal.common.util.CommonUtil;
import in.woowa.data.portal.common.util.IdUtil;
import in.woowa.data.portal.common.util.SessionScopeUtil;
import in.woowa.data.portal.common.util.StringUtils;
import in.woowa.data.portal.error.WdpError;
import in.woowa.data.portal.error.WdpException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
@RequiredArgsConstructor
public class DpReqService {

	@Autowired
	DpReqMapper dpReqMapper;

    @Autowired
    IdMapper idMapper;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	TableauService tableauService;
	
	@Autowired
	IdUtil idUtil;
	
	@Autowired
	private RangerService rangerService;
	
	@Autowired
	private KpiIndexReqService kpiIndexReqService;

	private final AlrmService alrmService;

    private final CommonUtil commonUtil;

	@Autowired
	LdapService ldapService;

	/**
	 * 전체 갯수
	 * 
	 * @param dpReqDto
	 * @return
	 */
	public int selectCount(DpReqDto dpReqDto) {
		return dpReqMapper.selectCount(dpReqDto);
	}

	/**
     * 권한신청 목록
     * 
     * @param dpReqDto
     * @return
     */
	public List<DpReqDto> selectAll(DpReqDto dpReqDto) {
		dpReqDto.setPaging(dpReqMapper.selectCount(dpReqDto));
		return dpReqMapper.selectAll(dpReqDto);
	}

	/**
	 * 대시보드 > 결재 현황 목록
	 *
	 * @param dpReqDto
	 * @return
	 */
	public List<DpReqDto> selectAprvAll(DpReqDto dpReqDto) {
		dpReqDto.setPaging(dpReqMapper.selectAprvCount(dpReqDto));
		return dpReqMapper.selectAprvAll(dpReqDto);
	}

	/**
	 * 대시보드 > 결재 현황 목록 엑셀다운로드
	 *
	 * @param dpReqDto
	 * @return
	 */
	@Transactional(readOnly = true)
	public DpReqDto.Result excelDpReqAprvList(DpReqDto dpReqDto) {

		return DpReqDto.Result.builder()
				.search(dpReqDto)
				.contents(dpReqMapper.excelDownload(dpReqDto))
				.build();
	}
	
	/**
	 * 미등록 단어 신청 내역 갯수
	 * 
	 * @param dpReqDto
	 * @return
	 */
	public int selectUnregistWordCount(DpReqDto dpReqDto) {
		return dpReqMapper.selectUnregistWordCount(dpReqDto);
	}
	
	/**
     * 미등록 단어 신청 내역
     * 
     * @param dpReqDto
     * @return
     */
	public List<DpReqDto> selectUnregistWordReqList(DpReqDto dpReqDto) {
		dpReqDto.setPaging(dpReqMapper.selectUnregistWordCount(dpReqDto));
		return dpReqMapper.selectUnregistWordReqList(dpReqDto);
	}
	
	/**
     * 신청리스트 엑셀다운로드
     * 
     * @param dpReqDto
     * @return
     */
	public List<DpReqDto> excelDownloadDpReqList(DpReqDto dpReqDto) {
		return dpReqMapper.excelDownloadDpReqList(dpReqDto);
	}

	/**
	 * 신청 리스트 상세 항목 엑셀 다운로드
	 *
	 * @param dpReqDto
	 * @return
	 */
	public List<DpReqDto> excelDownloadDetailList(DpReqDto dpReqDto) {

		List<DpReqDto> excelList = new ArrayList<>();
		try{
			//신청 카테고리 조회 (재식별/비식별/스키마)
			String category = dpReqDto.getCategory();
			//신청 엑셀 리스트 조회
			List<DpReqDto> reqList = dpReqMapper.excelDownloadDpReqList(dpReqDto);

			//데이터 정리
			for (DpReqDto req : reqList) {
				List<DpReqDto> splitData = new ArrayList<>();

				switch (category) {
					case DP_REQ.RE_IDENTIFY:
					case DP_REQ.DE_IDENTIFY:
						// JSON 데이터의 `targetData`를 파싱하고 schema별로 데이터를 처리
						splitData = generateIdentExcelList(req, category);
						break;
					case DP_REQ.SCHEMA_AUTH:
						// JSON 데이터의 `targetData`를 파싱하고 schema별로 데이터를 처리
						splitData = generateSchemaExcelList(req, category);
						break;
					default:
						continue;
				}
				excelList.addAll(splitData);
			}

		}
		catch (Exception e) {
			log.error("Error while generating excel list: ", e);
		}

		return excelList;
	}

	/**
	 * 데이터 정리 후 엑셀 리스트 반환 (식별/비식별 신청)
	 *
	 * @param req
	 * @return
	 */
	private List<DpReqDto> generateIdentExcelList(DpReqDto req, String category) {
		List<DpReqDto> excelList = new ArrayList<>();
		try {
			// reqContent JSON 파싱
			JSONObject reqContent = new JSONObject(req.getReqContent());

			JSONArray targetData = reqContent.optJSONArray("targetData")!=null ? reqContent.optJSONArray("targetData") : new JSONArray();

			// schema-테이블 기준으로 데이터를 그룹화
			Map<String, Map<String, List<String>>> schemaTableColumnMap = new HashMap<>();
			for (int i = 0; i < targetData.length(); i++) {
				JSONObject target = targetData.getJSONObject(i);
				String schema = target.optString("schema");
				String table = (category.equals(DP_REQ.RE_IDENTIFY) && target.optString("policyTblNm").isEmpty()) ? "*" : target.optString("policyTblNm"); // 테이블 정보
				String column = target.optString("column");

				// 스키마-테이블 맵에 컬럼 추가
				schemaTableColumnMap
						.computeIfAbsent(schema, k -> new HashMap<>()) // 스키마 그룹이 없으면 새로 추가
						.computeIfAbsent(table, k -> new ArrayList<>()) // 테이블 그룹이 없으면 새로 추가
						.add(column); // 컬럼 추가
			}

			DpReqDto newDto = new DpReqDto();
			newDto.setReqId(req.getReqId());
			newDto.setReqNo(req.getReqNo() != null ? req.getReqNo() : "");
			newDto.setReqCmt(req.getReqCmt());
			newDto.setType(req.getType());
			newDto.setStatus(req.getStatus());
			newDto.setAppr(req.getAppr());
			newDto.setCategory(req.getCategory());
			newDto.setRgstDt(req.getRgstDt());

			// JSON 데이터에 항목 추가
			JSONObject newReqContent = new JSONObject();
			newReqContent.put("externalLink", reqContent.optString("externalLink",""));
			newReqContent.put("grantStartDt", reqContent.optString("grantStartDt", ""));
			newReqContent.put("grantEndDt", reqContent.optString("grantEndDt", ""));
			newReqContent.put("realGrantStartDt", reqContent.optString("realGrantStartDt", ""));
			newReqContent.put("realGrantEndDt", reqContent.optString("realGrantEndDt", ""));
			newReqContent.put("rqstUserInfo", reqContent.optJSONObject("rqstUserInfo"));
			newReqContent.put("targetUserCnt", reqContent.optInt("targetUserCnt",0));
			newReqContent.put("externalLinkChk", reqContent.optBoolean("externalLinkChk",false));
			newReqContent.put("minimumMemberChk", reqContent.optBoolean("minimumMemberChk", false));
			newReqContent.put("repetitiveTaskChk", reqContent.optBoolean("repetitiveTaskChk", false));

			// targetUser 정보 추가 (콤마로 구분된 문자열)
			JSONArray targetUsers = reqContent.optJSONArray("targetUser")!=null ? reqContent.optJSONArray("targetUser") : new JSONArray();
			if (targetUsers != null && !targetUsers.isEmpty()) {
				List<String> userList = new ArrayList<>();
				for (int i = 0; i < targetUsers.length(); i++) {
					JSONObject user = targetUsers.getJSONObject(i);
					String userNm = user.optString("userNm","");
					String deptNm = user.optString("deptNm","-");
					userList.add(userNm + "(" + deptNm + ")");
				}
				newReqContent.put("targetUsers", String.join(", ", userList));
			}

			// targetSystem 정보 추가 (콤마로 구분된 문자열)
			JSONArray targetSystems = reqContent.optJSONArray("targetSystem")!=null ? reqContent.optJSONArray("targetSystem") : new JSONArray();
			if (targetSystems != null && !targetSystems.isEmpty()) {
				List<String> systemList = new ArrayList<>();
				for (int i = 0; i < targetSystems.length(); i++) {
					JSONObject user = targetSystems.getJSONObject(i);
					String systemId = user.optString("systemId","");
					systemList.add(systemId);
				}
				newReqContent.put("targetSystems", String.join(", ", systemList));
			}

			// apprLine 데이터 처리
			JSONObject apprInfo = req.getApprLine()!=null ? new JSONObject(req.getApprLine()) : new JSONObject();
			JSONArray apprLine = apprInfo.optJSONArray("apprLine");
			if (apprLine != null) {
				newDto.setApprLine(apprLine.toString()); // apprLine 데이터를 JSON 배열로 문자열화
			}

			if (!schemaTableColumnMap.isEmpty()) {
				// 각 schema-테이블에 대해 새로운 DpReqDto 생성
				for (Map.Entry<String, Map<String, List<String>>> schemaEntry : schemaTableColumnMap.entrySet()) {
					String schema = schemaEntry.getKey();
					Map<String, List<String>> tableColumnMap = schemaEntry.getValue();

					for (Map.Entry<String, List<String>> tableEntry : tableColumnMap.entrySet()) {
						//dto 깊은 복사
						DpReqDto newDto2 = deepCopyDto(newDto);
						JSONObject newReqContent2 = new JSONObject(newReqContent.toString());

						String table = tableEntry.getKey();
						List<String> columns = tableEntry.getValue();

						newReqContent2.put("schema", schema);
						newReqContent2.put("table", table);
						newReqContent2.put("columns", String.join(", ", columns)); // 컬럼을 콤마로 구분하여 추가
						newReqContent2.put("targetColumnCnt", reqContent.optInt("targetColumnCnt"));
						newReqContent2.put("targetSchemaCnt", reqContent.optInt("targetSchemaCnt"));

						newDto2.setReqContent(newReqContent2.toString());
						excelList.add(newDto2);
					}
				}
			}
			else {
				newDto.setReqContent(newReqContent.toString());
				excelList.add(newDto);
			}

		} catch (Exception e) {
			log.error("Error while splitting data for Ident excel List {}: {}", req.getReqId(), e.getMessage());
		}
		return excelList;
	}

	/**
	 * 데이터 정리 후 엑셀 리스트 반환 (스키마권한신청)
	 *
	 * @param req
	 * @return
	 */
	private List<DpReqDto> generateSchemaExcelList(DpReqDto req, String category) {
		List<DpReqDto> excelList = new ArrayList<>();
		try {
			// reqContent JSON 파싱
			JSONObject reqContent = new JSONObject(req.getReqContent());

			JSONArray targetData = reqContent.optJSONArray("targetData")!=null ? reqContent.optJSONArray("targetData") : new JSONArray();

			// layer-schema-table 기준으로 데이터를 그룹화
			Map<String, Map<String, List<String>>> layerSchemaTableMap = new HashMap<>();
			Map<String, String> schemaOwnerMap = new HashMap<>();
			for (int i = 0; i < targetData.length(); i++) {
				JSONObject target = targetData.getJSONObject(i);
				String layer = target.optString("layerNm");
				String schema = target.optString("schemaNm");
				String table = target.optString("tableNm", "*");
				String schemaOwnerNm = target.optString("schemaOwnerNm");

				// 레이어-스키마 맵에 컬럼 추가
				layerSchemaTableMap
						.computeIfAbsent(layer, k -> new HashMap<>()) // 레이어 그룹이 없으면 새로 추가
						.computeIfAbsent(schema, k -> new ArrayList<>()) // 스키마 그룹이 없으면 새로 추가
						.add(table); // 컬럼 추가

				// 스키마 오너 담당자
				schemaOwnerMap.put(schema, schemaOwnerNm);
			}

			DpReqDto newDto = new DpReqDto();
			newDto.setReqId(req.getReqId());
			newDto.setReqNo(req.getReqNo() != null ? req.getReqNo() : "");
			newDto.setReqCmt(req.getReqCmt());
			newDto.setType(req.getType());
			newDto.setStatus(req.getStatus());
			newDto.setAppr(req.getAppr());
			newDto.setCategory(req.getCategory());
			newDto.setRgstDt(req.getRgstDt());

			// JSON 데이터에 항목 추가
			JSONObject newReqContent = new JSONObject();
			newReqContent.put("grantStartDt", reqContent.optString("grantStartDt",""));
			newReqContent.put("grantEndDt", reqContent.optString("grantEndDt", ""));
			newReqContent.put("realGrantStartDt", reqContent.optString("realGrantStartDt", ""));
			newReqContent.put("realGrantEndDt", reqContent.optString("realGrantEndDt", ""));
			newReqContent.put("rqstUserInfo", reqContent.optJSONObject("rqstUserInfo"));
			newReqContent.put("targetUserCnt", reqContent.optInt("targetUserCnt",0));

			// targetUser 정보 추가 (콤마로 구분된 문자열)
			JSONArray targetUsers = reqContent.optJSONArray("targetUser")!=null ? reqContent.optJSONArray("targetUser") : new JSONArray();
			if (targetUsers != null && !targetUsers.isEmpty()) {
				List<String> userList = new ArrayList<>();
				for (int i = 0; i < targetUsers.length(); i++) {
					JSONObject user = targetUsers.getJSONObject(i);
					String userNm = user.optString("userNm","");
					String deptNm = user.optString("deptNm","-");
					userList.add(userNm + "(" + deptNm + ")");
				}
				newReqContent.put("targetUsers", String.join(", ", userList));
			}

			// targetSystem 정보 추가 (콤마로 구분된 문자열)
			JSONArray targetSystems = reqContent.optJSONArray("targetSystem")!=null ? reqContent.optJSONArray("targetSystem") : new JSONArray();
			if (targetSystems != null && !targetSystems.isEmpty()) {
				List<String> systemList = new ArrayList<>();
				for (int i = 0; i < targetSystems.length(); i++) {
					JSONObject user = targetSystems.getJSONObject(i);
					String systemId = user.optString("systemId","");
					systemList.add(systemId);
				}
				newReqContent.put("targetSystems", String.join(", ", systemList));
			}

			// apprLine 데이터 처리
			JSONObject apprInfo = req.getApprLine()!=null ? new JSONObject(req.getApprLine()) : new JSONObject();
			JSONArray apprLine = apprInfo.optJSONArray("apprLine");
			if (apprLine != null) {
				newDto.setApprLine(apprLine.toString()); // apprLine 데이터를 JSON 배열로 문자열화
			}

			if (!layerSchemaTableMap.isEmpty()) {
				// 각 layer-schema-테이블에 대해 새로운 DpReqDto 생성
				for (Map.Entry<String, Map<String, List<String>>> layerEntry : layerSchemaTableMap.entrySet()) {
					String layer = layerEntry.getKey();
					Map<String, List<String>> schemaMap = layerEntry.getValue();

					for (Map.Entry<String, List<String>> schemaEntry : schemaMap.entrySet()) {
						//dto 깊은 복사
						DpReqDto newDto2 = deepCopyDto(newDto);
						JSONObject newReqContent2 = new JSONObject(newReqContent.toString());

						String schema = schemaEntry.getKey();
						List<String> tables = schemaEntry.getValue();
						String schemaOwnerNm = schemaOwnerMap.get(schema);

						newReqContent2.put("layer", layer);
						newReqContent2.put("schema", schema);
						newReqContent2.put("tables", String.join(", ", tables)); // 컬럼을 콤마로 구분하여 추가
						newReqContent2.put("schemaOwnerNm", schemaOwnerNm);

						newDto2.setReqContent(newReqContent2.toString());
						excelList.add(newDto2);
					}
				}
			}
			else {
				newDto.setReqContent(newReqContent.toString());
				excelList.add(newDto);
			}

		} catch (Exception e) {
			log.error("Error while splitting data for schema excel List {}: {}", req.getReqId(), e.getMessage());
		}
		return excelList;
	}

	/**
	 * DpReqDto Mst 정보 깊은 복사
	 * @param original
	 * @return
	 */
	private DpReqDto deepCopyDto(DpReqDto original) {
		DpReqDto copy = new DpReqDto();
		copy.setReqId(original.getReqId());
		copy.setReqNo(original.getReqNo() != null ? original.getReqNo() : "");
		copy.setReqCmt(original.getReqCmt());
		copy.setStatus(original.getStatus());
		copy.setAppr(original.getAppr());
		copy.setCategory(original.getCategory());
		copy.setRgstDt(original.getRgstDt());
		copy.setApprLine(original.getApprLine());
		copy.setType(original.getType());
		return copy;
	}

	/**
	 * 상세 조회
	 * @param dpReqDto
	 * @return
	 */
	public DpReqDto selectDetail(String reqId) {
		return dpReqMapper.selectDetail(reqId);
	}
	
	/**
     * 신청 삭제
     *
     * @param reqId
     */
    @Transactional
    public void deleteDpReq(DpReqDto dpReqDto) {
    	dpReqMapper.deleteDpReq(dpReqDto);
	}
    
	/**
     * 신청 내역 메인 카운팅
	 * @return
     */
    @Transactional
    public DpReqStatsDto getDpReqCounts(DpReqDto dpReqDto) {
    	return dpReqMapper.getDpReqCounts(dpReqDto);
	}      
	
	/**
	 * 이미 신청한 단어인지 확인
	 * 
	 * @param dpReqDto
	 * @return
	 */
	public DpReqDto checkWordDuplication(StdWordModel stdWordModel) {
		return dpReqMapper.checkWordDuplication(stdWordModel);
	}

    /**
     * API검증시 미등록단어존재인 경우인데 신청 진행 중인 단어인지 확인
     *
     * @param dpReqDto
     * @return
     */
    public DpReqDto checkUnregisteredWordDuplication(StdWordModel stdWordModel) {
        return dpReqMapper.checkUnregisteredWordDuplication(stdWordModel);
    }
	
	/**
	 * 이미 신청한 용어인지 확인
	 * 
	 * @param dpReqDto
	 * @return
	 */
	 public DpReqDto checkTermDuplication(StdTermModel stdTermModel) {
		return dpReqMapper.checkTermDuplication(stdTermModel);
	}	
	
	/**
     * 권한신청 히스토리 조회
     * 
     * @param dpReqDto
     * @return
     */
    public List<DpReqDto> selectHist(DpReqDto dpReqDto) {
    	return dpReqMapper.selectHist(dpReqDto);
    }
    
    /**
     * 권한신청 상세 히스토리 조회
     * 
     * @param dpReqDto
     * @return
     */
    public List<DpReqDto> selectHistDtl(DpReqDto dpReqDto) {
    	return dpReqMapper.selectHistDtl(dpReqDto);
    }
	
    /**
     *   스케줄러가 실행할 때 단어[단건]신청 건 등록완료 처리  
     * @return
     */
    public int updateCompletionWordTarget() {
    	int result = dpReqMapper.updateCompletionWordTarget();
    	
    	// 알림
    	List<DpReqDto> list = dpReqMapper.selectTodayCompletionWordTarget();
    	if(list != null) {
    		for(int i = 0, len = list.size(); i < len; i++) {
    			alrmService.sendDpAlarm(ALRM.REQ_WORD_DONE, list.get(i).getReqId());
    		}
    	}
    	    	
    	return result;
    }
    
    /**
     * 스케줄러가 실행할 때 용어[단건]신청 건 등록완료 처리
     * @return
     */
    public int updateCompletionTermTarget() {
    	int result = dpReqMapper.updateCompletionTermTarget();
    	
    	// 알림
    	List<DpReqDto> list = dpReqMapper.selectTodayCompletionTermTarget();
    	if(list != null) {
    		for(int i = 0, len = list.size(); i < len; i++) {
    			alrmService.sendDpAlarm(ALRM.REQ_TERM_DONE, list.get(i).getReqId());
    		}
    	}
    	
    	return result;
    }
    
    /**
     * 스케줄러가 실행할 때 표준화검토 신청 건 등록완료 처리
     * @return
     */
    public int updateCompletionStdReviewTarget() {
    	int result = dpReqMapper.updateCompletionStdReviewTarget();
    	
    	// 알림
    	List<DpReqDto> list = dpReqMapper.selectTodayCompletionStdReviewTarget();
    	if(list != null) {
    		for(int i = 0, len = list.size(); i < len; i++) {
    			alrmService.sendDpAlarm(ALRM.REQ_STD_REVIEW_DONE, list.get(i).getReqId());
    		}
    	}
    	
    	return result;
    }
    
	/**
     * 권한신청 등록
     *  
     * @param dpReqDto
     * @return
     */
    public String insertMst(DpReqDto dpReqDto) {
        int result = 0;
        String id = "";

        try {
            id = idUtil.getSecureUUID();
            dpReqDto.setReqId(id);

            dHApprovalCreateApi(dpReqDto, false);

            // 신청SEQ 채번
            dpReqDto.setSeqNo(idMapper.nextReqSeqNo(dpReqDto));

            result = dpReqMapper.insertMst(dpReqDto);

            if(result <= 0) {
                throw new WdpException(WdpError.DP_REQ_MST_INSERT_FAILED);
            }

            dpReqMapper.insertMstHist(dpReqDto);

            result = dpReqMapper.insertDetail(dpReqDto);

            if(result <= 0) {
                throw new WdpException(WdpError.DP_REQ_DTL_INSERT_FAILED);
            }

            dpReqMapper.insertDetailHist(dpReqDto);

            //신청 공통 알람
            this.reqCommSendDpAlarm(dpReqDto);
        }
        catch(WdpException e) {
            log.debug(e.getMessage());
            result = -1;
        }
        catch(Exception e) {
            result = -1;
        }

        return id;
    }

	/**
     * 권한신청 수정 분기
     * 
     * @param dpReqDto
     * @return
     */
	public int updateDpReq(DpReqDto dpReqDto){

		//외부결재(ri: 재식별(column), di: 비식별(function), tb: 태블로권한신청, sa: 스키마권한신청)인지 확인
		String category = dpReqDto.getCategory();
		if(StringUtils.str(category).notIn(DP_REQ.TABLEAU, DP_REQ.RE_IDENTIFY, DP_REQ.DE_IDENTIFY,
                DP_REQ.SCHEMA_AUTH)
			|| StringUtils.str(dpReqDto.getStatus()).notIn("ai","r")
		){
			return updateMst(dpReqDto);
		}

		return dHApprovalApi(dpReqDto, category);
	}
    
    /**
     * 권한신청 수정
     * 
     * @param dpReqDto
     * @return
     */
    public int updateMst(DpReqDto dpReqDto) {

    	int result = dpReqMapper.updateMst(dpReqDto);
    	
    	if(result > 0) {
    		dpReqMapper.insertMstHist(dpReqDto);

            /**
             * 알람 제외
             * 배치대상
             * 표준화검토 - 표준사전 신청승인(uptType='stdApply')
             * 표준화검토 - 적용(uptType='apply')
             */
            if(dpReqDto.getUptType() == null) {
                //신청 공통 알람
                this.reqCommSendDpAlarm(dpReqDto);
            }
    	}
    	
    	return result;
    }
    
    /**
     * 신청 공통 알람
     * @param dpReqDto
     */
    public void reqCommSendDpAlarm(DpReqDto dpReqDto) {
		String tempId = "";	//tempId
		String currStatus 	= dpReqDto.getStatus();				//상태
		String currCategory = dpReqDto.getCategory();			//카테고리
		
		JSONObject alrmMsgParams = new JSONObject();			//알림메세지 Params
		
		//신청완료
		if(DP_REQ_STATUS.READY.equals(currStatus)) {
			alrmMsgParams.put("reqCmt", StringUtils.safeToString(dpReqDto.getReqCmt()));		//신청사유
			
			switch(currCategory) {
				//단어
				case DP_REQ.STD_WORD:
					tempId = ALRM.REQ_WORD_APRV_WILL;
					break;
				//용어
				case DP_REQ.STD_TERM:
					tempId = ALRM.REQ_TERM_APRV_WILL;
					break;
				//표준화검토
				case DP_REQ.STD_REVIEW:
					tempId = ALRM.REQ_STD_REVIEW_APRV_WILL;
					break;
				//스키마 생성 신청
				case DP_REQ.SCHEMA_CREATE:
					tempId = ALRM.REQ_SCHEMA_CREATE_APRV_WILL;
					break;

				// Global 데이터
				case DP_REQ.GLOBAL_DATA:
					tempId = ALRM.REQ_GLOBAL_DATA_APRV_WILL;
					break;
				default:
					break;
			}
		}
		//검토중
		else if(DP_REQ_STATUS.REVIEW.equals(currStatus)) {
			switch(currCategory) {
				//단어
				case DP_REQ.STD_WORD:
					tempId = ALRM.REQ_WORD_DOING;
					break;
				//용어
				case DP_REQ.STD_TERM:
					tempId = ALRM.REQ_TERM_DOING;
					break;
				//표준화검토
				case DP_REQ.STD_REVIEW:
					tempId = ALRM.REQ_STD_REVIEW_DOING;
					break;
				//스키마 생성 신청
				case DP_REQ.SCHEMA_CREATE:
					tempId = ALRM.REQ_SCHEMA_CREATE_DOING;
					break;

				// Global 데이터
				case DP_REQ.GLOBAL_DATA:
					tempId = ALRM.REQ_GLOBAL_DATA_DOING;
					break;
				default:
					break;
			}
		}
		//수정필요
		else if(DP_REQ_STATUS.REQ_MODIFY.equals(currStatus)) {
			alrmMsgParams.put("reqCmt", StringUtils.safeToString(dpReqDto.getProcCmt()));		//수정사유
			
			switch(currCategory) {
				//단어
				case DP_REQ.STD_WORD:
					tempId = ALRM.REQ_WORD_MODIFY_REQ;
					break;
				//용어
				case DP_REQ.STD_TERM:
					tempId = ALRM.REQ_TERM_MODIFY_REQ;
					break;
				//표준화검토
				case DP_REQ.STD_REVIEW:
					tempId = ALRM.REQ_STD_REVIEW_MODIFY_REQ;
					break;
				//스키마 생성 신청
				case DP_REQ.SCHEMA_CREATE:
					tempId = ALRM.REQ_SCHEMA_CREATE_MODIFY_REQ;
					break;

				// Global 데이터
				case DP_REQ.GLOBAL_DATA:
					tempId = ALRM.REQ_GLOBAL_DATA_MODIFY_REQ;
					break;
				default:
					break;
			}
		}
		//수정완료
		else if(DP_REQ_STATUS.MODIFY_DONE.equals(currStatus)) {
			switch(currCategory) {
				//단어
				case DP_REQ.STD_WORD:
					tempId = ALRM.REQ_WORD_MODIFY_APRV;
					break;
				//용어
				case DP_REQ.STD_TERM:
					tempId = ALRM.REQ_TERM_MODIFY_APRV;
					break;
				//표준화검토
				case DP_REQ.STD_REVIEW:
					tempId = ALRM.REQ_STD_REVIEW_MODIFY_APRV;
					break;
				//스키마 생성 신청
				case DP_REQ.SCHEMA_CREATE:
					tempId = ALRM.REQ_SCHEMA_CREATE_MODIFY_APRV;
					break;

				// Global 데이터
				case DP_REQ.GLOBAL_DATA:
					tempId = ALRM.REQ_GLOBAL_DATA_MODIFY_APRV;
					break;
				default:
					break;
			}
		}
		//반려
		else if(DP_REQ_STATUS.REJECT.equals(currStatus)) {
			alrmMsgParams.put("rejectCmt", StringUtils.safeToString(dpReqDto.getProcCmt()));	//반려사유
			DpReqDto selector = selectOneToOnlyId(dpReqDto);
			String reqContent = StringUtils.safeToString(selector.getReqContent());

			JSONObject reqContObj = null;

			//JSONObject일 경우만(배열 제외)
			if(reqContent.startsWith("{")) {
				reqContObj = new JSONObject(selector.getReqContent());
			}

			JSONArray targetUser = new JSONArray();
			JSONArray targetSystem = new JSONArray();
			JSONArray targetData = new JSONArray();

			switch(currCategory) {
				//단어
				case DP_REQ.STD_WORD:
					tempId = ALRM.REQ_WORD_REJECT;
					break;
				//용어
				case DP_REQ.STD_TERM:
					tempId = ALRM.REQ_TERM_REJECT;
					break;
				//표준화검토
				case DP_REQ.STD_REVIEW:
					tempId = ALRM.REQ_STD_REVIEW_REJECT;
					break;
				//스키마 생성 신청
				case DP_REQ.SCHEMA_CREATE:
					tempId = ALRM.REQ_SCHEMA_CREATE_REJECT;
					break;
				//태블로 권한
				case DP_REQ.TABLEAU:
					String authNm = reqContObj.optString("auth");
					String authInfo = authNm.equals(CommonConstants.TABLEAU.CREATOR.toUpperCase()) ? authNm + "(" + reqContObj.optString("projectNm") + ")" : authNm;
					alrmMsgParams.put("authInfo", authInfo);

					JSONArray authority = reqContObj.optJSONArray("authority");
					alrmMsgParams.put("targetUserList", alrmService.targetUserList(authority, null, currCategory));
					tempId = ALRM.REQ_TABLEAU_AUTH_REJ;
					break;
				//재식별화 권한
				case DP_REQ.RE_IDENTIFY:
					targetUser = reqContObj.optJSONArray("targetUser");
					targetSystem = reqContObj.optJSONArray("targetSystem");
					alrmMsgParams.put("targetUserList", alrmService.targetUserList(targetUser, targetSystem, currCategory));

					targetData = reqContObj.optJSONArray("targetData");
					alrmMsgParams.put("targetDataList", alrmService.targetDataList(targetData, currCategory));
					tempId = ALRM.REQ_REIDENT_AUTH_REJ;
					break;
				//비식별화 권한
				case DP_REQ.DE_IDENTIFY:
					targetUser = reqContObj.optJSONArray("targetUser");
					targetSystem = reqContObj.optJSONArray("targetSystem");
					alrmMsgParams.put("targetUserList", alrmService.targetUserList(targetUser, targetSystem, currCategory));

					tempId = ALRM.REQ_DEIDENT_AUTH_REJ;
					break;
				// Global 데이터
				case DP_REQ.GLOBAL_DATA:
					tempId = ALRM.REQ_GLOBAL_DATA_REJECT;
					break;
				// 스키마 권한
				case DP_REQ.SCHEMA_AUTH:
					String typeNm = "W".equals(reqContObj.optString("type")) ? CommonConstants.DP_REQ_TYPE.SCHEMA_WRITE : CommonConstants.DP_REQ_TYPE.SCHEMA_READ;
					alrmMsgParams.put("type", typeNm);

					targetUser = reqContObj.optJSONArray("targetUser");
					targetSystem = reqContObj.optJSONArray("targetSystem");
					alrmMsgParams.put("targetUserList", alrmService.targetUserList(targetUser, targetSystem, currCategory));

					targetData = reqContObj.optJSONArray("targetData");
					alrmMsgParams.put("targetDataList", alrmService.targetDataList(targetData, currCategory));
					tempId = ALRM.REQ_SCHEMA_AUTH_REJ;
					break;
				default:
					break;
			}
		}
		//작업완료
		else if(DP_REQ_STATUS.COMPLETE.equals(currStatus)) {
			switch(currCategory) {
				//단어
				case DP_REQ.STD_WORD:
					tempId = ALRM.REQ_WORD_DONE;
					break;
				//용어
				case DP_REQ.STD_TERM:
					tempId = ALRM.REQ_TERM_DONE;
					break;
				//표준화검토
				case DP_REQ.STD_REVIEW:
					tempId = ALRM.REQ_STD_REVIEW_DONE;
					break;
				//스키마 생성 신청
				case DP_REQ.SCHEMA_CREATE:
					tempId = ALRM.REQ_SCHEMA_CREATE_DONE;
					break;
				// Global 데이터
				case DP_REQ.GLOBAL_DATA:
					tempId = ALRM.REQ_GLOBAL_DATA_DONE;
					break;
				default:
					break;
			}
		}
		
		/* 태블로 | 재식별 | 비식별 | 스키마권한 신청완료 시 별도 처리(반려 제외) */
		if(! "".equals(tempId)) {
			alrmService.sendDpAlarm(tempId, dpReqDto.getReqId(), alrmMsgParams);
		}
		else {
			//신청완료
			if(DP_REQ_STATUS.READY.equals(currStatus)) {
				DpReqDto selector = selectOneToOnlyId(dpReqDto);
				JSONObject reqContObj = new JSONObject(selector.getReqContent());

				//태블로 권한
				if(DP_REQ.TABLEAU.equals(currCategory)) {
					JSONArray authority  = reqContObj.optJSONArray("authority");				//권한부여자 정보
					JSONObject requestor  = reqContObj.getJSONObject("requestor");				//신청자 정보
					String auth = reqContObj.optString("auth");

					//1차 결재요청 알림
					String authInfo = auth.equals(CommonConstants.TABLEAU.CREATOR.toUpperCase()) ? auth + "(" + reqContObj.optString("projectNm") + ")" : auth;
					alrmMsgParams.put("authInfo", authInfo);
					alrmMsgParams.put("targetUserList", alrmService.targetUserList(authority, null, currCategory));
					alrmService.sendDpAlarm(ALRM.REQ_TABLEAU_APRV_WILL, dpReqDto.getReqId(), "1", alrmMsgParams);

					//권한 신청 등록 알림(권한부여 대상자)
					String rqstNm = StringUtils.safeToString(requestor.getString("userNm"));	//신청자명
					String rqstId = StringUtils.safeToString(requestor.getString("userId"));	//신청자ID
					String authNm = StringUtils.safeToString(alrmService.targetUserInfo(authority));	//권한부여자명

					alrmMsgParams.clear();
					alrmMsgParams.put("rqstNm", rqstNm);
					alrmMsgParams.put("rqstId", rqstId);
					alrmMsgParams.put("userNm", authNm);
					
					alrmService.sendDpAlarm(ALRM.REQ_TABLEAU_AUTH_REG, dpReqDto.getReqId(), alrmMsgParams);
				}
				//재식별화 권한
				else if(DP_REQ.RE_IDENTIFY.equals(currCategory)) {
					//권한 신청 등록 알림(권한부여 대상자)
					alrmService.sendDpAlarm(ALRM.REQ_REIDENT_AUTH_REG, dpReqDto.getReqId());
					
					//권한 신청 등록 알림(참조부서)
					alrmService.sendDpAlarm(ALRM.REQ_REIDENT_AUTH_REG_MNGR, dpReqDto.getReqId(), alrmMsgParams);

					//1차 결재요청 알림
					JSONArray targetUser = reqContObj.optJSONArray("targetUser");
					JSONArray targetSystem = reqContObj.optJSONArray("targetSystem");
					alrmMsgParams.put("targetUserList", alrmService.targetUserList(targetUser, targetSystem, currCategory));

					JSONArray targetData = reqContObj.optJSONArray("targetData");
					alrmMsgParams.put("targetDataList", alrmService.targetDataList(targetData, currCategory));
					alrmService.sendDpAlarm(ALRM.REQ_REIDENT_APRV_WILL, dpReqDto.getReqId(), "1", alrmMsgParams);
				}
				//비식별화 권한
				else if(DP_REQ.DE_IDENTIFY.equals(currCategory)) {
					//권한 신청 등록 알림(권한부여 대상자)
					alrmService.sendDpAlarm(ALRM.REQ_DEIDENT_AUTH_REG, dpReqDto.getReqId());
					
					//권한 신청 등록 알림(참조부서)
					alrmService.sendDpAlarm(ALRM.REQ_DEIDENT_AUTH_REG_MNGR, dpReqDto.getReqId(), alrmMsgParams);

					//1차 결재요청 알림
					JSONArray targetUser = reqContObj.optJSONArray("targetUser");
					JSONArray targetSystem = reqContObj.optJSONArray("targetSystem");
					alrmMsgParams.put("targetUserList", alrmService.targetUserList(targetUser, targetSystem, currCategory));
					alrmService.sendDpAlarm(ALRM.REQ_DEIDENT_APRV_WILL, dpReqDto.getReqId(), "1", alrmMsgParams);
				}
				//스키마 권한
				else if(DP_REQ.SCHEMA_AUTH.equals(currCategory)) {
					//권한 신청 등록 알림(권한부여 대상자)
					alrmService.sendDpAlarm(ALRM.REQ_SCHEMA_AUTH_REG, dpReqDto.getReqId());

					//1차 결재요청 알림
					String typeNm = "W".equals(reqContObj.optString("type")) ? CommonConstants.DP_REQ_TYPE.SCHEMA_WRITE : CommonConstants.DP_REQ_TYPE.SCHEMA_READ;
					alrmMsgParams.put("type", typeNm);

					JSONArray targetUser = reqContObj.optJSONArray("targetUser");
					JSONArray targetSystem = reqContObj.optJSONArray("targetSystem");
					alrmMsgParams.put("targetUserList", alrmService.targetUserList(targetUser, targetSystem, currCategory));

					JSONArray targetData = reqContObj.optJSONArray("targetData");
					alrmMsgParams.put("targetDataList", alrmService.targetDataList(targetData, currCategory));
					alrmService.sendDpAlarm(ALRM.REQ_SCHEMA_AUTH_APRV_WILL, dpReqDto.getReqId(), "1", alrmMsgParams);
				}
			}
		}
    }
    
    /**
     * 권한신청 상세 수정
     * 
     * @param dpReqDto
     * @return
     */
    public int updateDetail(DpReqDto dpReqDto) {
    	int result = dpReqMapper.updateDetail(dpReqDto);
    	
    	if(result > 0) {
    		dpReqMapper.insertDetailHist(dpReqDto);
    	}
    	
    	return result;
    }

    /**
     * 신청내역 특정 요소 수정
     *
     * @param dpReqDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateReqDtlByObjId(DpReqDto dpReqDto) {
        dpReqMapper.lockReqDtlRow(dpReqDto.getReqId());

        int updateCount = dpReqMapper.updateDtlByObjId(dpReqDto);
        return updateCount;

        // 표준화검토 신청내역 단어/용어 변경 할 경우 사용
//        if (updateCount > 0 && dpReqDto.getReviewRefId() != null && !dpReqDto.getReviewRefId().isEmpty()) {
//            String category = dpReqDto.getCategory();
//            if(CommonConstants.DP_REQ.STD_WORD.equals(category)) {
//                // 단어 변경
//                dpReqMapper.updateReviewDtlWord(dpReqDto);
//            } else if (CommonConstants.DP_REQ.STD_TERM.equals(category)) {
//                // 용어 변경
//                dpReqMapper.updateReviewDtlTerm(dpReqDto);
//            }
//        }
    }

	/**
     * 외부결재 생성
     * 
     * @param dpReqDto
     * @return
     */
	public DpReqDto dHApprovalCreateApi(DpReqDto dpReqDto, boolean isCreate){

		//외부결재(ri: 재식별(column), di: 비식별(function), tb: 태블로권한신청, sa: 스키마권한신청)인지 확인
		//신청완료 후 재생성 시 기존 apprLine이 존재하지 않는다면 실행X
		String category = dpReqDto.getCategory();
		if(StringUtils.str(category).notIn(DP_REQ.TABLEAU, DP_REQ.RE_IDENTIFY, DP_REQ.DE_IDENTIFY,
                DP_REQ.SCHEMA_AUTH)
			|| (!isCreate && (dpReqDto.getApprLine()==null || dpReqDto.getApprLine().isBlank()))){
			return dpReqDto;
		}

		approvalService.setApprovalLine(dpReqDto);

		return dpReqDto;
	}

	/**
     * 외부결재 수정(승인/반려)
     * 
     * @param dpReqDto
     * @return
     */
	private int dHApprovalApi(DpReqDto dpReqDto, String category){

		DpReqDto selector = selectOneToOnlyId(dpReqDto);
		dpReqDto.setApprLineId(selector.getApprLineId());
		dpReqDto.setAppr(selector.getAppr());

		//결재성공 판별
		if(!"200".equals(approvalService.updateApprovalLine(dpReqDto).get("statusCode").toString())){
			throw new WdpException(WdpError.DP_REQ_APPROVAL_API_FAILED);
		}
		
		//결재 성공 시 재 조회 후 appr, appr_line 변경
		JSONObject newLine = approvalService.selectApprovalLine(dpReqDto.getApprLineId());
		String appr = "COMPLETED".equals(newLine.getString("status")) ? "c":Integer.toString(newLine.getInt("currentStepId") - 1);
		dpReqDto.setAppr(appr);
		dpReqDto.setApprLine(newLine.toString());

		//내부DB 1차 update
		int result = updateMst(dpReqDto);
		if(("c".equals(appr) && (DP_REQ.RE_IDENTIFY.equals(category) || DP_REQ.DE_IDENTIFY.equals(category))
				|| ("2".equals(appr) && DP_REQ.SCHEMA_AUTH.equals(category)))){
			updateDetail(dpReqDto);
		}
		
		//후처리
		if("c".equals(appr)){
			//최종완료일 시 8로 변경
			dpReqDto.setStatus("8");
			endApprovalline(dpReqDto,category);
		}
		else {
			// 알림 - 다음 결제자 알림
			int currentStep = newLine.optInt("currentStepId", 0);
			
			if(currentStep > 0) {
				String step = Integer.toString(currentStep);
				JSONObject reqContent = new JSONObject(selector.getReqContent());		//신청상세

				JSONObject alrmMsgParams = new JSONObject();			//알림메세지 Params
				alrmMsgParams.put("reqCmt", StringUtils.safeToString(selector.getReqCmt()));		//신청사유

				String alrmType = "";

				if(DP_REQ.TABLEAU.equals(category)) {
					String authNm = reqContent.optString("auth");
					String authInfo = authNm.equals(CommonConstants.TABLEAU.CREATOR.toUpperCase()) ? authNm + "(" + reqContent.optString("projectNm") + ")" : authNm;
					alrmMsgParams.put("authInfo", authInfo);

					JSONArray authority = reqContent.optJSONArray("authority");
					alrmMsgParams.put("targetUserList", alrmService.targetUserList(authority, null, category));

					alrmType = ALRM.REQ_TABLEAU_APRV_WILL;
				}
				else if(DP_REQ.RE_IDENTIFY.equals(category)) {
					JSONArray targetUser = reqContent.optJSONArray("targetUser");
					JSONArray targetSystem = reqContent.optJSONArray("targetSystem");
					alrmMsgParams.put("targetUserList", alrmService.targetUserList(targetUser, targetSystem, category));

					JSONArray targetData = reqContent.optJSONArray("targetData");
					alrmMsgParams.put("targetDataList", alrmService.targetDataList(targetData, category));
					alrmType = ALRM.REQ_REIDENT_APRV_WILL;
				}
				else if(DP_REQ.DE_IDENTIFY.equals(category)) {
					JSONArray targetUser = reqContent.optJSONArray("targetUser");
					JSONArray targetSystem = reqContent.optJSONArray("targetSystem");
					alrmMsgParams.put("targetUserList", alrmService.targetUserList(targetUser, targetSystem, category));

					alrmType = ALRM.REQ_DEIDENT_APRV_WILL;
				}
				else if(DP_REQ.SCHEMA_AUTH.equals(category)) {
					String typeNm = "W".equals(reqContent.optString("type")) ? CommonConstants.DP_REQ_TYPE.SCHEMA_WRITE : CommonConstants.DP_REQ_TYPE.SCHEMA_READ;
					alrmMsgParams.put("type", typeNm);

					JSONArray targetUser = reqContent.optJSONArray("targetUser");
					JSONArray targetSystem = reqContent.optJSONArray("targetSystem");
					alrmMsgParams.put("targetUserList", alrmService.targetUserList(targetUser, targetSystem, category));

					JSONArray targetData = reqContent.optJSONArray("targetData");
					alrmMsgParams.put("targetDataList", alrmService.targetDataList(targetData, category));
					alrmType = ALRM.REQ_SCHEMA_AUTH_APRV_WILL;
				}
								
				alrmService.sendDpAlarm(alrmType, dpReqDto.getReqId(), step, alrmMsgParams);
			}
		}

		return result;
	}

	/**
     * 후처리 로직
     * 
     * @param dpReqDto
     * @return
     */
	public void endApprovalline(DpReqDto dpReqDto, String category){
			
			try {
				
				DpReqDto reqDetail = selectDetail(dpReqDto.getReqId());
				JSONObject reqContent = new JSONObject(reqDetail.getReqContent());

				//후처리 분기
				switch(category){
					//재식별/비식별
					case DP_REQ.RE_IDENTIFY :
					case DP_REQ.DE_IDENTIFY :
						rangerService.updateIdentPolicyAsync(reqDetail);
						break;
					//태블로
					case DP_REQ.TABLEAU :
						String authNm = reqContent.getString("auth");
						String projectId = reqContent.optString("projectId");
						JSONArray authorityArray = reqContent.getJSONArray("authority");
						for (int i = 0; i < authorityArray.length(); i++) {
							String userId = authorityArray.getJSONObject(i).getString("userId");
							tableauService.createAuthForTableauUser(userId, authNm, projectId, dpReqDto);
						}
						break;
					//KPI Index 신청
					case "ka" :
						kpiIndexReqService.createKpiIndexAfterApproval(dpReqDto);
						break;
					case DP_REQ.SCHEMA_AUTH :
						rangerService.updateSchemaPolicyAsync(reqDetail);
						break;
				}


			} catch(RuntimeException e) {
				log.error("DpReqService endApprovalline RuntimeException:::: {}",e.getMessage());
				dpReqDto.setStatus("ad");
				updateMst(dpReqDto);
			} catch(Exception e) {
				log.error("DpReqService endApprovalline Exception:::: {}",e.getMessage());
				dpReqDto.setStatus("ad");
				updateMst(dpReqDto);
			} finally {
				//처리완료 시
				if(DP_REQ_STATUS.DONE.equals(dpReqDto.getStatus())) {
					String alrmType = "";
					
					switch(category) {
						case DP_REQ.TABLEAU :
							alrmType = ALRM.REQ_TABLEAU_AUTH_DONE;
							break;
						case "ka" :
							// KPI Index 신청 완료 알림 (필요시 추가)
							break;
						default:
							break;
					}
					
					//처리완료 알림
					if(!StringUtils.isEmpty(alrmType)) {
						alrmService.sendDpAlarm(alrmType, dpReqDto.getReqId());
					}
				}
			}

	}

	/**
     * 권한신청 메인 개별 조회 only Id
     * 
     * @param dpReqDto
     * @return
     */
	public DpReqDto selectOneToOnlyId(DpReqDto dpReqDto) {
		return dpReqMapper.selectOneToOnlyId(dpReqDto);
	}

	/**
	 * 처리지연 상태인 목록 조회
	 *
	 * @param dpReqDto
	 * @return
	 */
	public List<DpReqDto> selectAdStatusList(DpReqDto dpReqDto) {
		return dpReqMapper.selectAdStatusList(dpReqDto);
	}

	/**
	 * 권한신청 수정 분기
	 *
	 * @param dpReqDto
	 * @return
	 */
	public int dpReqRetryAuthorize(DpReqDto dpReqDto){
		String category = dpReqDto.getCategory();
		dpReqDto.setAppr("c");
		dpReqDto.setStatus("ai");

		//내부DB 1차 update (상태 : 처리지연 -> 처리 중)
		int result = updateMst(dpReqDto);

		//후처리
		endApprovalline(dpReqDto,category);

		return result;
	}
	
	/**
	 * 태블로 추가권한 가져오기
	 * 
	 * */
	public Map<String,Object> getVerticalProjectList() throws Exception {
    	return tableauService.getVerticalProjectList();
    }
	
	/**
     * 테이블 prefix/suffix 코드 정보 조회
     * @return
     */    
    @Transactional
    public List<TblFixDto> getTblFixList() {
    	return dpReqMapper.getTblFixList();
	}
    
    /**
     * 표준화검토 컬럼 정보 조회
     * 
     * @param dpReqDto
     * @return
     */
	public List<DpReqDto> getDpColInfoList(ColInfoDto colInfoDto) {
		return dpReqMapper.getDpColInfoList(colInfoDto);
	}

	/**
	 * 시스템 AD 계정 유효성 검사 (계정이 LDAP에 존재하고, Zepplin-hidden 권한을 가져야 함)
	 * @param accountList
	 * @return 존재 여부
	 */
	public Map<String, Boolean> isValidCheckAccount(List<String> accountList){
		// 결과를 저장할 Map 생성
		Map<String, Boolean> result = new HashMap<>();
		for (String account : accountList) {
			boolean isValid = ldapService.isUserInGroup(account, "zeppelin-hidden");
			result.put(account, isValid);
		}
		return result;
	}

	/**
	 * 표준화검토 - 미등록 용어|단어 데이터 조회
	 * @param reqId
	 * @return
	 */
	public List<DpReqDto> getUnRegListById(String reqId) {
		return dpReqMapper.getUnRegListById(reqId);
	}

    /**
     * 권한에 대한 사용자 목록 조회
     * @param authIdsList
     * @return
     */
    public List<UserModel> getUserLIstByAuth(List<String> authIdsList) {
        return dpReqMapper.getUserLIstByAuth(authIdsList);
    }

    /**
     * 표준화검토 현황 - 적용
     * @param dpReqDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateApply(DpReqDto dpReqDto) {
        String reviewReqId = dpReqDto.getReqId();                           //표준화검토 ID
        JSONArray applyList = new JSONArray(dpReqDto.getApplyList());       //적용 list

	    dpReqMapper.lockReqDtlRow(reviewReqId);                             //표준화검토 상세건 Lock

	    String reqNo = "";      //신청번호
	    String termReqId = "";  //미등록 용어 ID
	    String wordReqId = "";  //미등록 단어 ID
        DpReqDto refIdInfo = dpReqMapper.selectReviewRefId(dpReqDto);       //용|단어 refID 조회
        if(refIdInfo != null) {
	        reqNo = StringUtils.safeToString(refIdInfo.getReqNo());
			termReqId = StringUtils.safeToString(refIdInfo.getReviewTermReqId());
	        wordReqId = StringUtils.safeToString(refIdInfo.getReviewWordReqId());

	        if(!"".equals(termReqId)) dpReqMapper.lockReqDtlRow(termReqId); //미등록 용어 상세건 Lock
	        if(!"".equals(wordReqId)) dpReqMapper.lockReqDtlRow(wordReqId); //미등록 단어 상세건 Lock
        }

        // 신청 DTO
        DpReqDto unregistWordDto = new DpReqDto();
        DpReqDto unregistTermDto = new DpReqDto();

        // 다건 등록 multi 객체
        JSONObject wordObj = new JSONObject();
        JSONObject termObj = new JSONObject();

        // 미등록 리스트
        JSONArray wordList = new JSONArray();
        JSONArray termList = new JSONArray();

        String termLogNmsByTerm = "";   //미등록 용어 신청 용어s
	    String newUnTermReqId = "";     //new 미등록 용어 신청 ID

	    String termLogNmsByWord = "";   //미등록 단어 신청 용어s

	    Integer termAddCnt = 0;         //추가 용어 수
		Integer wordAddCnt = 0;         //추가 단어 수
	    String termAddReqCmtByC = "";   //미등록 용어 - 추가 신청 사유
	    String termAddReqCmtByD = "";   //미등록 용어 - 삭제 신청 사유
		String wordAddReqCmtByC = "";   //미등록 단어 - 추가 신청 사유
	    String wordAddReqCmtByD = "";   //미등록 단어 - 삭제 신청 사유

        for(int i = 0, len = applyList.length(); i < len; i++) {
            JSONObject applyInfo = applyList.getJSONObject(i);
            String type = applyInfo.optString("type");              //대상 type('C': 등록 | 'D': 삭제 | 'P': 검증패스)
            String stdType = applyInfo.optString("stdType");        //용어|단어 type('w': 단어 | 't' : 용어)
            String objId = applyInfo.optString("objId");            //objId
            String logNm = applyInfo.optString("logNm");            //한글명
            String termLogNm = applyInfo.optString("termLogNm");    //용어명(단어 활용예시 | 검증패스)

            // 미등록 용어
	        if("t".equals(stdType)) {
		        // 신청건 존재 X
				if("".equals(termReqId)) {
			        // 등록
			        if("C".equals(type)) {
				        appendUnTermList(termList, applyInfo);
				        termLogNmsByTerm += termLogNm + ", ";
			        }
		        }
		        else {
			        // 등록 | 삭제
					if("C".equals(type) || "D".equals(type)) {
				        DpReqDto termDtoParams = new DpReqDto();
				        termDtoParams.setReqId(termReqId);                  //신청ID
				        termDtoParams.setCategory(DP_REQ.STD_TERM);         //category
				        termDtoParams.setStatus(DP_REQ_STATUS.TEMP_SAVE);   //status
				        termDtoParams.setLogNm(logNm);                      //한글명
				        termDtoParams.setObjId(objId);                      //objId
						termDtoParams.setType(type);                        //type

				        dpReqMapper.updateUnRegMerge(termDtoParams);

						termAddCnt++;
						if("C".equals(type)) {
							termAddReqCmtByC += logNm + ", ";
						}
						else {
							termAddReqCmtByD += logNm + ", ";
						}
			        }
		        }
	        }

			// 미등록 단어
	        if("w".equals(stdType)) {
		        // 신청건 존재 X
				if("".equals(wordReqId)) {
			        // 등록
			        if("C".equals(type)) {
				        appendUnWordList(wordList, applyInfo);
				        termLogNmsByWord += termLogNm + ", ";
			        }
		        }
		        else {
			        // 등록 | 삭제
					if("C".equals(type) || "D".equals(type)) {
				        DpReqDto wordDtoParams = new DpReqDto();
				        wordDtoParams.setReqId(wordReqId);                  //신청ID
				        wordDtoParams.setCategory(DP_REQ.STD_WORD);         //category
				        wordDtoParams.setStatus(DP_REQ_STATUS.TEMP_SAVE);   //status
				        wordDtoParams.setLogNm(logNm);                      //한글명
				        wordDtoParams.setUsageCase(termLogNm);              //활용예시
				        wordDtoParams.setObjId(objId);                      //objId
						wordDtoParams.setType(type);                        //type

				        dpReqMapper.updateUnRegMerge(wordDtoParams);

						wordAddCnt++;
						if("C".equals(type)) {
							wordAddReqCmtByC += logNm + ", ";
						}
						else {
							wordAddReqCmtByD += logNm + ", ";
						}
			        }
		        }
	        }

			//검증패스
	        if("P".equals(type)) {
				DpReqDto passDtoParams = new DpReqDto();

				passDtoParams.setReqId(reviewReqId);        //신청ID
		        passDtoParams.setObjId(objId);              //objId
				passDtoParams.setLogNm(termLogNm);          //변경 컬럼 한글명
		        dpReqMapper.updateReviewPass(passDtoParams);
	        }

	        //검증패스 취소
	        if("PC".equals(type)) {
		        DpReqDto passCancelDtoParams = new DpReqDto();

		        passCancelDtoParams.setReqId(reviewReqId);        //신청ID
		        passCancelDtoParams.setObjId(objId);              //objId
		        dpReqMapper.updateReviewPassCancel(passCancelDtoParams);
	        }
        }

		/* 신청 old 업데이트(상태, 신청사유) */
		//미등록 용어
	    if(termAddCnt > 0) {
			if(!"".equals(termAddReqCmtByC)) {
				try { termAddReqCmtByC = termAddReqCmtByC.substring(0, termAddReqCmtByC.length() -2) + " 추가"; }
				catch(StringIndexOutOfBoundsException e) {}
			}
			if(!"".equals(termAddReqCmtByD)) {
				try { termAddReqCmtByD = termAddReqCmtByD.substring(0, termAddReqCmtByD.length() -2) + " 삭제"; }
				catch(StringIndexOutOfBoundsException e) {}
			}

			//신청사유
			String fullReqCmt = "";
			fullReqCmt += !"".equals(termAddReqCmtByC) ? "<p>" + termAddReqCmtByC + "</p>" : "";
			fullReqCmt += !"".equals(termAddReqCmtByD) ? "<p>" + termAddReqCmtByD + "</p>" : "";

			String fullProcCmt = "";
			fullProcCmt += !"".equals(termAddReqCmtByC) ? termAddReqCmtByC
							+ (!"".equals(termAddReqCmtByD) ? " | " : "") : "";
			fullProcCmt += !"".equals(termAddReqCmtByD) ? termAddReqCmtByD : "";

			DpReqDto termDtoParams = new DpReqDto();
	        termDtoParams.setReqId(termReqId);                      //신청ID
	        termDtoParams.setStatus(DP_REQ_STATUS.REQ_MODIFY);      //status(수정필요)
		    termDtoParams.setReqCmt(fullReqCmt);                    //신청사유
		    termDtoParams.setProcCmt(fullProcCmt);                  //관리자 메시지(수정사유)
		    termDtoParams.setModiId(SessionScopeUtil.getUserId());  //수정ID

		    dpReqMapper.updateMstByReview(termDtoParams);
	    }

		//미등록 단어
	    if(wordAddCnt > 0) {
			if(!"".equals(wordAddReqCmtByC)) {
				try { wordAddReqCmtByC = wordAddReqCmtByC.substring(0, wordAddReqCmtByC.length() -2) + " 추가"; }
				catch(StringIndexOutOfBoundsException e) {}
			}
			if(!"".equals(wordAddReqCmtByD)) {
				try { wordAddReqCmtByD = wordAddReqCmtByD.substring(0, wordAddReqCmtByD.length() -2) + " 삭제"; }
				catch(StringIndexOutOfBoundsException e) {}
			}

			String fullReqCmt = "";
			fullReqCmt += !"".equals(wordAddReqCmtByC) ? "<p>" + wordAddReqCmtByC + "</p>" : "";
			fullReqCmt += !"".equals(wordAddReqCmtByD) ? "<p>" + wordAddReqCmtByD + "</p>" : "";

			String fullProcCmt = "";
			fullProcCmt += !"".equals(wordAddReqCmtByC) ? wordAddReqCmtByC
							+ (!"".equals(wordAddReqCmtByD) ? " | " : "") : "";
			fullProcCmt += !"".equals(wordAddReqCmtByD) ? wordAddReqCmtByD : "";

			DpReqDto wordDtoParams = new DpReqDto();
	        wordDtoParams.setReqId(wordReqId);                      //신청ID
	        wordDtoParams.setStatus(DP_REQ_STATUS.REQ_MODIFY);      //status(수정필요)
		    wordDtoParams.setReqCmt(fullReqCmt);                    //신청사유
		    wordDtoParams.setProcCmt(fullProcCmt);                  //관리자 메시지(수정사유)
		    wordDtoParams.setModiId(SessionScopeUtil.getUserId());  //수정ID

		    dpReqMapper.updateMstByReview(wordDtoParams);
	    }

		/* 신청 new */
		//미등록 용어
		if("".equals(termReqId) && termList.length() > 0) {
			try { termLogNmsByTerm = termLogNmsByTerm.substring(0, termLogNmsByTerm.length() -2); }
			catch(StringIndexOutOfBoundsException e) {}

			unregistTermDto.setCategory(DP_REQ.STD_TERM);							            //신청분류 : 't'(용어신청)
			unregistTermDto.setType("2");											            //신청타입 : '2'(다건)
			unregistTermDto.setStatus(DP_REQ_STATUS.TEMP_SAVE);						            //상태 : 't'(임시저장)
			//unregistTermDto.setReqCmt(termLogNmsByTerm + " 표준화검토 신청 중 미등록 용어신청");	//신청내용
			unregistTermDto.setReqCmt("표준화검토 신청번호 : " + reqNo + "로 인한 신청");	        //신청내용
			unregistTermDto.setRgstId(SessionScopeUtil.getUserId());				            //신청자
			unregistTermDto.setRefId(reviewReqId);									            //종속 신청 건 : 표준화검토 신청ID
			unregistTermDto.setReviewRefId(reviewReqId);							            //종속 표준화검토 신청 건

			termObj.put("multi", termList);
			unregistTermDto.setReqContent(termObj.toString());

			newUnTermReqId = insertMst(unregistTermDto);
		}

	    //미등록 단어
	    if("".equals(wordReqId) && wordList.length() > 0) {
		    try { termLogNmsByWord = termLogNmsByWord.substring(0, termLogNmsByWord.length() -2); }
		    catch(StringIndexOutOfBoundsException e) {}

		    unregistWordDto.setCategory(DP_REQ.STD_WORD);						                //신청분류 : 'w'(단어신청)
		    unregistWordDto.setType("2");										                //신청타입 : '2'(다건)
		    unregistWordDto.setStatus(DP_REQ_STATUS.TEMP_SAVE);					                //상태 : 't'(임시저장)
		    //unregistWordDto.setReqCmt(termLogNmsByWord + " 표준화검토 신청 중 미등록 단어신청");	//신청내용
		    unregistWordDto.setReqCmt("표준화검토 신청번호 : " + reqNo + "로 인한 신청");	        //신청내용
		    unregistWordDto.setRgstId(SessionScopeUtil.getUserId());			                //신청자
		    unregistWordDto.setRefId(newUnTermReqId);		                                    //종속 신청 건 : 용어신청ID
		    unregistWordDto.setReviewRefId(reviewReqId);						                //종속 표준화검토 신청 건 : 표준화검토 신청ID

		    wordObj.put("multi", wordList);
		    unregistWordDto.setReqContent(wordObj.toString());

		    insertMst(unregistWordDto);
	    }
    }

	/**
	 * 미등록용어 리스트 생성
	 * @param termList
	 * @param term
	 */
    private void appendUnTermList(JSONArray termList, JSONObject term) {
	    if (term != null) {
		    String objId = term.optString("objId");     //objId
		    String logNm = term.optString("logNm");     //한글명

		    JSONObject tempObj = new JSONObject();

		    tempObj.put("category", DP_REQ.STD_TERM);
		    tempObj.put("status", DP_REQ_STATUS.TEMP_SAVE);
		    tempObj.put("termLogNm", logNm);
		    tempObj.put("objId", objId);

		    termList.put(tempObj);
	    }
    }

	/**
	 * 미등록단어 리스트 생성
	 * @param wordList
	 * @param word
	 */
	private void appendUnWordList(JSONArray wordList, JSONObject word) {
		if(word != null) {
			String objId = word.optString("objId");             //objId
			String logNm = word.optString("logNm");             //한글명
			String termLogNm = word.optString("termLogNm");     //용어명(단어 활용예시)

			JSONObject wordObj = new JSONObject();

			wordObj.put("category", DP_REQ.STD_WORD);           //신청분류 : 'w'(단어신청)
			wordObj.put("status", 	DP_REQ_STATUS.TEMP_SAVE);   //상태 : 't'(임시저장)
			wordObj.put("wordLogNm", logNm);                    //단어 한글명
			wordObj.put("usageCase", termLogNm);                //활용 예시
			wordObj.put("objId", objId);

			wordList.put(wordObj);
		}
	}

	/**
	 * 표준화검토 - 스키마 리스트 조회
	 * @return
	 */
	public List<GlueDbModel> getSchemaList() {
		return dpReqMapper.getSchemaList();
	}

	/**
	 * 스키마 생성 신청 - 스키마명 중복 체크
	 * @param schemaNm
	 * @return
	 */
	public Integer getSchemaNmDuplChk(String schemaNm) {
		return (commonUtil.isLocal() || commonUtil.isDev())
				? dpReqMapper.getSchemaNmDuplChkTest(schemaNm)
				: dpReqMapper.getSchemaNmDuplChk(schemaNm);
	}
}

