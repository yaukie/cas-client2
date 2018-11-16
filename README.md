# cas-client2
基于开源项目simple-sso的单点登录实例

# 部署
直接通过git命令下载该项目实例,并启动tomcat,执行 mvn build ,将打成的war包,放到tomcat 的webapp目录下,
在tomcat使热部署的前提下,tomcat将会自动重启

# 配置选项
通过配置 /resources/confifg.properties文件中的cas相关参数,来决定simple-sso是否启用单点,以及所需参数
cas sso config sso=true 
sso.app_url=http://localhost:8088/casClient2
sso.cas_url=http://192.168.1.105:8081/cas 
sso.filter_mapping=/*

# 访问页面
应用启动正确之后,直接访问 http://localhost:8088/casClient2 (注意:tomcat端口自己根据情况修改,项目名称亦如此),
页面将会跳转到cas的登录页 
完
