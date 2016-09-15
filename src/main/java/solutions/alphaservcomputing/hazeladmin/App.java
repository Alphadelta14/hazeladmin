
package solutions.alphaservcomputing.hazeladmin;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import java.net.InetAddress;
import java.util.Set;

public class App {
    public static void main(String[] args) {
        HazelcastInstance hazelcastInstance = createHazelcastClientInstance();
        IMap<String, String> map = hazelcastInstance.getMap("remoteDownloads");

        System.out.println(map.keySet());

        hazelcastInstance.shutdown();
    }

    private static HazelcastInstance createHazelcastClientInstance() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getGroupConfig().setName("Artifactory");
        clientConfig.getNetworkConfig().addAddress("localhost:10001");
        clientConfig.getNetworkConfig().setConnectionAttemptLimit(1);

        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    private HazelcastInstance createHazelcastServerInstance() {
        Config config = new Config();
        config.setInstanceName("Admin");
        config.getGroupConfig().setName("Artifactory");

        config.setProperty("hazelcast.version.check.enabled", "false");
        config.setProperty("hazelcast.memcache.enabled", "false");
        config.setProperty("hazelcast.merge.first.run.delay.seconds", "10");
        config.setProperty("hazelcast.merge.next.run.delay.seconds", "10");
        config.setProperty("hazelcast.shutdownhook.enabled", "false");
        config.setProperty("hazelcast.heartbeat.interval.seconds", "10");
        config.setProperty("hazelcast.max.no.master.confirmation.seconds", "60");
        config.setProperty("hazelcast.member.list.publish.interval.seconds", "30");
        config.setProperty("hazelcast.partition.count", "1");
        config.setProperty("hazelcast.mc.max.visible.instance.count", "100");

        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPort(10001);
        networkConfig.setPortAutoIncrement(false);
        networkConfig.setReuseAddress(true);

        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true);
        joinConfig.getTcpIpConfig().setRequiredMember(null);

        // networkConfig.getInterfaces().setEnabled(true)
        //   .addInterface(InetAddress.getLocalHost().toString());

        return Hazelcast.newHazelcastInstance(config);
    }
}
