package com.ml.meliproxy.service.component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.ml.meliproxy.service.component.AccessControlBean.LockEvent;
import com.ml.meliproxy.service.util.KeyUtil;

@Component
public class AccessControlBean implements MessageListener<LockEvent> {

	private static final String TOPIC_NAME = "accessControlTopic";

	@Autowired
	private HazelcastInstance hazelcastInstance;

	private ITopic<LockEvent> topic;

	private Map<String, Date> blockedByIp;
	private Map<String, Date> blockedByPath;
	private Map<String, Date> blockedByIpAndPath;

	@PostConstruct
	private void init() {
		this.blockedByIp = new HashMap<>();
		this.blockedByPath = new HashMap<>();
		this.blockedByIpAndPath = new HashMap<>();

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
