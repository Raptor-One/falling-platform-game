package com.rptr1.fpg.server.main;

import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class Main
{

    public static void main( String[] args )
    {
        String host = "192.168.1.21";
        int port = 8887;

        WebSocketServer server = new GameServerWebSocket( new InetSocketAddress( port ) );

        server.run();
    }
}
