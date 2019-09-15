# MutiThreadDictionaryServer
Assignment 1 of COMP90015 Distributed Systems

## Introduction
In this project, a multi-threaded dictionary client-server system is designed and implemented using a client-server architecture. The server of the system allows concurrent clients to modify or query the remote dictionary by using thread-per-request architecture in the server. The system provides reliable communication between the server and clients by using TCP protocol. Also, JSON is used in the system for providing a message exchange protocol between the server and clients. As for failure handling, errors including I/O errors, Network Connection error, and parameters errors, are properly managed on both the server and the client side. Also, illegal requests (such as adding a word that is already in the dictionary, removing or querying a none-exit word and adding a word without meaning) from clients can be detected and handled by the server properly. Moreover, the server can handle concurrent requests at the same time and edit the data in a correct way.

## Usage
Run the server:
```
Java -jar DictionaryServer.jar <port> <dictionary-file>
```

Run the client:
```
Java -jar DictionaryClient.jar <server-address> <server-port>
```

## More Detail
If you want to learn more detail of this simple application, please read the report.pdf.

## Warning
This repository is for reference only. Learn from it when you are concerned about the assignment. **Please do not copy all the code for submission.**
