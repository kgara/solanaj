package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class EpochDto {
    @Json(name = "epoch")
    final private int epoch;
}
