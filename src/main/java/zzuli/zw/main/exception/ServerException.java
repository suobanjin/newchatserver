package zzuli.zw.main.exception;

/**
 * @author 索半斤
 * @description 服务器异常
 * @date 2022/1/25
 * @className ServeException
 */
public class ServerException extends Exception{
    public ServerException() {
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }

    public ServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
