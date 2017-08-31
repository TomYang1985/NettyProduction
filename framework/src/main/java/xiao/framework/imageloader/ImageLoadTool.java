package xiao.framework.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;

import xiao.framework.R;
import xiao.framework.util.DensityUtils;

/**
 * Created by guochang on 2015/8/7.
 * 图片加载工具类
 */
public class ImageLoadTool {
    //图片圆角弧度
    private static final int IMAGE_ROUND_DP = 3;
    private static final int FADE_IN_DURATION = 1200;
    //纯色背景
    private static final int LOADING_IMAGE = R.mipmap.fw_ic_ht_default_loading_empty;
    public static final int DEFAULT_EMPTY_IMAGE = R.mipmap.fw_ic_ht_default_loading_empty;

    //加载大图资源
    private static final int DEFAULT_BIG_LOADING_IMAGE = R.mipmap.fw_ic_ht_default_big_loading;
    private static final int DEFAULT_BIG_LOADING_ERROR_IMAGE = R.mipmap.fw_ic_ht_default_big_loading_error;
    //加载小图资源
    private static final int DEFAULT_SMALL_LOADING_IMAGE = R.mipmap.fw_ic_ht_default_small_loading;
    private static final int DEFAULT_SMALL_LOADING_ERROR_IMAGE = R.mipmap.fw_ic_ht_default_small_loading_error;

    private static ImageLoadTool mImageLoad;
    private ImageLoader mImageLoader;
    //加载小图片(带小点的图片加载loading)
    public static DisplayImageOptions mOptionsSmallImage;
    //加载大图片(带大点的图片加载loading)
    public static DisplayImageOptions mOptionsBigImage;
    //显示头像，带有默认的头像加载图片
    public static DisplayImageOptions mOptionsAvater;
    //无loading加载图
    public static DisplayImageOptions mOptionsNoLoading;
    //圆角option
    public static DisplayImageOptions mOptionsRound;
    //正方形
    public static DisplayImageOptions mOptionsSquare;
    //从相册选择图片时使用(不缓存)
    public static DisplayImageOptions mOptionsPhotoPick;

    private ImageLoadTool(){

    }

    public static ImageLoadTool getInstance(){
        if(mImageLoad == null){
            synchronized (ImageLoadTool.class){
                if(mImageLoad == null){
                    mImageLoad = new ImageLoadTool();
                }
            }
        }

        return mImageLoad;
    }



