./gradlew build -x test

docker build --build-arg JAR_FILE=build/libs/\*.jar -t nono_server .

docker push jangtaehwan/nono_server:latest
