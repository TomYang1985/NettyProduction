package xiao.framework.rxjava;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle.components.support.RxFragment;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by xiaoguochang on 2016/1/30.
 */
public class RxjavaHelper {
    /**
     * 在UI线程中运行
     *
     * @param runable
     * @param <M>
     */
    public static <M> void runOnUiThread(final RunableThread runable) {
        Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(Subscriber<? super M> sub) {
                M data = runable.run();
                sub.onNext(data);
                sub.onCompleted();
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())//在IO线程中取消订阅
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AbsSubscriber<M>() {
                    @Override
                    protected void onSuccess(M m) {

                    }

                    @Override
                    protected void onError(int code, String msg) {

                    }
                });
    }

    /**
     * 创建线程
     *
     * @param runable
     * @param <M>
     */
    public static <M> void createThread(final RunableThread runable) {
        Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(Subscriber<? super M> sub) {
                M data = runable.run();
                sub.onNext(data);
                sub.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())//在IO线程中取消订阅
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AbsSubscriber<M>() {
                    @Override
                    protected void onSuccess(M m) {

                    }

                    @Override
                    protected void onError(int code, String msg) {

                    }
                });
    }

    /**
     * 创建线程，运行耗时操作
     *
     * @param runable    创建线程业务操作（运行在线程中）
     * @param subscriber 订阅回调，运行在主线程中
     * @param <M>
     */
    public static <M> void createThread(final RunableThread runable, final AbsSubscriber<M> subscriber) {
        Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(Subscriber<? super M> sub) {
                M data = runable.run();
                sub.onNext(data);
                sub.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())//在IO线程中取消订阅
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    /**
     * add by janiszhang
     *
     * @param <M>
     * @param runable
     * @param subscriber
     */
    public static <M> Subscription createThreadwithResult(final RunableThread runable, final boolean isRunDoStart, final AbsSubscriber<M> subscriber) {
        return Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(Subscriber<? super M> sub) {
                M data = runable.run();
                sub.onNext(data);
                sub.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())//在IO线程中取消订阅
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (isRunDoStart) {
                            subscriber.doStart();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 创建线程，运行耗时操作
     *
     * @param runable    创建线程业务操作（运行在线程中）
     * @param subscriber 订阅回调，运行在主线程中
     * @param <M>
     */
    public static <M> void createThread(final RunableThread runable, final boolean isRunDoStart, final AbsSubscriber<M> subscriber) {
        Observable<M> observable = Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(Subscriber<? super M> sub) {
                M data = runable.run();
                sub.onNext(data);
                sub.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())//在IO线程中取消订阅
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (isRunDoStart) {
                            subscriber.doStart();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread());

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public static <M> void createFragmentThread(RxFragment fragment, final RunableThread runable) {
        Observable<M> observable = Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(Subscriber<? super M> sub) {
                M data = runable.run();
                sub.onNext(data);
                sub.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());//在IO线程中取消订阅

        if (fragment != null) {
            observable = observable.compose(fragment.<M>bindUntilEvent(FragmentEvent.DESTROY));//管理rxjava生命周期(可以不设置)
        }

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AbsSubscriber<M>() {
                    @Override
                    protected void onSuccess(M m) {

                    }

                    @Override
                    protected void onError(int code, String msg) {

                    }
                });
    }

    /**
     * 创建线程，运行耗时操作(与fragment生命周期绑定)
     *
     * @param fragment
     * @param runable    创建线程业务操作（运行在线程中）
     * @param subscriber 订阅回调，运行在主线程中
     * @param <M>
     */
    public static <M> void createFragmentThread(RxFragment fragment, final RunableThread runable, final boolean isRunDoStart, final AbsSubscriber<M> subscriber) {
        Observable<M> observable = Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(Subscriber<? super M> sub) {
                M data = runable.run();
                sub.onNext(data);
                sub.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())//在IO线程中取消订阅
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (isRunDoStart) {
                            subscriber.doStart();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread());

        if (fragment != null) {
            observable = observable.compose(fragment.<M>bindUntilEvent(FragmentEvent.DESTROY));//管理rxjava生命周期(可以不设置)
        }

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public static <M> void createActivityThread(RxAppCompatActivity activity, final RunableThread runable) {
        Observable<M> observable = Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(Subscriber<? super M> sub) {
                M data = runable.run();
                sub.onNext(data);
                sub.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());//在IO线程中取消订阅

        if (activity != null) {
            observable = observable.compose(activity.<M>bindUntilEvent(ActivityEvent.DESTROY));//管理rxjava生命周期(可以不设置)
        }

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AbsSubscriber<M>() {
                    @Override
                    protected void onSuccess(M m) {

                    }

                    @Override
                    protected void onError(int code, String msg) {

                    }
                });
    }

    /**
     * 创建线程，运行耗时操作(与activity生命周期绑定)
     *
     * @param activity
     * @param runable    创建线程业务操作（运行在线程中）
     * @param subscriber 订阅回调，运行在主线程中
     * @param <M>
     */
    public static <M> void createActivityThread(RxAppCompatActivity activity, final RunableThread runable, final boolean isRunDoStart, final AbsSubscriber<M> subscriber) {
        Observable<M> observable = Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(Subscriber<? super M> sub) {
                M data = runable.run();
                sub.onNext(data);
                sub.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())//在IO线程中取消订阅
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (isRunDoStart) {
                            subscriber.doStart();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread());

        if (activity != null) {
            observable = observable.compose(activity.<M>bindUntilEvent(ActivityEvent.DESTROY));//管理rxjava生命周期(可以不设置)
        }

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 发送请求，不带map变换的
     *
     * @param activity
     * @param observable
     * @param subscriber
     * @param isRunDoStart
     * @param isRunThread  subscriber回调是否在线程中运行
     * @param <M>
     */
    public static <M> void sendActivityRequest(RxAppCompatActivity activity, Observable<M> observable, final AbsSubscriber<M> subscriber, final boolean isRunDoStart, final boolean isRunThread) {
        observable = observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (isRunDoStart) {
                            subscriber.doStart();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread());

        if (activity != null) {
            observable = observable.compose(activity.<M>bindUntilEvent(ActivityEvent.DESTROY));//管理rxjava生命周期(可以不设置)
        }
        if (isRunThread) {
            observable = observable.observeOn(Schedulers.io());
        } else {
            observable = observable.observeOn(AndroidSchedulers.mainThread());
        }

        observable.subscribe(subscriber);
    }

    /**
     * 发送请求（进行一次map变换）
     *
     * @param activity
     * @param observable
     * @param func
     * @param subscriber
     * @param isRunDoStart
     * @param isRunThread  subscriber回调是否在线程中运行
     * @param <M>
     * @param <R>
     */
    public static <M, R> void sendActivityRequest(RxAppCompatActivity activity, Observable<M> observable, Func1<M, R> func, final AbsSubscriber<R> subscriber, final boolean isRunDoStart, final boolean isRunThread) {
        observable = observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (isRunDoStart) {
                            subscriber.doStart();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread());

        if (activity != null) {
            observable = observable.compose(activity.<M>bindUntilEvent(ActivityEvent.DESTROY));//管理rxjava生命周期(可以不设置)
        }

        Observable<R> newObservable = observable.observeOn(Schedulers.io())
                .map(func);

        if (isRunThread) {
            newObservable = newObservable.observeOn(Schedulers.io());
        } else {
            newObservable = newObservable.observeOn(AndroidSchedulers.mainThread());
        }

        newObservable.subscribe(subscriber);
    }

    /**
     * 发送请求，不带map变换的
     *
     * @param fragment
     * @param observable
     * @param subscriber
     * @param isRunDoStart
     * @param isRunThread  subscriber回调是否在线程中运行
     * @param <M>
     */
    public static <M> void sendFragmentRequest(RxFragment fragment, Observable<M> observable, final AbsSubscriber<M> subscriber, final boolean isRunDoStart, final boolean isRunThread) {
        observable = observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (isRunDoStart) {
                            subscriber.doStart();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread());

        if (fragment != null) {
            observable = observable.compose(fragment.<M>bindUntilEvent(FragmentEvent.DESTROY));//管理rxjava生命周期(可以不设置)
        }
        if (isRunThread) {
            observable = observable.observeOn(Schedulers.io());
        } else {
            observable = observable.observeOn(AndroidSchedulers.mainThread());
        }

        observable.subscribe(subscriber);
    }

    /**
     * 发送请求（进行一次map变换）
     *
     * @param fragment
     * @param observable
     * @param func
     * @param subscriber
     * @param isRunDoStart
     * @param isRunThread  subscriber回调是否在线程中运行
     * @param <M>
     * @param <R>
     */
    public static <M, R> void sendFragmentRequest(RxFragment fragment, Observable<M> observable, Func1<M, R> func, final AbsSubscriber<R> subscriber, final boolean isRunDoStart, final boolean isRunThread) {
        observable = observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (isRunDoStart) {
                            subscriber.doStart();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread());

        if (fragment != null) {
            observable = observable.compose(fragment.<M>bindUntilEvent(FragmentEvent.DESTROY));//管理rxjava生命周期(可以不设置)
        }

        Observable<R> newObservable = observable.observeOn(Schedulers.io()).map(func);

        if (isRunThread) {
            newObservable = newObservable.observeOn(Schedulers.io());
        } else {
            newObservable = newObservable.observeOn(AndroidSchedulers.mainThread());
        }

        newObservable.subscribe(subscriber);
    }
}
