package hs.wdp.app.gd.meta.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper=false)
public class DhMetaSchemaModel {
    /* 프로젝트 ID(이름) */
    private String projectId;

    /* 스키마 ID(이름) */
    private String schemaId;

    /* 스키마 설명(DH) */
    private String dhDscr;

    /* 스키마 설명(우형) */
    private String wwDscr;

    /* 데이터허브 레이어 */
    private String datahubLayer;

    private String dhEnv;

    /* 타임존 */
    private String timezone;

    /* 사용여부 */
    private String useYn;

    /* 삭제여부 */
    private String delYn;

    /* 생성일시(DH) */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+0")
    private LocalDateTime dhRgstDt;

    /* 최종 수정일시(DH) */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+0")
    private LocalDateTime dhModiDt;

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