    public void init(Context context){
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));

        mOptionsSmallImage = new DisplayImageOptions
                .Builder()
                .showImageOnLoading(DEFAULT_SMALL_LOADING_IMAGE)
                .showImageForEmptyUri(DEFAULT_EMPTY_IMAGE)
                .showImageOnFail(DEFAULT_SMALL_LOADING_ERROR_IMAGE)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        mOptionsBigImage = new DisplayImageOptions
                .Builder()
                .showImageOnLoading(DEFAULT_BIG_LOADING_IMAGE)
                .showImageForEmptyUri(DEFAULT_EMPTY_IMAGE)
                .showImageOnFail(DEFAULT_BIG_LOADING_ERROR_IMAGE)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(FADE_IN_DURATION))
                .considerExifParams(true)
                .build();

        mOptionsNoLoading = new DisplayImageOptions
                .Builder()
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(FADE_IN_DURATION))
                .considerExifParams(true)
                .build();

        /**
         * 用于本地相册浏览，相册浏览时不要开启缓存
         */
        mOptionsPhotoPick = new DisplayImageOptions
                .Builder()
                .showImageOnLoading(LOADING_IMAGE)
                .showImageForEmptyUri(DEFAULT_EMPTY_IMAGE)
                .showImageOnFail(LOADING_IMAGE)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        mOptionsRound = new DisplayImageOptions.Builder()
                .showImageOnLoading(LOADING_IMAGE)
                .showImageForEmptyUri(DEFAULT_EMPTY_IMAGE)
                .showImageOnFail(LOADING_IMAGE)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(DensityUtils.dp2px(context, IMAGE_ROUND_DP)))
                .build();

        mOptionsSquare = new DisplayImageOptions.Builder()
                .showImageOnLoading(LOADING_IMAGE)
                .showImageForEmptyUri(DEFAULT_EMPTY_IMAGE)
                .showImageOnFail(LOADING_IMAGE)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(DensityUtils.dp2px(context, 0f)))
                .build();
    }

    /**
     * 根据url获得磁盘中缓存的文件
     * @param url
     * @return
     */
    public File getFileFromDiskCache(String url){
        return mImageLoader.getDiskCache().get(url);
    }

    /**
     * 获取磁盘缓存目录
     * @return
     */
    public File getDiskCacheDirectory(){
        return mImageLoader.getDiskCache().getDirectory();
    }

    /**
     * 清除缓存
     */
    public void clear(){
        mImageLoader.getMemoryCache().clear();
        mImageLoader.getDiskCache().clear();
    }

    /**
     * 判断网络图片是否在磁盘缓存中
     * @param url
     * @return
     */
    public boolean isExistDiskCache(String url){
        if(isHttpOrHttps(url)){
            File file = mImageLoader.getDiskCache().get(url);
            if( file != null && file.exists()){
                return true;
            }
        }

        return false;
    }

    /**
     * 根据url获得内存中缓存的图片
     * @param url
     * @return
     */
    public Bitmap getBitmapFromMemoryCache(String url){
        return mImageLoader.getMemoryCache().get(url);
    }

    public void loadImage(String url, SimpleImageLoadingListener listener){
        //如果既不是http也不是https，则认为是本地图片
        if(!isHttpOrHttps(url)) {
            if(ofUri(url) != ImageDownloader.Scheme.FILE) {
                url = ImageDownloader.Scheme.FILE.wrap(url);
            }
        }
        mImageLoader.loadImage(url, listener);

    }

    public void loadImage(String url, DisplayImageOptions options, SimpleImageLoadingListener listener){
        //如果既不是http也不是https，则认为是本地图片
        if(!isHttpOrHttps(url)) {
            if(ofUri(url) != ImageDownloader.Scheme.FILE) {
                url = ImageDownloader.Scheme.FILE.wrap(url);
            }
        }
        mImageLoader.loadImage(url, options, listener);

    }

    public void loadImage(String url, int width, int height, SimpleImageLoadingListener listener){
        //如果既不是http也不是https，则认为是本地图片
        if(!isHttpOrHttps(url)) {
            if(ofUri(url) != ImageDownloader.Scheme.FILE) {
                url = ImageDownloader.Scheme.FILE.wrap(url);
            }
        }

        mImageLoader.loadImage(url, new ImageSize(width, height), mOptionsSmallImage, listener);
    }

    public void displayImage(ImageView imageView, String url, SimpleImageLoadingListener listener) {
        //如果既不是http也不是https，则认为是本地图片
        if(!isHttpOrHttps(url)) {
            if(ofUri(url) != ImageDownloader.Scheme.FILE) {
                url = ImageDownloader.Scheme.FILE.wrap(url);
            }
        }

        if(listener == null){
            mImageLoader.displayImage(url, imageView, mOptionsSmallImage);
        }else {
            mImageLoader.displayImage(url, imageView, mOptionsSmallImage, listener);
        }
    }

    /**
     * 显示图片
     * @param imageView
     * @param url 可以是网路图片url，也可以是本地路径
     * @param options
     * @param listener
     */
    public void displayImage(ImageView imageView, String url, DisplayImageOptions options, SimpleImageLoadingListener listener) {
        //如果既不是http也不是https，则认为是本地图片
        if(!isHttpOrHttps(url)) {
            if(ofUri(url) != ImageDownloader.Scheme.FILE) {
                url = ImageDownloader.Scheme.FILE.wrap(url);
            }
        }

        if(options == null){
            options = mOptionsSmallImage;
        }

        if(listener == null){
            mImageLoader.displayImage(url, imageView, options);
        }else {
            mImageLoader.displayImage(url, imageView, options, listener);
        }
    }

    /**
     * 从Drawable中加载图片
     * @param imageView
     * @param url
     * @param displayImageOptions
     */
    public void displayImageFromDrawable(ImageView imageView, String url, DisplayImageOptions displayImageOptions){
        //wrap为id添加DRAWABLE前缀
        if(ofUri(url) != ImageDownloader.Scheme.DRAWABLE) {
            url = ImageDownloader.Scheme.DRAWABLE.wrap(url);
        }

        if(displayImageOptions == null){
            displayImageOptions = mOptionsSmallImage;
        }

        mImageLoader.displayImage(url, imageView, displayImageOptions);
    }

    /**
     * 显示默认空图片
     * @param imageView
     * @param displayImageOptions
     */
    public void displayEmptyImage(ImageView imageView, DisplayImageOptions displayImageOptions){
        String url = ImageDownloader.Scheme.DRAWABLE.wrap(DEFAULT_EMPTY_IMAGE + "");

        if(displayImageOptions == null){
            displayImageOptions = mOptionsSmallImage;
        }

        mImageLoader.displayImage(url, imageView, displayImageOptions);
    }

    /**
     * 判断uri的类型，有如下7种
     * HTTP("http"), HTTPS("https")
     * FILE("file")
     * CONTENT("content"):图片文件来源于app的contentprovider
     * ASSETS("assets")
     * DRAWABLE("drawable")
     * UNKNOWN("")
     * @param url
     * @return
     */
    public ImageDownloader.Scheme ofUri(String url){
        return ImageDownloader.Scheme.ofUri(url);
    }

    /**
     * 去掉url中的本地文件头"file://"标示
     * @param url
     * @return
     */
    public String fileCrop(String url){
        if(ofUri(url) == ImageDownloader.Scheme.FILE) {
            return ImageDownloader.Scheme.FILE.crop(url);
        }

        return url;
    }

    /**
     * 在文件路径上添加"file://"头
     * @param url
     * @return
     */
    public String fileWrap(String url){
        if(ofUri(url) != ImageDownloader.Scheme.FILE) {
            return ImageDownloader.Scheme.FILE.wrap(url);
        }

        return url;
    }

    /**
     * 是否是网络图片
     * @param url
     * @return
     */
    public boolean isHttpOrHttps(String url){
        return ofUri(url) == ImageDownloader.Scheme.HTTP || ofUri(url) == ImageDownloader.Scheme.HTTPS;
    }

    /**
     * 得到缩略图(先裁剪中间部分，然后缩放到默认值150x150)
     * @param url
     * @param width <=0为默认值
     * @param height <=0为默认值
     * @return
     */
    public String getThumbImageUrl(String url, int width, int height){
        if(width <= 0 || height <= 0){
            width = 150;
            height = 150;
        }
        return url + "?imageView2/1/w/" + width + "/h/" + height;
    }
}
