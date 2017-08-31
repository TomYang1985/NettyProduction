package xiao.framework.retrofit;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xiao.framework.FrameworkApi;
import xiao.framework.util.LogUtil;

/**
 * Created by xiaoguochang on 2016/1/9.
 */
public class OKHttpClientManager {
    private static OKHttpClientManager sInstance;
    private OkHttpClient mOkHttpClient;

    public static OKHttpClientManager getInstance() {
        if (sInstance == null) {
            synchronized (OKHttpClientManager.class) {
                if(sInstance == null) {
                    sInstance = new OKHttpClientManager();
                }
            }
        }

        return sInstance;
    }

    private OKHttpClientManager() {
        createOkHttpClient();
    }

    public OkHttpClient getOkHttpClient(){
        return mOkHttpClient;
    }

    private void createOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    try {
                        X509Certificate x509Certificate[] = session.getPeerCertificateChain();
                        if (x509Certificate.length == 0) {
                            return false;
                        }
                        PublicKey publicKey = x509Certificate[0].getPublicKey();

                        String str2 = "baf9c769474a8741d07e21c9ebf1d33d820f08158c7c53b319a804d59546e6dccb2a78677ec61435154727a9bc7c402aa0efd3723c824328566d13551856c2fd8fd43c3705c5dd5ebde38c1ee86380da6c07eaa3fca43de5f79a6a548e37aea1c5023ffaeba8036eea60b09f0fafb3da686b896966b313ccadaa52f2546808a144a895302e13657dc4505daf5739e8a488cfc7dc7a145baaa70d101af553c73fabe37fd5aae8e1d8d3ea8ca8af63d55f2bbd7ef8c3d2ce2ba5d379ced5176ebdc68a8051bf7e5cec8e0b5653a526c6876439716803a4badd7a129d8f11f9dc6203fe0a5b9dc2d2e2a6ecdcdf44f304143f88f7fadd621276d1ea955b688a3909";

                        if (publicKey.toString().contains(str2)) {
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                }
            });

            mOkHttpClient = builder.addInterceptor(new LoggingInterceptor()).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 拦截器
     * 可以添加header
     */
    class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) {
            Request request = chain.request();

            if (FrameworkApi.isDebug) {
                LogUtil.d("Sending %s on %s%n%s", request.toString(), chain.connection(), request.headers());
            }

            //添加header
            request = request.newBuilder().addHeader("X-Client-Type", "Android").build();

            //发起请求
            Response response = null;
            try {
                response = chain.proceed(request);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }
    }
}
