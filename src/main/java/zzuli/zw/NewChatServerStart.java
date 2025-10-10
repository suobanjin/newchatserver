package zzuli.zw;

import zzuli.zw.main.annotation.BeanScan;
import zzuli.zw.main.annotation.MapperScan;
import zzuli.zw.main.connection.NioServerStart;
import java.io.IOException;

@BeanScan("zzuli.zw")
@MapperScan("zzuli.zw.dao")
public class NewChatServerStart {
    public static void main(String[] args)  {
        System.out.print("\n\n  _____                            _____                   _                 \n" +
                " / ____|                          |  __ \\                 (_)                \n" +
                "| (___   ___ _ ____   _____ _ __  | |__) |   _ _ __  _ __  _ _ __ ___   __ _ \n" +
                " \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__| |  _  / | | | '_ \\| '_ \\| '_ ` _ \\ / _` |\n" +
                " ____) |  __/ |   \\ V /  __/ |    | | \\ \\ |_| | | | | | | | | | | | | | (_| |\n" +
                "|_____/ \\___|_|    \\_/ \\___|_|    |_|  \\__,_|_| |_|_| |_|_|_| |_| |_|\\__, |\n" +
                "                                                                        __/ |\n" +
                "                                                                       |___/ \n"+
                "                        server version (2.0 - NIO)\n\n");
        
        // 直接使用NIO服务器启动
        NioServerStart nioServer = new NioServerStart(NewChatServerStart.class);
        nioServer.start();
    }
}
