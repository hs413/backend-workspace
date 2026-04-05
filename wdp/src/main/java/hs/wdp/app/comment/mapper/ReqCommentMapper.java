package hs.wdp.app.comment.mapper;

import hs.wdp.app.comment.dto.ReqCommentDto;
import hs.wdp.app.comment.model.ReqCommentModel;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ReqCommentMapper {

	/**
	 * 댓글 삽입
	 *
	 * @param reqComment 댓글 정보
	 */
	void insertReqComment(ReqCommentModel reqComment);

    /**
	 * 댓글 리스트 조회
	 *
	 * @return 댓글 리스트
	 */
	List<ReqCommentDto> selectReqComments(ReqCommentDto.SearchParam params);

    /**
     * 댓글 조회
     *
     * @return
     * */
    ReqCommentModel selectReqCommentById(String id);


    /**
     * 댓글 수정
     *
     * @param reqComment 수정할 댓글 정보
     */
    void updateReqComment(ReqCommentModel reqComment);

    /**
     * 댓글 삭제
     *
     * @param reqComment 삭제할 댓글 정보
     */
    void deleteReqComment(ReqCommentModel reqComment);



    /**
     * refId 기준 전체 댓글 카운트 (refKey, type gropyBy)
     * */
    List<ReqCommentDto.CountResponse> selectCountAll(ReqCommentDto.SearchParam params);


    /**
     * refId, refKey, type 기준 댓글 카운트
     * */
    int selectCount(ReqCommentDto.SearchParam params);
}