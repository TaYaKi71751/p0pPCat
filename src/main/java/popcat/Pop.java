package popcat;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;

import okhttp3.Cache;
import okhttp3.Request;

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
        String result = "",
                shFilePath = Arrays.asList(new java.io.File("./").listFiles()).stream()
                        .filter(a -> a.getName().contains(".sh") && a.isFile()).toList().get(0).getName(),
                cmd = "sh " + shFilePath, line = "";
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(cmd);
        pr.waitFor();
        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
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

    String getRandomPopCount() {
        return (728 + (long) (Math.random() * 70)) + "";
    }

    void pop() throws Exception {
        long total_count = 0;
        okhttp3.Request request;
        okhttp3.Response res;
        okhttp3.ResponseBody resBody;
        String resBodyString = "", pop_count = "", url, captchaToken;
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
        res = null;
        do {
            params.put("captcha_token", captchaToken = getCaptchaToken());
            params.put("pop_count", pop_count = getRandomPopCount());
            url = h.url.replace((new URI(h.url).getQuery()), "") + "pop_count=" + pop_count + "&captcha_token="
                    + captchaToken;
            request = requestBuilder.url(url).headers(h.getReqHeadersBuilder().build())
                    .post(new okhttp3.FormBody.Builder().build()).build();
            res = nonRdrctOkHttpClient.newCall(request).execute();
            if (res.isSuccessful()) {
                total_count += Integer.parseInt(pop_count);
            }
            resBodyString = (resBody = res.body()).string();
            System.out
                    .println(resBody + " - " + (new SimpleDateFormat("(yyyy-MM-dd hh:mm:ss.SSS) - ").format(new Date()))
                            + "(" + total_count + ")" + pop_count + " - " + resBodyString);
            res.close();
            resBody.close();
        } while (mainPage(pop_count).isSuccessful || res.code() / 100 > 1);
        res.close();
    }

    class PageResult {
        boolean isSuccessful;
        String bs;
    }

    PageResult mainPage(String pop_count) throws Exception {
        okhttp3.Response res;
        res = nonRdrctOkHttpClient
                .newCall((new Request.Builder()).url("https://popcat.click/").headers(new H(harFilePath) {
                    {
                        loadFirst("https://popcat.link");
                    }
                }.getReqHeadersBuilder().build()).header("Cookie", "country=KR; pop_count=" + pop_count).build())
                .execute();
        return new PageResult() {
            {
                this.bs = res.body().string();
                this.isSuccessful = res.isSuccessful();
                res.close();
            }
        };
    }

    public static void main(String[] args) throws Exception {
        new Pop();
    }
}
