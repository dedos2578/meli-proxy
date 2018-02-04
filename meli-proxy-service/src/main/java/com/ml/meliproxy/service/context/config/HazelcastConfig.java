package com.ml.meliproxy.service.context.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
public class HazelcastConfig {
	@Value("${hazelcast.network.port:6701}")
	private int port;

	@Value("${hazelcast.interface:127.0.0.1}")
	private String hazelcastInterface;

	@Value("#{'${hazelcast.network.tcp-ip-members:127.0.0.1}'.split(',')}")
	private List<String> tcpIpMembers;

	@Bean
	public HazelcastInstance getHazelcast() {
		// Configure Hazelcast Properties
		Config config = new Config().setProperty("hazelcast.logging.type", "slf4j");

		// Configure Hazelcast Network
		NetworkConfig network = config.getNetworkConfig().setPort(this.port).setPortAutoIncrement(true);
		network.getInterfaces().addInterface(this.hazelcastInterface).setEnabled(true);
		network.getJoin().getTcpIpConfig().setRequiredMember(null).setMembers(this.tcpIpMembers).setEnabled(true);
		network.getJoin().getMulticastConfig().setEnabled(false);

		return Hazelcast.newHazelcastInstance(config);
	}
}
