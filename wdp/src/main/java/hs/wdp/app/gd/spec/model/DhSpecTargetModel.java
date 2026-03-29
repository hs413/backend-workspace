package hs.wdp.app.gd.spec.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import in.woowa.data.portal.app.core.gcp.spec.dto.DhSpecDto;
import java.time.LocalDateTime;
import java.util.List;
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
@EqualsAndHashCode(callSuper=false)
public class DhSpecTargetModel {
    private String targetId;
    private String targetNm;
    private String purpose;
    private List<DhSpecDto.RefInfo> refs;
    private String description;
    private String category;
    private String exam;
    private int ordSeq;
    private String status;

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

    private String delYn;
}
