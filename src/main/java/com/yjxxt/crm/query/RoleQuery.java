package com.yjxxt.crm.query;

import com.yjxxt.crm.base.BaseQuery;

public class RoleQuery extends BaseQuery {
    private String roleName;

    public RoleQuery() {
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "RoleQuery{" +
                "roleName='" + roleName + '\'' +
                '}';
    }
}
