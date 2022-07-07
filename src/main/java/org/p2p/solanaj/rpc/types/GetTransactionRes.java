package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTransactionRes {

    @Json(name = "slot")
    private long slot;
    @Json(name = "transaction")
    private Transaction transaction;

}
