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
			this.blockedByIp.put(event.getKey(), new Date());
			break;
		case PATH:
			this.blockedByPath.put(event.getKey(), new Date());
			break;
		case IP_PATH:
			this.blockedByIpAndPath.put(event.getKey(), new Date());
			break;
		default:
			throw new IllegalStateException(event.getType().name());
		}
	}

	public boolean isBlocked(String ip, String path) {
		return blockedByIp.containsKey(ip) || blockedByPath.containsKey(path)
				|| blockedByIpAndPath.containsKey(KeyUtil.combine(ip, path));
	}

	public void block(String key, Type type) {
		LockEvent message = new LockEvent();
		message.setType(type);
		message.setKey(key);
		this.topic.publish(message);

		switch (type) {
		case IP:
			blockedRepository.addByIp(key);
			break;
		case PATH:
			blockedRepository.addByPath(key);
			break;
		case IP_PATH:
			blockedRepository.addByIpAndPath(key);
			break;
		default:
			throw new IllegalStateException(type.name());
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
