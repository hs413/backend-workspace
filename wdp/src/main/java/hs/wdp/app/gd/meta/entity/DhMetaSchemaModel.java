package hs.wdp.app.gd.meta.entity;

import lombok.Builder;

/**
 * MyBatis 용
 * */
@Builder
public class DhMetaSchemaModel {
    private String projectId;
    private String schemaId;
    private String dhDscr;
    private String wwDscr;
    private String dhLayer;
    private String dhEnv;
    private String timezone;
    private String useYn;
    private String delYn;
    private String dhRgstDt;
    private String dhModiDt;
    private String rgstId;
    private String rgstDt;
    private String modiId;
    private String modiDt;
}
