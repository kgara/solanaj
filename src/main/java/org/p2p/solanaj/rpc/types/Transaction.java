package org.p2p.solanaj.rpc.types;

import java.util.List;

import com.squareup.moshi.Json;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {

    public static final String PROGRAM_NAME_STAKE = "stake";
    public static final String PROGRAM_NAME_SYSTEM = "system";
    public static final String INSTRUCTION_TYPE_CREATE = "createAccountWithSeed";
    public static final String INSTRUCTION_TYPE_INITIALIZE = "initialize";
    public static final String INSTRUCTION_TYPE_DELEGATE = "delegate";

    @Json(name = "message")
    private Message message;
    @Json(name = "signatures")
    private List<String> signatures;

    @Getter
    @Setter
    public static class Message {
        @Json(name = "instructions")
        private List<Instructions> instructions;
    }

    @Getter
    @Setter
    public static class Instructions {
        @Json(name = "program")
        private String program;
        @Json(name = "programId")
        private String programId;
        @Json(name = "parsed")
        private ParsedInstruction parsedInstruction;
    }

    @Getter
    @Setter
    public static class ParsedInstruction {
        @Json(name = "type")
        private String type;
        @Json(name = "info")
        private InstructionInfo info;
    }

    @Getter
    @Setter
    public static class InstructionInfo {
        @Json(name = "lamports")
        private long lamports;
    }
}
