package hs.wdp.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(name = "use_yn", length = 1)
    private String useYn = "Y";

    @Column(name = "del_yn", length = 1)
    private String delYn = "N";

    @CreatedBy
    @Column(name = "rgst_id", updatable = false)
    private String rgstId;

    @CreatedDate
    @Column(name = "rgst_dt", updatable = false)
    private LocalDateTime rgstDt;

    @LastModifiedBy
    @Column(name = "modi_id")
    private String modiId;

    @LastModifiedDate
    @Column(name = "modi_dt")
    private LocalDateTime modiDt;
}