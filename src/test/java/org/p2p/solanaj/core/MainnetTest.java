package org.p2p.solanaj.core;

import org.bitcoinj.core.Base58;
import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.report.DelegatorsReport;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;
import org.p2p.solanaj.rpc.types.ConfigObjects;
import org.p2p.solanaj.rpc.types.InflationReward;
import org.p2p.solanaj.rpc.types.ProgramAccount;
import org.p2p.solanaj.rpc.types.RpcSendTransactionConfig;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainnetTest {

    @Test
    public void connectToMainnet() {

        final RpcClient client = new RpcClient(Cluster.MAINNET);
        final PublicKey publicKey = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");

        try {
            // Get account Info
            final AccountInfo accountInfo = client.getApi().getAccountInfo(publicKey);
            final double balance = (double) accountInfo.getValue().getLamports()/ 100000000;

            // Account data list
            final List<String> accountData = accountInfo.getValue().getData();

            // Verify "base64" string in accountData
            assertTrue(accountData.stream().anyMatch(s -> s.equalsIgnoreCase("base64")));
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void getStakesByValidatorMainnet() throws JsonProcessingException {

        final RpcClient client = new RpcClient(Cluster.MAINNET);
        final PublicKey programPublicKey = new PublicKey("Stake11111111111111111111111111111111111111");
        String validatorAccountPublicKeyString = "";
        final PublicKey validatorAccountPublicKey = new PublicKey(validatorAccountPublicKeyString);

        try {
            List<Object> filters = new ArrayList<Object>();
            // This filter is just a mindless copypaste from rust call implementation, seems to work the same without it
            final byte[] filterArg1 = {2, 0, 0, 0};
            filters.add(new ConfigObjects.Filter(new ConfigObjects.Memcmp(0, Base58.encode(filterArg1))));
            filters.add(new ConfigObjects.Filter(new ConfigObjects.Memcmp(124, validatorAccountPublicKey.toBase58())));

            ConfigObjects.ProgramAccountConfig programAccountConfig = new ConfigObjects.ProgramAccountConfig(RpcSendTransactionConfig.Encoding.jsonParsed,
                    filters);

            final List<ProgramAccount> programAccounts = client.getApi().getProgramAccounts(programPublicKey, programAccountConfig);
            ObjectMapper objectMapper = new ObjectMapper();
            List<DelegatorsReport> reports = programAccounts.stream()
                    .map(a -> DelegatorsReport.builder()
                            .delegatorAddress(a.getPubkey())
                            .validatorAddress(validatorAccountPublicKeyString)
                            .stakeAmount(new BigInteger(a.getAccount().getAccountData().getParsed().getInfo().getStake().getDelegation().getStake()))
                            .build())
                    .collect(Collectors.toList());
            List<String> delegators = reports.stream().map(DelegatorsReport::getDelegatorAddress).collect(Collectors.toList());
            //System.out.println(objectMapper.writeValueAsString(delegators));
            final int epoch = 312;
            final Map<String, InflationReward> inflationRewards = client.getApi().getInflationRewards(
                    delegators, epoch);
            List<DelegatorsReport> reportsWithRewards = new ArrayList<>();
            for (DelegatorsReport r : reports) {
                final InflationReward inflationReward = inflationRewards.get(r.getDelegatorAddress());
                BigInteger delegatorNetReward = BigInteger.ZERO;
                if (inflationReward != null) {
                    delegatorNetReward = new BigDecimal(inflationReward.getAmount()).toBigInteger();
                }
                reportsWithRewards.add(r.toBuilder().delegatorNetReward(delegatorNetReward).epoch(epoch).build());
            }

            String reportsString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reportsWithRewards);
            System.out.println(reportsString);
            assertTrue(programAccounts.size() > 0);
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

}
