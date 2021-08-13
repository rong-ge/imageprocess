package com.baidu.translate.pic;

import com.baidu.translate.data.Config;
import com.baidu.translate.http.HttpClient;
import com.baidu.translate.http.HttpParams;
import com.baidu.translate.http.HttpStringCallback;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;

/**
 * PicTranslate
 */
public class PicTranslate {
    private static final String PIC_URL = "https://fanyi-api.baidu.com/api/trans/sdk/picture";
    private static final String FILE_CONTENT_TYPE = "mutipart/form-data";

    private final HttpClient httpClient;
    private Config config;

    public PicTranslate() {
        this.httpClient = new HttpClient();
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * 图片翻译
     *
     * @param callback 请求回调
     */
    public void trans(HttpStringCallback callback) {
        if (callback == null || config == null) {
            return;
        }
        HttpParams params = new HttpParams();
        params.put("image", new File(config.getPicPath()), FILE_CONTENT_TYPE);
        params.put("from", config.getFrom());
        params.put("to", config.getTo());
        params.put("appid", config.getAppId());
        params.put("salt", System.currentTimeMillis() / 1000L);
        params.put("cuid", "APICUID");
        params.put("mac", "mac");
        params.put("version", "3");
        params.put("paste", config.getPaste());
        params.put("erase", config.getErase());
        params.put("sign", PicSigner.sign(config, params));
        httpClient.post(PIC_URL, params, callback);
    }

    public Response trans() throws IOException {

        HttpParams params = new HttpParams();
        params.put("image", new File(config.getPicPath()), FILE_CONTENT_TYPE);
        params.put("from", config.getFrom());
        params.put("to", config.getTo());
        params.put("appid", config.getAppId());
        params.put("salt", System.currentTimeMillis() / 1000L);
        params.put("cuid", "APICUID");
        params.put("mac", "mac");
        params.put("version", "3");
        params.put("paste", config.getPaste());
        params.put("erase", config.getErase());
        params.put("sign", PicSigner.sign(config, params));
        return httpClient.syncPost(PIC_URL, params);
    }
}
