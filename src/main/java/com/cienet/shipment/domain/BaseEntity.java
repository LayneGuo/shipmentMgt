package com.cienet.shipment.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Id;
import java.io.Serializable;
import java.time.Instant;

public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @Id
    @TableId(type= IdType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public BaseEntity setId(Long id) {
        this.id = id;
        return this;
    }

    @TableField("created_by")
    @JsonIgnore
    private String createdBy;

    @TableField("created_date")
    @JsonIgnore
    private Instant createdDate = Instant.now();

    @JsonIgnore
    @TableField("last_modified_by")
    private String lastModifiedBy;

    @JsonIgnore
    @TableField("last_modified_date")
    private Instant lastModifiedDate = Instant.now();

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
