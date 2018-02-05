package com.ml.meliproxy.reports.api;

import java.io.Serializable;
import java.util.List;

public class ItemsDTO<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<T> items;

	public ItemsDTO() {
	}

	public ItemsDTO(List<T> items) {
		super();
		this.items = items;
	}

	public List<T> getItems() {
		return this.items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}
}
