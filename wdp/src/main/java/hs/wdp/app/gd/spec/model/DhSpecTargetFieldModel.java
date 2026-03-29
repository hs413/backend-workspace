package hs.wdp.app.gd.spec.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@EqualsAndHashCode(callSuper=false)
public class DhSpecTargetFieldModel {

    private String targetId;
    private String targetFieldId;
    private String targetFieldNm;
    private String dataType;
    private String description;
    private String requiredYn;
    private String useYn;
    private String delYn;
    private int ordSeq;
    private int dhOid;

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
