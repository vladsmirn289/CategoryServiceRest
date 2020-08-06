FROM alpine/git as clone
WORKDIR /home/categoryService
RUN git clone https://github.com/vladsmirn289/CategoryServiceRest.git

FROM maven:3.5-jdk-8-alpine as build
WORKDIR /home/categoryService
COPY --from=clone /home/categoryService/CategoryServiceRest .
RUN mvn -DskipTests=true package

FROM openjdk:8-jre-alpine
WORKDIR /home/categoryService
COPY --from=build /home/categoryService/target/*.jar .
ENV db_host db
CMD java -jar *.jar --db_url=jdbc:postgresql://${db_host}:5432/shop_db