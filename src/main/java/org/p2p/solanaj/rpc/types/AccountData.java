package org.p2p.solanaj.rpc.types;

import java.util.AbstractMap;
import java.util.List;

import com.squareup.moshi.Json;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Json(name = "data")
public class AccountData {

    @Json(name = "parsed")
    private Parsed parsed;
    @Json(name = "program")
    private String program;
    @Json(name = "space")
    private long space;

    @Getter
    @Setter
    public static class Parsed {
        @Json(name = "info")
        private Info info;
        @Json(name = "type")
        private String type;
    }

    @Getter
    @Setter
    public static class Info {
        @Json(name = "stake")
        private Stake stake;
    }

    @Getter
    @Setter
    public static class Stake {
        @Json(name = "creditsObserved")
        private long creditsObserved;
        @Json(name = "delegation")
        private Delegation delegation;
    }

    @Getter
    @Setter
    public static class Delegation {
        @Json(name = "activationEpoch")
        private String activationEpoch;
        @Json(name = "deactivationEpoch")
        private String deactivationEpoch;
        @Json(name = "stake")
        private String stake;
        @Json(name = "voter")
        private String voter;
    }
}
