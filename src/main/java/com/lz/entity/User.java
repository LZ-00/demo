package com.lz.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author lz
 * @since 2020-12-09
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @ApiModel(value="User对象", description="")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty(value = "主键")
        @TableId(value = "id", type = IdType.AUTO)
      private Long id;

      @ApiModelProperty(value = "用户名")
      private String username;

      @ApiModelProperty(value = "密码")
      private String password;

      @ApiModelProperty(value = "是否锁定")
      private Integer lockState;

      @ApiModelProperty(value = "角色")
      private String role;

      @ApiModelProperty(value = "权限")
      private String perms;

      @ApiModelProperty(value = "乐观锁")
      @Version
    private Integer version;

      @ApiModelProperty(value = "逻辑删除")
      @TableLogic
    private Integer deleted;

      @ApiModelProperty(value = "创建时间")
      @TableField(fill = FieldFill.INSERT)
      private Date gmtCreate;

      @TableField(fill = FieldFill.INSERT_UPDATE)
      @ApiModelProperty(value = " 更新时间")
      private Date gmtModify;


}
