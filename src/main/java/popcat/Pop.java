package popcat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;

import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Pop {
    okhttp3.OkHttpClient.Builder okHttpClientBuilder = new okhttp3.OkHttpClient.Builder().followSslRedirects(true)
            .cache(new Cache(new File("_cache"), 1024L * 1024L * 1024L * 1024L)).followRedirects(false);
    okhttp3.OkHttpClient nonRdrctOkHttpClient = okHttpClientBuilder.build();
    okhttp3.Headers.Builder headersBuilder = new okhttp3.Headers.Builder();
    okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
    okhttp3.Cookie.Builder cookieBuilder = new okhttp3.Cookie.Builder();
    String harFilePath = Arrays.asList(new java.io.File("./").listFiles()).stream()
            .filter(a -> a.getName().contains("har") && a.isFile()).toList().get(0).getName();
    HashMap<String, String> params = new HashMap<String, String>();
    H h;

    Pop() throws Exception {
        pop();
    }

    java.util.stream.Stream<String> arrStrContaind(String[] arr, String... contains) {
        return arrStrToStream(arr).filter(a -> {
            boolean isContains = true;
            for (String s : contains) {
                isContains = isContains & a.contains(s);
            }
            return isContains;
        });
    }

    java.util.stream.Stream<String> arrStrToStream(String[] arr) {
        return Arrays.asList(arr).stream();
    }

    String getCaptchaToken() throws Exception {
        String result = "",shFilePath = Arrays.asList(new java.io.File("./").listFiles()).stream()
            .filter(a -> a.getName().contains(".sh") && a.isFile()).toList().get(0).getName();
        String cmd = "sh " + shFilePath;
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(cmd);
        pr.waitFor();
        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = "";
        while ((line = buf.readLine()) != null) {
            result += line;
        }
        for (String a : arrStrToStream(result.split("\'")).filter(a -> a.contains("resp")).toList()) {
            for (String b : arrStrToStream(a.split("[\\[|\\]|\",\"|]")).filter(b -> !b.isEmpty() && b.length() == 462)
                    .toList()) {
                result = b;
            }
        }
        return result;
    }

    String getPostPopUrl(String url) {
        return url;
    }

    void curl() {

    }

    String getRandomPopCount() {
        return (728 + (long) (Math.random() * 70)) + "";
    }

    void pop() throws Exception {
        long total_count = 0;
        params.clear();
        this.h = new H(harFilePath) {
            {
                load("stats.popcat.click");
            }
        };
        arrStrToStream(new URI(h.url).getQuery().split("&")).filter(a -> {
            String key = a.substring(0, a.indexOf("=")), value = a.replace(key + "=", "");
            params.put(key, value);
            return false;
        });
        okhttp3.Request request = requestBuilder.url(h.url).headers(h.getReqHeadersBuilder().build())
                .post(new okhttp3.FormBody.Builder().build()).build();
        okhttp3.Call call;
        okhttp3.Response res;
        okhttp3.ResponseBody resBody;
        String resBodyString = "", pop_count = "";
        res = null;
        do {
            if (res != null) {
                res.close();
            }
            params.put("captcha_token", getCaptchaToken());
            params.put("pop_count", pop_count = getRandomPopCount());
            res = (call = nonRdrctOkHttpClient.newCall(request)).execute();
            if (res.isSuccessful()) {
                total_count += Integer.parseInt(pop_count);
            }
            resBodyString = (resBody = res.body()).string();
            System.out
                    .println(resBody + " - " + (new SimpleDateFormat("(yyyy-MM-dd hh:mm:ss.SSS) - ").format(new Date()))
                            + "(" + total_count + ")" + pop_count + " - " + resBodyString);
        } while (!call.isExecuted() || res.code() / 100 > 1 || mainPage(pop_count));
        return;
    }

    boolean mainPage(String pop_count) throws IOException {
        okhttp3.Response res;
        res = nonRdrctOkHttpClient.newCall((new Request.Builder()).url("https://popcat.click/")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:91.0) Gecko/20100101 Firefox/91.0")
                .header("Cookie", "country=KR; pop_count=" + pop_count).build()).execute();
        return res.isSuccessful();
    }

    public static void main(String[] args) throws Exception {
        new Pop();
    }
}
