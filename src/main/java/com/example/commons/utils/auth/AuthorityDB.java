package com.example.commons.utils.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zzyy.leaf.commons.BaseDB;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * 权限表
 *
 * @author hao.wang
 * @date Created in 2021/3/13
 */

@Getter
@Setter
public class AuthorityDB {

    @JsonIgnore
    private Set<String> path;
    /**
     * 运营后台选择操作默认值，Authorize注解any第二个参数
     */
    @ApiModelProperty("操作默认名称")
    private String defaultName;

    @ApiModelProperty("操作默认名称")
    private String customizeName;

    @ApiModelProperty("路由名称")
    private String name;

    @ApiModelProperty("路由地址")
    private String component;

    @ApiModelProperty("接口标识")
    private String flag;

    @ApiModelProperty("页面url")
    private String webPath;

    @ApiModelProperty("页面标题")
    private String title;

    @ApiModelProperty("面包屑")
    private Boolean breadcrumb;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("重定向地址")
    private String redirect;

    @ApiModelProperty("菜单key")
    private String permissionKey;

}
