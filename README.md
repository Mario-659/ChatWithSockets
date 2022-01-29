# Console Chat Application

Simple client-server chat app implemented with sockets

## Technologies

* Lombok
* Log4j2
* JUnit 5
* Mockito

## Setup

Running server with gradle:

    C:\Users\User\ChatWithSockets> gradle server::run

Running client:

    C:\Users\User\ChatWithSockets> gradle --console plain client:run

Building jar with gradle:

    C:\Users\User\ChatWithSockets> gradle server::shadowJar

By default, server and client run on localhost, port 55. To run on given host and port:
    
    C:\Users\User\ChatWithSockets> gradle server:run --args="<host> <port>"

## Functionalities

* Create chat rooms
* Chat with multiple clients