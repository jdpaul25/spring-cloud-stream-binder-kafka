/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.binder.kafka.streams.endpoint;

import java.util.List;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.cloud.stream.binder.kafka.streams.KafkaStreamsRegistry;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.util.StringUtils;

/**
 * Actuator endpoint for topology description.
 *
 * @author Soby Chacko
 * @since 3.0.4
 */
@Endpoint(id = "topology")
public class TopologyEndpoint {

	/**
	 * Topology not found message.
	 */
	public static final String NO_TOPOLOGY_FOUND_MSG = "No topology found for the given application ID";

	private final KafkaStreamsRegistry kafkaStreamsRegistry;

	public TopologyEndpoint(KafkaStreamsRegistry kafkaStreamsRegistry) {
		this.kafkaStreamsRegistry = kafkaStreamsRegistry;
	}

	@ReadOperation
	public String topology() {
		final List<StreamsBuilderFactoryBean> streamsBuilderFactoryBeans = this.kafkaStreamsRegistry.streamsBuilderFactoryBeans();
		final StringBuilder topologyDescription = new StringBuilder();
		streamsBuilderFactoryBeans.stream()
				.forEach(streamsBuilderFactoryBean ->
						topologyDescription.append(streamsBuilderFactoryBean.getTopology().describe().toString()));
		return topologyDescription.toString();
	}

	@ReadOperation
	public String topology(@Selector String applicationId) {
		if (!StringUtils.isEmpty(applicationId)) {
			final StreamsBuilderFactoryBean streamsBuilderFactoryBean = this.kafkaStreamsRegistry.streamsBuilderFactoryBean(applicationId);
			if (streamsBuilderFactoryBean != null) {
				return streamsBuilderFactoryBean.getTopology().describe().toString();
			}
			else {
				return NO_TOPOLOGY_FOUND_MSG;
			}
		}
		return NO_TOPOLOGY_FOUND_MSG;
	}
}
