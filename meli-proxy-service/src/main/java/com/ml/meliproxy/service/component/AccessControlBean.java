package com.ml.meliproxy.service.component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.ml.meliproxy.persistence.model.BlockedDoc;
import com.ml.meliproxy.persistence.repository.BlockedRepository;
import com.ml.meliproxy.service.component.AccessControlBean.LockEvent;
import com.ml.meliproxy.service.util.KeyUtil;

@Component
public class AccessControlBean implements MessageListener<LockEvent> {

	private static final String TOPIC_NAME = "accessControlTopic";

	@Autowired
	private HazelcastInstance hazelcastInstance;

	@Autowired
	private BlockedRepository blockedRepository;

	private ITopic<LockEvent> topic;

	private Map<String, Date> blockedByIp;
	private Map<String, Date> blockedByPath;
	private Map<String, Date> blockedByIpAndPath;

	@PostConstruct
	private void init() {
		BlockedDoc blockedDoc = blockedRepository.get();
		this.blockedByIp = toBlockedMap(blockedDoc.getByIp());
		this.blockedByPath = toBlockedMap(blockedDoc.getByPath());
		this.blockedByIpAndPath = toBlockedMap(blockedDoc.getByIpAndPath());

		this.topic = this.hazelcastInstance.getTopic(TOPIC_NAME);
		this.topic.addMessageListener(this);
	}

	@Override
	public void onMessage(Message<LockEvent> message) {
		LockEvent event = message.getMessageObject();

		switch (event.getType()) {
		case IP:
			this.blockedByIp.putIfAbsent(event.getKey(), new Date());
			break;
		case PATH:
			this.blockedByPath.putIfAbsent(event.getKey(), new Date());
			break;
		case IP_PATH:
			this.blockedByIpAndPath.putIfAbsent(event.getKey(), new Date());
			break;
		default:
			throw new IllegalStateException(event.getType().name());
		}
	}

	public boolean isBlocked(String ip, String path) {
		return this.blockedByIp.containsKey(ip) || this.blockedByPath.containsKey(path)
				|| this.blockedByIpAndPath.containsKey(KeyUtil.combine(ip, path));
	}

	public void blockByIp(String ip) {
		if (this.blockedByIp.putIfAbsent(ip, new Date()) == null) {
			LockEvent message = new LockEvent();
			message.setType(Type.IP);
			message.setKey(ip);
			this.topic.publish(message);

			this.blockedRepository.addByIp(ip);
		}
	}

	public void blockByPath(String path) {
		if (this.blockedByPath.putIfAbsent(path, new Date()) == null) {
			LockEvent message = new LockEvent();
			message.setType(Type.PATH);
			message.setKey(path);
			this.topic.publish(message);

			this.blockedRepository.addByPath(path);
		}
	}

	public void blockByIpAndPath(String combinedKey) {
		if (this.blockedByIpAndPath.putIfAbsent(combinedKey, new Date()) == null) {
			LockEvent message = new LockEvent();
			message.setType(Type.IP_PATH);
			message.setKey(combinedKey);
			this.topic.publish(message);

			this.blockedRepository.addByIpAndPath(combinedKey);
		}
	}

	private Map<String, Date> toBlockedMap(Set<String> set) {
		if (set == null) {
			return new HashMap<>();
		}
		Date now = new Date();
		return set.stream().collect(Collectors.toMap(Function.identity(), v -> now));
	}

	protected enum Type {
		IP, PATH, IP_PATH;
	}

	protected static class LockEvent implements Serializable {
		private static final long serialVersionUID = 1L;

		private String key;
		private Type type;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}
	}
}
