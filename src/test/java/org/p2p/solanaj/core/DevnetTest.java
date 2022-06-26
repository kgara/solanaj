package org.p2p.solanaj.core;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Base58;
import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;
import org.p2p.solanaj.rpc.types.ConfigObjects;
import org.p2p.solanaj.rpc.types.ProgramAccount;
import org.p2p.solanaj.rpc.types.RpcSendTransactionConfig;

public class DevnetTest {

    @Test
    public void connectToDev() {

        final RpcClient client = new RpcClient(Cluster.DEVNET);
        final PublicKey publicKey = new PublicKey("F95vVhuyAjAtmXbg2EnNVWKkD5yQsDS5S83Uw1TUDcZm");

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
    public void getStakesByValidator() {

        final RpcClient client = new RpcClient(Cluster.DEVNET);
        final PublicKey programPublicKey = new PublicKey("Stake11111111111111111111111111111111111111");
        final PublicKey validatorAccountPublicKey = new PublicKey("F95vVhuyAjAtmXbg2EnNVWKkD5yQsDS5S83Uw1TUDcZm");

        try {
            List<Object> filters = new ArrayList<Object>();
            // This filter is just a mindless copypaste from rust call implementation, seems to work the same without it
            final byte[] filterArg1 = {2, 0, 0, 0};
            filters.add(new ConfigObjects.Filter(new ConfigObjects.Memcmp(0, Base58.encode(filterArg1))));
            filters.add(new ConfigObjects.Filter(new ConfigObjects.Memcmp(124, validatorAccountPublicKey.toBase58())));

            ConfigObjects.ProgramAccountConfig programAccountConfig = new ConfigObjects.ProgramAccountConfig(RpcSendTransactionConfig.Encoding.base64, filters);

            final List<ProgramAccount> programAccounts = client.getApi().getProgramAccounts(programPublicKey, programAccountConfig);
            assertTrue(programAccounts.size() > 0);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

}
