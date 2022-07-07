package org.p2p.solanaj.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;
import org.p2p.solanaj.rpc.types.Signature;

public class MainnetTest {

    @Test
    public void connectToMainnet() {

        final RpcClient client = new RpcClient(Cluster.MAINNET);
        final PublicKey publicKey = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");

        try {
            // Get account Info
            final AccountInfo accountInfo = client.getApi().getAccountInfo(publicKey);
            final double balance = (double) accountInfo.getValue().getLamports() / 100000000;

            // Account data list
            final List<String> accountData = accountInfo.getValue().getData();

            // Verify "base64" string in accountData
            assertTrue(accountData.stream().anyMatch(s -> s.equalsIgnoreCase("base64")));
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getEpochFirstBlockTimestampTest() {
        final RpcClient client = new RpcClient(Cluster.MAINNET);
        try {
            long epochFirstBlockTimestamp = client.getApi().getEpochFirstBlockTimestamp(316);
            assertEquals(1654482411L, epochFirstBlockTimestamp);
            epochFirstBlockTimestamp = client.getApi().getEpochFirstBlockTimestamp(240);
            assertEquals(1635284456L, epochFirstBlockTimestamp);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getEpochFirstBlockTimestampNotYetTest() {
        final RpcClient client = new RpcClient(Cluster.MAINNET);
        try {
            final long epochFirstBlockTimestamp = client.getApi().getEpochFirstBlockTimestamp(Integer.MAX_VALUE);
            assertEquals(0, epochFirstBlockTimestamp);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCurrentEpochNumberTest() {
        final RpcClient client = new RpcClient(Cluster.MAINNET);
        try {
            final long currentEpochNumber = client.getApi().getCurrentEpochNumber();
            assertNotEquals(0, currentEpochNumber);
            System.out.println(currentEpochNumber);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSignaturesForAddressTest() {
        final RpcClient client = new RpcClient(Cluster.MAINNET);
        final String expectedSignature = "5AAvEgyAPtR5DZGCxdQaMAkR6AttBzgf9JjQ9wfyp4Gy6HAUnq8QA5g9pCGGYrY5m97SEN2shiqaRk13RnLAZi3f";
        try {
            final List<Signature> signaturesForAddress = client.getApi().getSignaturesForAddress("C2HCvcQbqjAcwuJGr8HukXWmRpuSP7uqxsCLprrP8VLf", 100);
            assertEquals(1, signaturesForAddress.stream().filter(s -> expectedSignature.equals(s.getSignature())).count());
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

}
