package zzuli.zw.utils;

import zzuli.zw.main.connection.FileDataServerThreadStart;
import zzuli.zw.main.connection.ServerThreadStart;
import zzuli.zw.main.connection.NioServerStart;
import java.io.IOException;

public class RequestStart {
    public static void run(Class<?> clazz) throws IOException {
        System.out.print("\n\n  _____                            _____                   _                 \n" +
                " / ____|                          |  __ \\                 (_)                \n" +
                "| (___   ___ _ ____   _____ _ __  | |__) |   _ _ __  _ __  _ _ __ ___   __ _ \n" +
                " \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__| |  _  / | | | '_ \\| '_ \\| | '_ ` _ \\ / _` |\n" +
                " ____) |  __/ |   \\ V /  __/ |    | | \\ \\ |_| | | | | | | | | | | | | | (_| |\n" +
                "|_____/ \\___|_|    \\_/ \\___|_|    |_|  \\_\\__,_|_| |_|_| |_|_|_| |_| |_|\\__, |\n" +
                "                                                                        __/ |\n" +
                "                                                                       |___/ \n"+
                "                        server version (2.0 - NIO)\n\n");
        
        // 使用NIO服务器
        NioServerStart nioServer = new NioServerStart(clazz);
        nioServer.start();
    }
    
    public static void runBIO(Class<?> clazz) throws IOException {
        System.out.print("\n\n  _____                            _____                   _                 \n" +
                " / ____|                          |  __ \\                 (_)                \n" +
                "| (___   ___ _ ____   _____ _ __  | |__) |   _ _ __  _ __  _ _ __ ___   __ _ \n" +
                " \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__| |  _  / | | | '_ \\| '_ \\| | '_ ` _ \\ / _` |\n" +
                " ____) |  __/ |   \\ V /  __/ |    | | \\ \\ |_| | | | | | | | | | | | | | (_| |\n" +
                "|_____/ \\___|_|    \\_/ \\___|_|    |_|  \\_\\__,_|_| |_|_| |_|_|_| |_| |_|\\__, |\n" +
                "                                                                        __/ |\n" +
                "                                                                       |___/ \n"+
                "                        server version (1.0 - BIO)\n\n");
        
        // 使用传统BIO服务器
        new ServerThreadStart(clazz);
    }
    
    public static void runFileServer() throws IOException {
        new FileDataServerThreadStart();
    }
}
