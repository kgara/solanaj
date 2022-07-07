package org.p2p.solanaj.rpc;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.rpc.types.AccountInfo;
import org.p2p.solanaj.rpc.types.ConfigObjects.ConfirmedSignFAddr2;
import org.p2p.solanaj.rpc.types.ConfigObjects.Filter;
import org.p2p.solanaj.rpc.types.ConfigObjects.Memcmp;
import org.p2p.solanaj.rpc.types.ConfigObjects.ProgramAccountConfig;
import org.p2p.solanaj.rpc.types.ConfirmedTransaction;
import org.p2p.solanaj.rpc.types.Epoch;
import org.p2p.solanaj.rpc.types.GetTransactionRes;
import org.p2p.solanaj.rpc.types.InflationReward;
import org.p2p.solanaj.rpc.types.ProgramAccount;
import org.p2p.solanaj.rpc.types.RecentBlockhash;
import org.p2p.solanaj.rpc.types.RpcResultTypes.ValueLong;
import org.p2p.solanaj.rpc.types.RpcSendTransactionConfig;
import org.p2p.solanaj.rpc.types.RpcSendTransactionConfig.Encoding;
import org.p2p.solanaj.rpc.types.Signature;
import org.p2p.solanaj.rpc.types.SignatureInformation;
import org.p2p.solanaj.ws.SubscriptionWebSocketClient;
import org.p2p.solanaj.ws.listeners.NotificationEventListener;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class RpcApi {
    public static final long SLOTS_PER_EPOCH = 432000L;
    private RpcClient client;

    public RpcApi(RpcClient client) {
        this.client = client;
    }

    public String getRecentBlockhash() throws RpcException {
        return client.call("getRecentBlockhash", null, RecentBlockhash.class).getRecentBlockhash();
    }

    public String sendTransaction(Transaction transaction, Account signer) throws RpcException {
        return sendTransaction(transaction, Arrays.asList(signer));
    }

    public String sendTransaction(Transaction transaction, List<Account> signers) throws RpcException {
        String recentBlockhash = getRecentBlockhash();
        transaction.setRecentBlockHash(recentBlockhash);
        transaction.sign(signers);
        byte[] serializedTransaction = transaction.serialize();

        String base64Trx = Base64.getEncoder().encodeToString(serializedTransaction);

        List<Object> params = new ArrayList<Object>();

        params.add(base64Trx);
        params.add(new RpcSendTransactionConfig());

        return client.call("sendTransaction", params, String.class);
    }

    public void sendAndConfirmTransaction(Transaction transaction, List<Account> signers,
                                          NotificationEventListener listener) throws RpcException {
        String signature = sendTransaction(transaction, signers);

        SubscriptionWebSocketClient subClient = SubscriptionWebSocketClient.getInstance(client.getEndpoint());
        subClient.signatureSubscribe(signature, listener);
    }

    public long getBalance(PublicKey account) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());

        return client.call("getBalance", params, ValueLong.class).getValue();
    }

    public ConfirmedTransaction getConfirmedTransaction(String signature) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(signature);
        // TODO jsonParsed, base58, base64
        // the default encoding is JSON
        // params.add("json");

        return client.call("getConfirmedTransaction", params, ConfirmedTransaction.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<SignatureInformation> getConfirmedSignaturesForAddress2(PublicKey account, int limit)
            throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());
        params.add(new ConfirmedSignFAddr2(limit));

        List<AbstractMap> rawResult = client.call("getConfirmedSignaturesForAddress2", params, List.class);

        List<SignatureInformation> result = new ArrayList<SignatureInformation>();
        for (AbstractMap item : rawResult) {
            result.add(new SignatureInformation(item));
        }

        return result;
    }

    public List<ProgramAccount> getProgramAccounts(PublicKey account, long offset, String bytes) throws RpcException {
        List<Object> filters = new ArrayList<Object>();
        filters.add(new Filter(new Memcmp(offset, bytes)));

        ProgramAccountConfig programAccountConfig = new ProgramAccountConfig(filters);
        return getProgramAccounts(account, programAccountConfig);
    }

    public List<ProgramAccount> getProgramAccounts(PublicKey account) throws RpcException {
        return getProgramAccounts(account, new ProgramAccountConfig(Encoding.base64));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<ProgramAccount> getProgramAccounts(PublicKey account, ProgramAccountConfig programAccountConfig)
            throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());

        if (programAccountConfig != null) {
            params.add(programAccountConfig);
        }

        List<AbstractMap> rawResult = client.call("getProgramAccounts", params, List.class);

        List<ProgramAccount> result = new ArrayList<ProgramAccount>();
        for (AbstractMap item : rawResult) {
            result.add(new ProgramAccount(item));
        }

        return result;
    }

    public AccountInfo getAccountInfo(PublicKey account) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());
        params.add(new RpcSendTransactionConfig());

        return client.call("getAccountInfo", params, AccountInfo.class);
    }

    public long getMinimumBalanceForRentExemption(long dataLength) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(dataLength);

        return client.call("getMinimumBalanceForRentExemption", params, Long.class);
    }

    public long getBlockTime(long block) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(block);

        return client.call("getBlockTime", params, Long.class);
    }

    public String requestAirdrop(PublicKey address, long lamports) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(address.toString());
        params.add(lamports);

        return client.call("requestAirdrop", params, String.class);
    }

    @SuppressWarnings({"unchecked"})
    public Map<String, InflationReward> getInflationRewards(List<String> addresses, int epoch)
            throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(addresses);

        params.add(Collections.singletonMap("epoch", epoch));

        List<Object> abstractResultsList = client.call("getInflationReward", params, List.class);

        JsonAdapter<InflationReward> adapter = new Moshi.Builder().build()
                .adapter(InflationReward.class);
        List<InflationReward> resultsList = abstractResultsList.stream().map(adapter::fromJsonValue).collect(Collectors.toList());

        if (resultsList.size() != addresses.size()) {
            throw new RpcException("Request list size does not match the response one");
        }
        Map<String, InflationReward> resultMap = new HashMap<>();
        for (int i = 0; i < resultsList.size(); i++) {
            resultMap.put(addresses.get(i), resultsList.get(i));
        }

        return resultMap;
    }

    //It is pretty unreliable to use this API for multiple address calls. In the response there is no binding between address and reward.
    //If address has no reward in that epoch - the null placeholder is returned in it place, so ordering might be a workaround, but...
    @SuppressWarnings({"unchecked"})
    public InflationReward getInflationReward(String address, int epoch)
            throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(List.of(address));

        params.add(Collections.singletonMap("epoch", epoch));

        List<Object> abstractResultsList = client.call("getInflationReward", params, List.class);
        if (abstractResultsList.size() > 1) {
            throw new RpcException("Request list size should be zero or one");
        }

        JsonAdapter<InflationReward> adapter = new Moshi.Builder().build()
                .adapter(InflationReward.class);

        return abstractResultsList.stream().map(adapter::fromJsonValue).findFirst().orElse(null);

    }

    @SuppressWarnings({"unchecked"})
    public List<Signature> getSignaturesForAddress(String address, int limit)
            throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(address);

        params.add(Collections.singletonMap("limit", limit));

        List<Object> abstractResultsList = client.call("getSignaturesForAddress", params, List.class);

        JsonAdapter<Signature> adapter = new Moshi.Builder().build()
                .adapter(Signature.class);

        return abstractResultsList.stream().map(adapter::fromJsonValue).collect(Collectors.toList());

    }

    public GetTransactionRes getTransaction(String signature) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(signature);
        params.add("jsonParsed");

        return client.call("getTransaction", params, GetTransactionRes.class);
    }

    @SuppressWarnings({"unchecked"})
    public long getEpochFirstBlockTimestamp(int epoch) throws RpcException {
        long epochFirstSlot = epoch * SLOTS_PER_EPOCH;
        List<Double> abstractResultsList = client.call("getBlocksWithLimit", List.of(epochFirstSlot, 1), List.class);
        if (abstractResultsList.size() == 0) {
            return 0;
        } else if (abstractResultsList.size() > 1) {
            throw new RpcException("Result list size should be 0 or 1");
        }
        final long epochFirstBlockNumber = abstractResultsList.stream()
                .findFirst().orElseThrow(() -> new RpcException("Request list size should be zero or one")).longValue();
        //
        return client.call("getBlockTime", List.of(epochFirstBlockNumber), Long.class);
    }

    @SuppressWarnings({"unchecked"})
    public int getCurrentEpochNumber() throws RpcException {
        Epoch epoch = client.call("getEpochInfo", null, Epoch.class);
        return epoch.getEpoch();
    }

}
