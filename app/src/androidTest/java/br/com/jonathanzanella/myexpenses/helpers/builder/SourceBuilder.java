package br.com.jonathanzanella.myexpenses.helpers.builder;

import br.com.jonathanzanella.myexpenses.source.Source;

/**
 * Created by jzanella on 8/28/16.
 */

public class SourceBuilder {
	private String name = "sourceTest";
	private long updatedAt = 0L;

	public SourceBuilder name(String name) {
		this.name = name;
		return this;
	}

	public SourceBuilder updatedAt(long updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public Source build() {
		Source source = new Source();
		source.setName(name);
		source.setUpdatedAt(updatedAt);
		return source;
	}
}