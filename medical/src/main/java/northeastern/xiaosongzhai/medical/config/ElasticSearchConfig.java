package northeastern.xiaosongzhai.medical.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/4/11 11:04
 * @Description: ElasticSearch configuration
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "northeastern.xiaosongzhai.medical.repository")
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("192.168.1.234:9200")
                .build();
    }
}
