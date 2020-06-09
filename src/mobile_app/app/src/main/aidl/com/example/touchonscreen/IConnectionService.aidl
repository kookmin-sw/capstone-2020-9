// IConnectionService.aidl
package com.example.touchonscreen;

// Declare any non-default types here with import statements

interface IConnectionService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int getStatus();
    void setSocket(String ip);
    void connect();
    void disconnect();
    void send(String smsg);
    String recvMsg();
    void con_send(String host, String ss);
    void con_send_signup(String host, String ss);
    void receive();
    void send_recv(String msg);
}
