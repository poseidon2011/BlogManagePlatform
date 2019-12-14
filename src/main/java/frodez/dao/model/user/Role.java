package frodez.dao.model.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

/**
 * @description 用户角色表
 * @table tb_role
 * @date 2019-12-09
 */
@Data
@Entity
@Table(name = "tb_role")
@ApiModel(description = "用户角色返回数据")
public class Role implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * ID(不能为空)
	 */
	@Id
	@NotNull
	@Column(name = "id")
	@ApiModelProperty("ID")
	private Long id;

	/**
	 * 创建时间(不能为空)
	 */
	@NotNull
	@Column(name = "create_time")
	@ApiModelProperty("创建时间")
	private Date createTime;

	/**
	 * 角色名称(不能为空)
	 */
	@NotBlank
	@Length(max = 255)
	@Column(name = "name", length = 255)
	@ApiModelProperty("角色名称")
	private String name;

	/**
	 * 角色等级 0-9 0最高,9最低(不能为空,默认值:0)
	 */
	@NotNull
	@Column(name = "level")
	@ApiModelProperty(value = "角色等级  0-9  0最高,9最低")
	private Byte level = 0;

	/**
	 * 描述
	 */
	@Nullable
	@Length(max = 1000)
	@Column(name = "description", length = 1000)
	@ApiModelProperty("描述")
	private String description;
}
