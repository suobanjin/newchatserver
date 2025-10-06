package zzuli.zw;

import zzuli.zw.main.annotation.BeanScan;
import zzuli.zw.main.annotation.MapperScan;
import zzuli.zw.utils.RequestStart;
import java.io.IOException;

@BeanScan("zzuli.zw")
@MapperScan("zzuli.zw.dao")
public class NewChatServerStart {
    public static void main(String[] args) throws IOException {
        RequestStart.run(NewChatServerStart.class);
    }
}
