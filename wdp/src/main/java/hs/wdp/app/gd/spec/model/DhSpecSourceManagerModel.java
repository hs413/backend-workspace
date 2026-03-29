package hs.wdp.app.gd.spec.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Builder
@EqualsAndHashCode(callSuper=false)
public class DhSpecSourceManagerModel {
    private String sourceId;
    private String userId;

    /* 등록자 ID */
    private String rgstId;

    /* 등록일시 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+9")
    private LocalDateTime rgstDt;


}
