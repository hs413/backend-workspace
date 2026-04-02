package hs.wdp.app.comment.service;

import hs.wdp.app.comment.dto.ReqCommentDto;
import hs.wdp.app.comment.mapper.ReqCommentMapper;
import in.woowa.data.portal.app.core.reqcomment.dto.ReqCommentDto;
import in.woowa.data.portal.app.core.reqcomment.mapper.ReqCommentMapper;
import in.woowa.data.portal.app.core.reqcomment.model.ReqCommentModel;
import in.woowa.data.portal.common.dto.DpReqDto;
import in.woowa.data.portal.common.service.DpReqService;
import in.woowa.data.portal.common.util.IdUtil;
import in.woowa.data.portal.common.util.SessionScopeUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReqCommentService {
    private final ReqCommentMapper reqCommentMapper;

    public ReqCommentDto.Response getComments(ReqCommentDto.SearchParam params) {
        List<ReqCommentDto> list = reqCommentMapper.selectReqComments(params);
        DpReqDto dto = dpReqService.selectDetail(params.getRefId());

        return ReqCommentDto.Response.builder()
                .comments(list)
                .reqDetail(ReqCommentDto.ReqInfo.from(dto))
                .build();
    }

    @Transactional
    public String createReqComment(ReqCommentDto.Create dto) {
        String userId = SessionScopeUtil.getUserId();

//        dto.setMngrYn(resolveMngrYn(dto.getMngrYn(), dto.getUpCommentId()));

        ReqCommentModel reqCommentModel = ReqCommentModel.builder()
                .commentId(idUtil.getSecureUUID())
                .upCommentId(dto.getUpCommentId())
                .type(dto.getType().getCode())
                .cn(dto.getCn())
                .mngrYn(dto.getMngrYn())
                .refKey(dto.getRefKey())
                .refId(dto.getRefId())
                .rgstId(userId)
                .modiId(userId)
                .build();

        reqCommentMapper.insertReqComment(reqCommentModel);
        reqCommentAlrmService.sendCreateCommentAlrm(reqCommentModel);
        return reqCommentModel.getCommentId();
    }

    @Transactional
    public String updateComment(String commentId, ReqCommentDto.Update dto) {
        ReqCommentModel existing = reqCommentMapper.selectReqCommentById(commentId);
        if (existing == null) {
            throw new RuntimeException("comment not found");
        }

//        dto.setMngrYn(resolveMngrYn(dto.getMngrYn(), dto.getUpCommentId()));

        ReqCommentModel updated = existing.toBuilder()
                .cn(dto.getCn())
                .mngrYn(dto.getMngrYn())
                .modiId(SessionScopeUtil.getUserId())
                .modiDt(LocalDateTime.now())
                .build();

        reqCommentMapper.updateReqComment(updated);
        return updated.getCommentId();
    }

    /**
     * 댓글 작성 시 관리자 여부(mngrYn)를 결정하기 위한 규칙 처리 메서드.
     *
     * 규칙:
     *  - 현재 댓글이 일반(N)이고 상위 댓글이 존재하는 경우,
     *    상위 댓글이 관리자(Y)라면 해당 댓글도 관리자(Y)로 승계한다.
     *  - 그 외 상황은 입력값(current)을 그대로 반환한다.
     *
     * @param current      현재 댓글의 관리자 여부(Y/N)
     * @param upCommentId  상위 댓글 ID (null이면 상위 댓글 없음)
     * @return 적용 결과의 mngrYn (Y 또는 N)
     */
    private String resolveMngrYn(String current, String upCommentId) {
        if ("Y".equals(current) || upCommentId == null) return current;

        ReqCommentModel parent = reqCommentMapper.selectReqCommentById(upCommentId);
        return (parent != null && "Y".equals(parent.getMngrYn())) ? "Y" : current;
    }

    @Transactional
    public void deleteComment(String commentId) {
        ReqCommentModel existing = reqCommentMapper.selectReqCommentById(commentId);
        if (existing == null) {
            throw new RuntimeException("comment not found");
        }

        reqCommentMapper.deleteReqComment(existing);
    }

    public List<ReqCommentDto.CountResponse> getCountAll(ReqCommentDto.SearchParam params) {
        return reqCommentMapper.selectCountAll(params);
    }

    public int getCount(ReqCommentDto.SearchParam params) {
        return reqCommentMapper.selectCount(params);
    }
}
