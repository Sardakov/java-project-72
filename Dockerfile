FROM gradle:8.8-jdk21

WORKDIR /app

COPY /app .

RUN gradle clean installDist

ENV TEST_ENV=TEST

CMD ./build/install/app/bin/app