package hs.wdp.app.comment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqCommentModel {
    /* ID (UUID: idUtil.getSecureUUID()) */
    private String commentId;

    /* 상위ID (NULL: 댓글, NOT NULL: 답글) */
    private String upCommentId;

    /* 분류(req:신청서, tbl:테이블, col:컬럼, t:미등록용어, w:미등록단어) */
    private String type;

    /* 참조 신청ID */
    private String refId;

    /* 참조키(type key) */
    private String refKey;

    /* 내용 */
    private String cn;

    /* 관리자 댓글/답글 여부 (Y/N) */
    private String mngrYn;

    /* 사용여부 (Y/N) */
    private String useYn;

    /* 등록자 ID */
    private String rgstId;

    /* 등록일시 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+9")
    private LocalDateTime rgstDt;

    /* 수정자 ID */
    private String modiId;

    /* 수정일시 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+9")
    private LocalDateTime modiDt;
}
