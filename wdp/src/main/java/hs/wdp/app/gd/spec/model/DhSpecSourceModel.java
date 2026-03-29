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
public class DhSpecSourceModel {
    private String targetId;
    private String sourceId;
    private String sourceNm;
    private int ordSeq;
    private String coverage;
    private List<String> manageItems;
    private String delYn;

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

    /* 매니저 정보 전달용, 저장x */
    private List<DhSpecDto.ManagerInfo> managers;
}
