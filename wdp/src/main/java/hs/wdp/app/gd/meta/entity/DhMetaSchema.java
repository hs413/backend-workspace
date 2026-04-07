package hs.wdp.app.gd.meta.entity;

import hs.wdp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JPA 용
 * */
@Entity
@Table(name = "dh_meta_schema", schema = "dataportal")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DhMetaSchema extends BaseEntity {
    @EmbeddedId
    private DhMetaSchemaId id;

    @Column(name = "dh_dscr")
    private String dhDscr;

    @Column(name = "ww_dscr")
    private String wwDscr;

    @Column(name = "datahub_layer")
    private String datahubLayer;

    @Column(name = "dh_env")
    private String dhEnv;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "dh_rgst_dt")
    private String dhRgstDt;

    @Column(name = "dh_modi_dt")
    private String dhModiDt;

    @Column(name = "use_yn")
    private String useYn;


    public void updateInfo(String useYn, String wwDscr, String modiId) {
        this.wwDscr = wwDscr;
        this.dhModiDt = modiId;
        this.useYn = useYn;
    }
}
