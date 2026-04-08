package hs.wdp.app.req.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dp_req", schema = "dataportal")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DpReqEntity {
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

}
