package org.utils.ad;

import java.security.cert.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
/****
 *
 * @author panjianyu
 *接口类，不要删除
 */
public class DummyTrustManager implements X509TrustManager {
    public void checkClientTrusted( X509Certificate[] cert, String authType) {
        return;
    }
    
    public void checkServerTrusted( X509Certificate[] cert, String authType) {
        return;
    }
    
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}