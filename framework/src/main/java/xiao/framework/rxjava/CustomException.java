package xiao.framework.rxjava;


/**
 * Created by xiaoguochang on 2016/5/30.
 * 自定义App错误
 */
public class CustomException extends Exception{
    public int code;
    public String message;

    public CustomException(int code, String message){
        this.code = code;
        this.message = message;
    }
}
