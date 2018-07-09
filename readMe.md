# 简介

**注意：**
由于extend包可能依赖众多，通过maven使用时可能需要排除其自身依赖而使用项目本身的依赖（*表示排除所有依赖）：
```
<dependency>
  <groupId>com.tingfeng</groupId>
  <artifactId>>tingfengutil-java-extend</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <exclusions>
    <exclusion>
      <groupId>*</groupId>
      <artifactId>*</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```

# 包结构和功能说明
主包：com.tingfeng.util.java.extend

## common 通用工具

### utils

- QRCodeUtils 二维码工具
- RSAUtils rsa工具
- ServletUtils servlet工具
- XMLUtils xml工具
- ZipUtils zip工具

#### execl

- ExcelWriterHelperBySXSSF execl写工具


### helper

- ZipHelper ziphelper

## hadoop hadoop工具包

## mvc spring mvc 相关工具包
- SpringContextHolder spring mvc的上下文工具

## orm orm映射相关工具包

### hibernate

#### baseDao

- BaseDaoImpl 和 BaseDaoImpl  构建basedao使用

#### helper

- TreeHelper crud tree结构数据

#### utils

- HibernateUtils crud增删改查工具

## Serialization 序列化相关工具包

### json

- FastJsonUtils fastJson工具



## web web相关工具包

### bean 
- MyJson 通用返回json格式数据
- MyPage 接收前端返回的分页数据
- MyPager 后端返回前端的分页数据

### http
- HttpUtils http工具
- BaseHttpClient 基础http工具支持类
- HttpServletRequestUtils request工具
- HttpServletResponseUtils response工具

### utils
- WebServiceUtils WebService工具
