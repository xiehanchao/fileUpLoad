package com.example.apple.myapplication;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**********************************************************************
 *
 *
 * @类名 OkHttpUtil
 * @包名 com.example.apple.myapplication
 * @author 谢晗超
 * @创建日期 2018/6/11
 ***********************************************************************/
public class OkHttpUtil {

    /**
     * 默认请求连接超时时间（秒）
     */
    private static final long DEFAULT_CONN_TIMEOUT = 30L;

    /**
     * 默认请求读取超时时间（秒）
     */
    private static final long DEFAULT_READ_TIMEOUT = 30L;

    /**
     * 默认请求写入超时时间（秒）
     */
    private static final long DEFAULT_WRITE_TIMEOUT = 30L;

    /**
     * 连接超时时间（秒）
     */
    private long mConnectTimeout = DEFAULT_CONN_TIMEOUT;

    /**
     * 读取超时时间（秒）
     */
    private long mReadTimeOut = DEFAULT_READ_TIMEOUT;

    /**
     * 读取超时时间（秒）
     */
    private long mWriteTimeOut = DEFAULT_WRITE_TIMEOUT;


    private OkHttpClient okHttpClient;

    public void postFile(String url, Callback callback, Set files) {
        okHttpClient = build();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        //第一个参数要与Servlet中的一致
        Iterator iterator = files.iterator();
        while (iterator.hasNext()){
            File file = (File) iterator.next();
            builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
        }

        MultipartBody multipartBody = builder.build();
        Request request = new Request.Builder().url(url).post(multipartBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public OkHttpClient build() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(mConnectTimeout, TimeUnit.SECONDS)
                .readTimeout(mReadTimeOut, TimeUnit.SECONDS)
                .writeTimeout(mWriteTimeOut, TimeUnit.SECONDS);
        return builder.build();
    }
}
