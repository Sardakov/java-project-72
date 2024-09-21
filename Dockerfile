FROM gradle:8.8-jdk21

WORKDIR /app

COPY /app .

RUN gradle clean install

CMD ./build/install/app/bin/app