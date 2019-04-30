#mvn deploy:deploy-file -DgroupId=com.iflytek -DartifactId=Msc -Dversion=1.0 -Dpackaging=jar -Dfile=Msc.jar -Durl=http://192.168.1.10:8081/repository/maven-releases/ -DrepositoryId=daoming-releases
mvn deploy:deploy-file -DgroupId=nl.captcha -DartifactId=simplecaptcha -Dversion=1.2.1 -Dpackaging=jar -Dfile=simplecaptcha-1.2.1.jar -Durl=https://repo.rdc.aliyun.com/repository/29962-release-ablYFt/ -DrepositoryId=rdc-releases

#mvn install:install-file -Dfile=Msc.jar -DgroupId=com.iflytek -DartifactId=Msc -Dversion=1.0 -Dpackaging=jar