package com.csp.app.entity;

import com.csp.app.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;


@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Admin extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    private Integer id;
    @Column
    private Integer adminId;
    @Column
    private String adminName;
    @Column
    private String password;
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public static final class Builder {
        private Admin admin;

        public Builder() {
            admin = new Admin();
        }

        public static Builder anAdmin() {
            return new Builder();
        }

        public Builder id(Integer id) {
            admin.setId(id);
            return this;
        }

        public Builder adminId(Integer adminId) {
            admin.setAdminId(adminId);
            return this;
        }

        public Builder adminName(String adminName) {
            admin.setAdminName(adminName);
            return this;
        }

        public Builder password(String password) {
            admin.setPassword(password);
            return this;
        }

        public Builder createTime(Date createTime) {
            admin.setCreateTime(createTime);
            return this;
        }

        public Admin build() {
            return admin;
        }
    }
}
