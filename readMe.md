# ���

**ע�⣺**
����extend�����������ڶ࣬ͨ��mavenʹ��ʱ������Ҫ�ų�������������ʹ����Ŀ�����������*��ʾ�ų�������������
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

# ���ṹ�͹���˵��
������com.tingfeng.util.java.extend

## common ͨ�ù���

### utils

- QRCodeUtils ��ά�빤��
- RSAUtils rsa����
- ServletUtils servlet����
- XMLUtils xml����
- ZipUtils zip����

#### execl

- ExcelWriterHelperBySXSSF execlд����


### helper

- ZipHelper ziphelper

## hadoop hadoop���߰�

## mvc spring mvc ��ع��߰�
- SpringContextHolder spring mvc�������Ĺ���

## orm ormӳ����ع��߰�

### hibernate

#### baseDao

- BaseDaoImpl �� BaseDaoImpl  ����basedaoʹ��

#### helper

- TreeHelper crud tree�ṹ����

#### utils

- HibernateUtils crud��ɾ�Ĳ鹤��

## Serialization ���л���ع��߰�

### json

- FastJsonUtils fastJson����



## web web��ع��߰�

### bean 
- MyJson ͨ�÷���json��ʽ����
- MyPage ����ǰ�˷��صķ�ҳ����
- MyPager ��˷���ǰ�˵ķ�ҳ����

### http
- HttpUtils http����
- BaseHttpClient ����http����֧����
- HttpServletRequestUtils request����
- HttpServletResponseUtils response����

### utils
- WebServiceUtils WebService����
