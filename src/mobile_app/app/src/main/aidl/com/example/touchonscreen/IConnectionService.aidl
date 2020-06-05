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
    void con_send(String ss);
    String receive();
}
