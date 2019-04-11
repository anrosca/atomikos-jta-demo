package com.endava.jta.jtademo.config;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.endava.jta.jtademo.config.jta.AtomikosJtaPlatform;

@Configuration
@EnableTransactionManagement
public class TransactionConfiguration {

    @Bean
    public UserTransaction userTransaction() throws SystemException {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        // userTransactionImp.setTransactionTimeout(50_000);
        return userTransactionImp;
    }

    @Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
    public UserTransactionManager atomikosTransactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        return userTransactionManager;
    }

    @Bean
    public AtomikosJtaPlatform atomikosJtaPlatform(UserTransactionManager userTransactionManager, UserTransaction userTransaction) {
        AtomikosJtaPlatform.setTransaction(userTransactionManager);
        AtomikosJtaPlatform.setTransactionManager(userTransactionManager);
        return new AtomikosJtaPlatform();
    }

    @Bean
    public PlatformTransactionManager transactionManager(UserTransaction userTransaction, TransactionManager atomikosTransactionManager) {
        return new JtaTransactionManager(userTransaction, atomikosTransactionManager);
    }
}
