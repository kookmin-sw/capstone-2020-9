// IConnectionService.aidl
package com.example.touchonscreen;

// Declare any non-default types here with import statements

interface IConnectionService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    boolean getStatus();
    void setSocket(String ip);
    void connect();
    void disconnect();
    void send(String smsg);
    String receive();
}
