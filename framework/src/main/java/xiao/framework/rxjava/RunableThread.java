package xiao.framework.rxjava;

/**
 * Created by Administrator on 2016/4/18 0018.
 * 用户使用Observable创建线程，在线程中的回调接口
 */
public interface RunableThread {
    /**
     * run运行在创建的Observable线程中
     * @param <M>
     */
    <M> M run();
}
