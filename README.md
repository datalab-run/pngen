PNGEN
===
各种生成器

下载pngen.jar到本地，无需解压。

在Intellij里选择Settings=>Plugins=>Install Plugin From Disk即可。

## 1. 根据Entity生成create table

![http://o9sdkg4ln.bkt.clouddn.com/pngen.gif](http://o9sdkg4ln.bkt.clouddn.com/pngen.gif)

- 基于票牛的插件修改
- 支持JPA注解
- 支持继承

```
public class BaseEntity {
    @Id
    private  Long id;

    @Column(name = "update_ts")
    private Date updateTs;
}

@Table(name = "tb_user")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    @Column(name = "namename", nullable = false, unique = true)
    private String namename;

    /**
     * 密码
     */
    @Column(name = "password", columnDefinition = "VARCHAR(32)")
    private String password;

    @Column(name = "age")
    private Integer age;

    @Column(name = "is_male", nullable = false)
    private Boolean isMale;
}

    CREATE TABLE `tb_user` (
        `namename` VARCHAR(255) NOT NULL UNIQUE COMMENT '用户名',
        `password` VARCHAR(32) COMMENT '密码',
        `age` INT,
        `is_male` TINYINT NOT NULL,
        `id` BIGINT,
        `update_ts` DATETIME,
        PRIMARY KEY (`id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
```