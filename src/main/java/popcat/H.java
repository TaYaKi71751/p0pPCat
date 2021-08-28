package popcat;

import java.io.File;

import de.sstoehr.harreader.HarReader;
import de.sstoehr.harreader.model.Har;
import de.sstoehr.harreader.model.HarEntry;
import de.sstoehr.harreader.model.HttpMethod;
import okhttp3.MediaType;

public class H {
    okhttp3.Headers.Builder headersBuilder = new okhttp3.Headers.Builder();
    String harFilePath, url, postDataText, resContentText;
    HarReader harReader = new HarReader();
    Har har;
    HttpMethod method;
    MediaType mediaType;

    public H(String harFilePath) throws Exception {
        this.har = harReader.readFromFile(new File(this.harFilePath = harFilePath));
        this.har.getLog();
    }

    public void reload() throws Exception {
        this.har = harReader.readFromFile(new File(harFilePath));
    }

    public void load(String str) {
        this.url = null;
        this.method = null;
        this.headersBuilder = new okhttp3.Headers.Builder();
        this.har.getLog().getEntries().stream().filter(a -> a.getRequest().getUrl().contains(str)).toList()
                .forEach(a -> {
                    this.url = a.getRequest().getUrl();
                    this.method = a.getRequest().getMethod();
                    this.postDataText = a.getRequest().getPostData().getText();
                    this.resContentText = a.getResponse().getContent().getText();
                    a.getRequest().getHeaders().stream()
                            .filter(b -> !(b.getName().contains("Accept-Encoding") || b.getName().contains("Cookie")))
                            .forEach(b -> {
                                this.headersBuilder.set(b.getName(), b.getValue());
                            });
                    this.mediaType = this.headersBuilder.get("content-type") != null
                            ? MediaType.parse(this.headersBuilder.get("content-type"))
                            : null;
                });
    }

    public void loadFirst(String str) {
        this.url = null;
        this.method = null;
        this.headersBuilder = new okhttp3.Headers.Builder();
        for (HarEntry a : this.har.getLog().getEntries().stream().filter(a -> a.getRequest().getUrl().contains(str))
                .toList()) {
            this.url = a.getRequest().getUrl();
            this.method = a.getRequest().getMethod();
            this.postDataText = a.getRequest().getPostData().getText();
            this.resContentText = a.getResponse().getContent().getText();
            a.getRequest().getHeaders().stream()
                    .filter(b -> !(b.getName().contains("Accept-Encoding") || b.getName().contains("Cookie")))
                    .forEach(b -> {
                        this.headersBuilder.set(b.getName(), b.getValue());
                    });
            this.mediaType = this.headersBuilder.get("content-type") != null
                    ? MediaType.parse(this.headersBuilder.get("content-type"))
                    : null;
        }
    }

    public okhttp3.Headers.Builder getReqHeadersBuilder() {
        return headersBuilder;
    }

    public void headerSetHar() {

    }

    public void headerSetCookie(String cookieString) {
        headersBuilder.set("Cookie", cookieString);
    }
}