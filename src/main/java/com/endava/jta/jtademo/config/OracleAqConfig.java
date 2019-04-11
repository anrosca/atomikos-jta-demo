package com.endava.jta.jtademo.config;

import java.sql.SQLException;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.sql.XADataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.ExplicitCamelContextNameStrategy;
import org.apache.camel.spi.CamelContextNameStrategy;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import static org.apache.camel.model.TransactedDefinition.PROPAGATION_REQUIRED;

import com.atomikos.jdbc.AtomikosDataSourceBean;

import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

@Configuration
public class OracleAqConfig extends CamelConfiguration {

    public static final String TRANSACTION_POLICY = "camelTransactionPolicy";

    @Value("${reporting-messaging.camel.tracer.enabled}")
    private boolean enableTracing;

    @Override
    protected void setupCamelContext(final CamelContext camelContext) throws Exception {
        super.setupCamelContext(camelContext);
        CamelContextNameStrategy contextNameStrategy = new ExplicitCamelContextNameStrategy("CAMEL_CONTEXT");
        camelContext.setNameStrategy(contextNameStrategy);
        camelContext.setTracing(enableTracing);
    }

    @Bean
    @ConfigurationProperties("reporting-aq.datasource")
    public DataSourceProperties reportingAqDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public XADataSource reportingAQDataSource(@Qualifier("reportingAqDataSourceProperties") DataSourceProperties dataSourceProperties) throws SQLException {
        PoolXADataSource poolDataSource = PoolDataSourceFactory.getPoolXADataSource();
        poolDataSource.setURL(dataSourceProperties.getUrl());
        poolDataSource.setUser(dataSourceProperties.getUsername());
        poolDataSource.setPassword(dataSourceProperties.getPassword());
        poolDataSource.setConnectionFactoryClassName("oracle.jdbc.replay.OracleXADataSourceImpl");
        return poolDataSource;
    }

    @Bean
    public AtomikosDataSourceBean aqAtomikosDataSourceBean(@Qualifier("reportingAQDataSource") XADataSource dataSource) {
        AtomikosDataSourceBean dataSourceBean = new AtomikosDataSourceBean();
        dataSourceBean.setXaDataSource(dataSource);
        dataSourceBean.setUniqueResourceName("ORACLE_AQ");
        return dataSourceBean;
    }

    @Primary
    @Bean
    public ConnectionFactory connectionFactory(@Qualifier("aqAtomikosDataSourceBean") AtomikosDataSourceBean dataSource) throws JMSException {
        return oracle.jms.AQjmsFactory.getQueueConnectionFactory(dataSource);
    }

    @Bean(name = TRANSACTION_POLICY)
    public SpringTransactionPolicy transactionPolicy(PlatformTransactionManager transactionManager) {
        SpringTransactionPolicy transactionPolicy = new SpringTransactionPolicy(transactionManager);
        transactionPolicy.setPropagationBehaviorName(PROPAGATION_REQUIRED);
        return transactionPolicy;
    }

    @Bean
    public JmsComponent oracleAqComponent(ConnectionFactory connectionFactory, PlatformTransactionManager transactionManager) {
        return JmsComponent.jmsComponentTransacted(connectionFactory, transactionManager);
    }
}
