package com.endava.jta.jtademo.config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.XADataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.endava.jta.jtademo.config.jta.AtomikosJtaPlatform;

import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.endava.jta.jtademo.repository")
public class SafeStoreDBConfig {

    public static final String PERSISTENCE_UNIT = "CORE";

    @Primary
    @Bean
    @ConfigurationProperties("safestore.datasource")
    public DataSourceProperties safeStoreDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public XADataSource safeStoreDataSource(@Qualifier("safeStoreDataSourceProperties") DataSourceProperties properties) throws SQLException {
        PoolXADataSource poolDataSource = PoolDataSourceFactory.getPoolXADataSource();
        poolDataSource.setURL(properties.getUrl());
        poolDataSource.setUser(properties.getUsername());
        poolDataSource.setPassword(properties.getPassword());
        poolDataSource.setConnectionFactoryClassName("oracle.jdbc.replay.OracleXADataSourceImpl");
        return poolDataSource;
    }

    @Bean
    public AtomikosDataSourceBean safeStoreDbAtomikosDataSourceBean(@Qualifier("safeStoreDataSource") XADataSource dataSource) {
        AtomikosDataSourceBean dataSourceBean = new AtomikosDataSourceBean();
        dataSourceBean.setXaDataSource(dataSource);
        dataSourceBean.setUniqueResourceName("SAFESTORE_DB");
        return dataSourceBean;
    }

    @DependsOn({"transactionManager", "atomikosJtaPlatform"})
    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("safeStoreDbAtomikosDataSourceBean") AtomikosDataSourceBean dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setJtaDataSource(dataSource);
        factoryBean.setPackagesToScan("com.endava.jta.jtademo.domain");
        factoryBean.setPersistenceUnitName(PERSISTENCE_UNIT);
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.setJpaPropertyMap(makeJpaProperties());
        factoryBean.afterPropertiesSet();
        return factoryBean;
    }

    private Map<String, Object> makeJpaProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        properties.put("javax.persistence.transactionType", "JTA");
        return properties;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform("org.hibernate.dialect.Oracle12cDialect");
        return adapter;
    }
}