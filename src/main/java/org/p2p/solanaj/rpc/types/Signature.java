package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Signature {

    @Json(name = "blockTime")
    private long blockTime;
    @Json(name = "confirmationStatus")
    private String confirmationStatus;
    @Json(name = "err")
    private String err;
    @Json(name = "memo")
    private String memo;
    @Json(name = "signature")
    private String signature;
    @Json(name = "slot")
    private long slot;

}
