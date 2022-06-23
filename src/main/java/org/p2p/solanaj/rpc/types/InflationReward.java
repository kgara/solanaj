package org.p2p.solanaj.rpc.types;

import java.math.BigDecimal;

import com.squareup.moshi.Json;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InflationReward {

    @Json(name = "amount")
    private String amount;
    @Json(name = "commission")
    private int commission;
    @Json(name = "effectiveSlot")
    private String effectiveSlot;
    @Json(name = "epoch")
    private int epoch;
    @Json(name = "postBalance")
    private String postBalance;

}
