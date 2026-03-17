package hs.wdp.gd.meta.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

/**
 * JPA 용
 * */
@Entity
public class DhMetaSchema {
    @EmbeddedId
    private DhMetaSchemaId id;

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
