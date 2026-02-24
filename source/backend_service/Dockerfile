FROM openjdk:11.0.14.1-jdk-buster

ARG USER_HOME_DIR="/root"
ARG SHA=f790857f3b1f90ae8d16281f902c689e4f136ebe584aba45e4b1fa66c80cba826d3e0e52fdd04ed44b4c66f6d3fe3584a057c26dfcac544a60b301e6d0f91c26

WORKDIR /app
COPY . /app

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \ 
  && echo "${SHA}  ./maven/apache-maven.tar.gz" | sha512sum -c - \
  && tar -xzf ./maven/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

RUN mkdir -p $MAVEN_CONFIG && \
    echo "<settings><mirrors><mirror><id>aliyun-central</id><url>https://maven.aliyun.com/repository/public</url><mirrorOf>central,!thirdparty-sync</mirrorOf></mirror></mirrors></settings>" > $MAVEN_CONFIG/settings.xml

RUN mvn clean package -Dmaven.test.skip=true


ENTRYPOINT ["sh", "-c", "java -jar /app/sample/target/sample-1.10.0.jar"]

