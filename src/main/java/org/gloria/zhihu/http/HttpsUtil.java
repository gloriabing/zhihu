package org.gloria.zhihu.http;

import okhttp3.*;
import org.apache.commons.lang.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create on 2016/12/22 17:38.
 *
 * @author : gloria.
 */
public class HttpsUtil {

    static OkHttpClient client;
    static String _xsrf = "";

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                if (null != cookies) {
                    for (Cookie cookie : cookies) {
                        if (cookie.name().equals("_xsrf")) {
                            _xsrf = cookie.value();
                        }
                    }
                }
                return cookies != null ? cookies : new ArrayList<>();
            }
        });
        client = builder.sslSocketFactory(getCertificates()) // getCertifiactes()方法在下面
                .build();
    }

    public static String get(String url, boolean isWap) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        String body = null;
        if (isWap) {
            builder.
                    header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
            builder.addHeader("X-Xsrftoken", "819c02637352c452209a22bbd84c44f3");
            builder.addHeader("Cookie", "l_n_c=1; _za=258bde61-d23b-404b-8db8-3f90c07b56c2; udid=\"AFCArGxDmQmPTuxRwQig6WsQHFDM3tmv1ho=|1457684153\"; d_c0=\"ADDADRL-tQmPTosgN2w2_UVS7c-Y-kj0CnE=|1459612129\"; _zap=bee4202b-f779-4712-9596-5a3e17a11b90; a_t=\"2.0AABAllQfAAAXAAAA99RlWAAAQJZUHwAAADDADRL-tQkXAAAAYQJVTT7UZVgAfOM3pOUZVgq_H7vG1ITMZAMNoYeWmz-xyPPfFBAl81Bwn4s3SagIqQ==\"; _xsrf=819c02637352c452209a22bbd84c44f3; q_c1=7afed27604d84c2999383014a5c14fd6|1481943565000|1462270099000; _ga=GA1.2.1911791909.1458204130; o_act=login; l_cap_id=\"NmU3MDE5NmM4YjE2NDhmYzk0NmIzOWU3ZjkwZDVhZmI=|1482590807|4b9cb128b8f97714c8133ab940f1fb8131f83f13\"; cap_id=\"ZDQ5YjI1ODdhNDE0NGFmM2JmZGMzMTE5NzFhYjI5ZmM=|1482590807|b60623f4cb20e2b0709d93be110d8cac7f75cdfa\"; r_cap_id=\"MWI4MTcwOTg5MzVhNGUzMGJiNGM4M2NlMWE2MzVhYTU=|1482590808|ed89e82940cade93a3d7728dba4c54fa15c74533\"; login=\"MzA0ZjhlYzNiMGJkNDA0YThiZmNjNTZkNGM5OWUzYWI=|1482590829|116b1dbf04ca33fccc43a5cb2e61a59f19d43959\"; n_c=1; __utmt=1; z_c0=Mi4wQUFCQWxsUWZBQUFBTU1BTkV2NjFDUmNBQUFCaEFsVk4zeGVHV0FDeUR1Z1pibElxTnNRMGswQ1NNMFdMZUtVbVJn|1482677281|6dc955cc59016a9bcf3a142e38ab10fbb4983464; __utma=51854390.1911791909.1458204130.1482590811.1482676925.4; __utmb=51854390.6.10.1482676925; __utmc=51854390; __utmz=51854390.1482676925.4.4.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=51854390.110--|2=registration_date=20131017=1^3=entry_date=20131017=1; _za=258bde61-d23b-404b-8db8-3f90c07b56c2; udid=\"AFCArGxDmQmPTuxRwQig6WsQHFDM3tmv1ho=|1457684153\"; d_c0=\"ADDADRL-tQmPTosgN2w2_UVS7c-Y-kj0CnE=|1459612129\"; _zap=bee4202b-f779-4712-9596-5a3e17a11b90; a_t=\"2.0AABAllQfAAAXAAAA99RlWAAAQJZUHwAAADDADRL-tQkXAAAAYQJVTT7UZVgAfOM3pOUZVgq_H7vG1ITMZAMNoYeWmz-xyPPfFBAl81Bwn4s3SagIqQ==\"; _xsrf=819c02637352c452209a22bbd84c44f3; q_c1=7afed27604d84c2999383014a5c14fd6|1481943565000|1462270099000; _ga=GA1.2.1911791909.1458204130; o_act=login; l_cap_id=\"NmU3MDE5NmM4YjE2NDhmYzk0NmIzOWU3ZjkwZDVhZmI=|1482590807|4b9cb128b8f97714c8133ab940f1fb8131f83f13\"; cap_id=\"ZDQ5YjI1ODdhNDE0NGFmM2JmZGMzMTE5NzFhYjI5ZmM=|1482590807|b60623f4cb20e2b0709d93be110d8cac7f75cdfa\"; r_cap_id=\"MWI4MTcwOTg5MzVhNGUzMGJiNGM4M2NlMWE2MzVhYTU=|1482590808|ed89e82940cade93a3d7728dba4c54fa15c74533\"; login=\"MzA0ZjhlYzNiMGJkNDA0YThiZmNjNTZkNGM5OWUzYWI=|1482590829|116b1dbf04ca33fccc43a5cb2e61a59f19d43959\"; n_c=1; __utmt=1; z_c0=Mi4wQUFCQWxsUWZBQUFBTU1BTkV2NjFDUmNBQUFCaEFsVk4zeGVHV0FDeUR1Z1pibElxTnNRMGswQ1NNMFdMZUtVbVJn|1482720832|51dce9a708b66af935a0c661eb565ae9804463a9; __utma=51854390.1911791909.1458204130.1482584840.1482590811.3; __utmb=51854390.4.10.1482720645; __utmc=51854390; __utmz=51854390.1482590811.3.3.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=51854390.110--|2=registration_date=20131017=1^3=entry_date=20131017=1");

            Request request = builder.build();
            Response response = null;
            try {
                response = client.newBuilder()
                        .followRedirects(false)             //禁止自动重定向
                        .followSslRedirects(false)
                        .build().newCall(request).execute();
                if (response.isSuccessful()) {
                    body = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        } else {
            builder.
                    header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.98 Safari/537.36");
            builder.addHeader("X-Xsrftoken", "819c02637352c452209a22bbd84c44f3");
            builder.addHeader("Cookie", "l_n_c=1; _za=258bde61-d23b-404b-8db8-3f90c07b56c2; udid=\"AFCArGxDmQmPTuxRwQig6WsQHFDM3tmv1ho=|1457684153\"; d_c0=\"ADDADRL-tQmPTosgN2w2_UVS7c-Y-kj0CnE=|1459612129\"; _zap=bee4202b-f779-4712-9596-5a3e17a11b90; a_t=\"2.0AABAllQfAAAXAAAA99RlWAAAQJZUHwAAADDADRL-tQkXAAAAYQJVTT7UZVgAfOM3pOUZVgq_H7vG1ITMZAMNoYeWmz-xyPPfFBAl81Bwn4s3SagIqQ==\"; _xsrf=819c02637352c452209a22bbd84c44f3; q_c1=7afed27604d84c2999383014a5c14fd6|1481943565000|1462270099000; _ga=GA1.2.1911791909.1458204130; o_act=login; l_cap_id=\"NmU3MDE5NmM4YjE2NDhmYzk0NmIzOWU3ZjkwZDVhZmI=|1482590807|4b9cb128b8f97714c8133ab940f1fb8131f83f13\"; cap_id=\"ZDQ5YjI1ODdhNDE0NGFmM2JmZGMzMTE5NzFhYjI5ZmM=|1482590807|b60623f4cb20e2b0709d93be110d8cac7f75cdfa\"; r_cap_id=\"MWI4MTcwOTg5MzVhNGUzMGJiNGM4M2NlMWE2MzVhYTU=|1482590808|ed89e82940cade93a3d7728dba4c54fa15c74533\"; login=\"MzA0ZjhlYzNiMGJkNDA0YThiZmNjNTZkNGM5OWUzYWI=|1482590829|116b1dbf04ca33fccc43a5cb2e61a59f19d43959\"; n_c=1; __utmt=1; z_c0=Mi4wQUFCQWxsUWZBQUFBTU1BTkV2NjFDUmNBQUFCaEFsVk4zeGVHV0FDeUR1Z1pibElxTnNRMGswQ1NNMFdMZUtVbVJn|1482677281|6dc955cc59016a9bcf3a142e38ab10fbb4983464; __utma=51854390.1911791909.1458204130.1482590811.1482676925.4; __utmb=51854390.6.10.1482676925; __utmc=51854390; __utmz=51854390.1482676925.4.4.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=51854390.110--|2=registration_date=20131017=1^3=entry_date=20131017=1; _za=258bde61-d23b-404b-8db8-3f90c07b56c2; udid=\"AFCArGxDmQmPTuxRwQig6WsQHFDM3tmv1ho=|1457684153\"; d_c0=\"ADDADRL-tQmPTosgN2w2_UVS7c-Y-kj0CnE=|1459612129\"; _zap=bee4202b-f779-4712-9596-5a3e17a11b90; a_t=\"2.0AABAllQfAAAXAAAA99RlWAAAQJZUHwAAADDADRL-tQkXAAAAYQJVTT7UZVgAfOM3pOUZVgq_H7vG1ITMZAMNoYeWmz-xyPPfFBAl81Bwn4s3SagIqQ==\"; _xsrf=819c02637352c452209a22bbd84c44f3; q_c1=7afed27604d84c2999383014a5c14fd6|1481943565000|1462270099000; _ga=GA1.2.1911791909.1458204130; o_act=login; l_cap_id=\"NmU3MDE5NmM4YjE2NDhmYzk0NmIzOWU3ZjkwZDVhZmI=|1482590807|4b9cb128b8f97714c8133ab940f1fb8131f83f13\"; cap_id=\"ZDQ5YjI1ODdhNDE0NGFmM2JmZGMzMTE5NzFhYjI5ZmM=|1482590807|b60623f4cb20e2b0709d93be110d8cac7f75cdfa\"; r_cap_id=\"MWI4MTcwOTg5MzVhNGUzMGJiNGM4M2NlMWE2MzVhYTU=|1482590808|ed89e82940cade93a3d7728dba4c54fa15c74533\"; login=\"MzA0ZjhlYzNiMGJkNDA0YThiZmNjNTZkNGM5OWUzYWI=|1482590829|116b1dbf04ca33fccc43a5cb2e61a59f19d43959\"; n_c=1; __utmt=1; z_c0=Mi4wQUFCQWxsUWZBQUFBTU1BTkV2NjFDUmNBQUFCaEFsVk4zeGVHV0FDeUR1Z1pibElxTnNRMGswQ1NNMFdMZUtVbVJn|1482720832|51dce9a708b66af935a0c661eb565ae9804463a9; __utma=51854390.1911791909.1458204130.1482584840.1482590811.3; __utmb=51854390.4.10.1482720645; __utmc=51854390; __utmz=51854390.1482590811.3.3.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=51854390.110--|2=registration_date=20131017=1^3=entry_date=20131017=1");
            Request request = builder.build();

            Response response = null;
            try {
                response = client.newBuilder()
                        .build().newCall(request).execute();
                if (response.isSuccessful()) {
                    body = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        } 
        
        return body;
    }

    public static InputStream getAsInputStream(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url).build();
        Response response = client.newBuilder()
                .followRedirects(false)             //禁止自动重定向
                .followSslRedirects(false)
                .build().newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().byteStream();
        }
        return null;
    }

    public static String post(String url ,Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        params.forEach((k, v) -> builder.add(k, v));

        RequestBody body = builder.build();

        Request.Builder requestBuilder = new Request.Builder().url(url)
                .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
        if (StringUtils.isNotBlank(_xsrf)) {
            requestBuilder.header("X-Xsrftoken", _xsrf);
        }
        requestBuilder.addHeader("X-Xsrftoken", "819c02637352c452209a22bbd84c44f3");
        requestBuilder.addHeader("Cookie", "_za=258bde61-d23b-404b-8db8-3f90c07b56c2; udid=\"AFCArGxDmQmPTuxRwQig6WsQHFDM3tmv1ho=|1457684153\"; d_c0=\"ADDADRL-tQmPTosgN2w2_UVS7c-Y-kj0CnE=|1459612129\"; _zap=bee4202b-f779-4712-9596-5a3e17a11b90; a_t=\"2.0AABAllQfAAAXAAAA99RlWAAAQJZUHwAAADDADRL-tQkXAAAAYQJVTT7UZVgAfOM3pOUZVgq_H7vG1ITMZAMNoYeWmz-xyPPfFBAl81Bwn4s3SagIqQ==\"; _xsrf=819c02637352c452209a22bbd84c44f3; q_c1=7afed27604d84c2999383014a5c14fd6|1481943565000|1462270099000; _ga=GA1.2.1911791909.1458204130; o_act=login; l_cap_id=\"NmU3MDE5NmM4YjE2NDhmYzk0NmIzOWU3ZjkwZDVhZmI=|1482590807|4b9cb128b8f97714c8133ab940f1fb8131f83f13\"; cap_id=\"ZDQ5YjI1ODdhNDE0NGFmM2JmZGMzMTE5NzFhYjI5ZmM=|1482590807|b60623f4cb20e2b0709d93be110d8cac7f75cdfa\"; r_cap_id=\"MWI4MTcwOTg5MzVhNGUzMGJiNGM4M2NlMWE2MzVhYTU=|1482590808|ed89e82940cade93a3d7728dba4c54fa15c74533\"; __utmt=1; __utma=51854390.1911791909.1458204130.1482584840.1482590811.3; __utmb=51854390.2.10.1482590811; __utmc=51854390; __utmz=51854390.1482590811.3.3.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=51854390.010--|2=registration_date=20131017=1^3=entry_date=20160503=1; login=\"MzA0ZjhlYzNiMGJkNDA0YThiZmNjNTZkNGM5OWUzYWI=|1482590829|116b1dbf04ca33fccc43a5cb2e61a59f19d43959\"; z_c0=Mi4wQUFCQWxsUWZBQUFBTU1BTkV2NjFDUmNBQUFCaEFsVk5iUmVHV0FEYlVxU3d4QjlFTUkwbVN1MUhZNTduQU5HbUZ3|1482590829|95962e0bc2a26cdac2cf5003d4a4bbe1b939255c; n_c=1");
        Request request = requestBuilder.post(body).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String newPost(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        params.forEach((k, v) -> builder.add(k, v));

        RequestBody body = builder.build();

        Request.Builder requestBuilder = new Request.Builder().url(url)
                .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
        if (StringUtils.isNotBlank(_xsrf)) {
            requestBuilder.header("X-Xsrftoken", _xsrf);
        }
        requestBuilder.addHeader("X-Xsrftoken", "819c02637352c452209a22bbd84c44f3");
        requestBuilder.addHeader("Cookie", "_za=258bde61-d23b-404b-8db8-3f90c07b56c2; udid=\"AFCArGxDmQmPTuxRwQig6WsQHFDM3tmv1ho=|1457684153\"; d_c0=\"ADDADRL-tQmPTosgN2w2_UVS7c-Y-kj0CnE=|1459612129\"; _zap=bee4202b-f779-4712-9596-5a3e17a11b90; a_t=\"2.0AABAllQfAAAXAAAA99RlWAAAQJZUHwAAADDADRL-tQkXAAAAYQJVTT7UZVgAfOM3pOUZVgq_H7vG1ITMZAMNoYeWmz-xyPPfFBAl81Bwn4s3SagIqQ==\"; _xsrf=819c02637352c452209a22bbd84c44f3; q_c1=7afed27604d84c2999383014a5c14fd6|1481943565000|1462270099000; _ga=GA1.2.1911791909.1458204130; o_act=login; l_cap_id=\"NmU3MDE5NmM4YjE2NDhmYzk0NmIzOWU3ZjkwZDVhZmI=|1482590807|4b9cb128b8f97714c8133ab940f1fb8131f83f13\"; cap_id=\"ZDQ5YjI1ODdhNDE0NGFmM2JmZGMzMTE5NzFhYjI5ZmM=|1482590807|b60623f4cb20e2b0709d93be110d8cac7f75cdfa\"; r_cap_id=\"MWI4MTcwOTg5MzVhNGUzMGJiNGM4M2NlMWE2MzVhYTU=|1482590808|ed89e82940cade93a3d7728dba4c54fa15c74533\"; login=\"MzA0ZjhlYzNiMGJkNDA0YThiZmNjNTZkNGM5OWUzYWI=|1482590829|116b1dbf04ca33fccc43a5cb2e61a59f19d43959\"; n_c=1; __utmt=1; z_c0=Mi4wQUFCQWxsUWZBQUFBTU1BTkV2NjFDUmNBQUFCaEFsVk4zeGVHV0FDeUR1Z1pibElxTnNRMGswQ1NNMFdMZUtVbVJn|1482677274|78ef0b6d723bacb9a36f35ad382c488a859db8b2; __utma=51854390.1911791909.1458204130.1482590811.1482676925.4; __utmb=51854390.4.10.1482676925; __utmc=51854390; __utmz=51854390.1482676925.4.4.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=51854390.100--|2=registration_date=20131017=1^3=entry_date=20131017=1/; __utmv=51854390.010--|2=registration_date=20131017=1^3=entry_date=20160503=1; login=\"MzA0ZjhlYzNiMGJkNDA0YThiZmNjNTZkNGM5OWUzYWI=|1482590829|116b1dbf04ca33fccc43a5cb2e61a59f19d43959\"; z_c0=Mi4wQUFCQWxsUWZBQUFBTU1BTkV2NjFDUmNBQUFCaEFsVk5iUmVHV0FEYlVxU3d4QjlFTUkwbVN1MUhZNTduQU5HbUZ3|1482590829|95962e0bc2a26cdac2cf5003d4a4bbe1b939255c; n_c=1");
        Request request = requestBuilder.post(body).build();
        Call call = new OkHttpClient().newBuilder()
                .sslSocketFactory(getCertificates()).build().newCall(request);
        try {
            Response response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SSLSocketFactory getCertificates() {
        try {
            InputStream inputStream = HttpsUtil.class.getClassLoader().getResourceAsStream("*.zhihu.com.cer");
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);

            int index = 0;
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(inputStream));

            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
