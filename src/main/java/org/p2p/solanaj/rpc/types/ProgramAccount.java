package org.p2p.solanaj.rpc.types;

import java.util.AbstractMap;
import java.util.Base64;
import java.util.List;

import org.bitcoinj.core.Base58;
import org.p2p.solanaj.rpc.types.RpcSendTransactionConfig.Encoding;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class ProgramAccount {

    public final class Account {
        @Json(name = "data")
        private String data;
        private AccountData accountData;
        @Json(name = "executable")
        private boolean executable;
        @Json(name = "lamports")
        private double lamports;
        @Json(name = "owner")
        private String owner;
        @Json(name = "rentEpoch")
        private double rentEpoch;

        private String encoding;

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Account(Object acc) {
            AbstractMap account = (AbstractMap) acc;

            Object rawData = account.get("data");
            if (rawData instanceof List) {
                List<String> dataList = ((List<String>) rawData);

                this.data = dataList.get(0);
                this.encoding = (String) dataList.get(1);
            } else if (rawData instanceof String) {
                this.data = (String) rawData;
            } else {
                JsonAdapter<AccountData> adapter = new Moshi.Builder().build()
                        .adapter(AccountData.class);
                accountData = adapter.fromJsonValue(rawData);
                this.encoding = Encoding.jsonParsed.getEncoding();
            }

            this.executable = (boolean) account.get("executable");
            this.lamports = (double) account.get("lamports");
            this.owner = (String) account.get("owner");
            this.rentEpoch = (double) account.get("rentEpoch");
        }

        public String getData() {
            return data;
        }

        public byte[] getDecodedData() {
            if (encoding.equals(Encoding.base64.toString())) {
                return Base64.getDecoder().decode(data);
            }

            return Base58.decode(data);
        }

        public boolean isExecutable() {
            return executable;
        }

        public double getLamports() {
            return lamports;
        }

        public String getOwner() {
            return owner;
        }

        public double getRentEpoch() {
            return rentEpoch;
        }

        public AccountData getAccountData() {
            return accountData;
        }

        public void setAccountData(AccountData accountData) {
            this.accountData = accountData;
        }
    }

    @Json(name = "account")
    private Account account;
    @Json(name = "pubkey")
    private String pubkey;

    public Account getAccount() {
        return account;
    }

    public String getPubkey() {
        return pubkey;
    }

    public ProgramAccount() {
    }

    @SuppressWarnings({ "rawtypes" })
    public ProgramAccount(AbstractMap pa) {
        this.account = (Account) new Account(pa.get("account"));
        this.pubkey = (String) pa.get("pubkey");
    }
}
