package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class RpcSendTransactionConfig {

    public static enum Encoding {
        base64("base64"),
        //"binary" in their terms is the same as "base58"
        binary("binary"),
        jsonParsed("jsonParsed"),;

        private String enc;

        Encoding(String enc) {
            this.enc = enc;
        }

        public String getEncoding() {
            return enc;
        }

    }

    @Json(name = "encoding")
    private Encoding encoding = Encoding.base64;

}
