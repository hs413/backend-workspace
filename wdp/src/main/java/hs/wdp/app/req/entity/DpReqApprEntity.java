package hs.wdp.app.req.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_req_appr", schema = "dataportal")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DpReqApprEntity {
    @Id
    private String apprId;

    private String reqId;

    private int step;
    private String apprIdNm;
    private String apprDt;
    private String appr;

}
