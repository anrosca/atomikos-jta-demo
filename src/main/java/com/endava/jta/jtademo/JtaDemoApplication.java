package com.endava.jta.jtademo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.endava.jta.jtademo.service.FooService;

@SpringBootApplication
public class JtaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JtaDemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(FooService fooService) {
        return args -> {
            fooService.foo();
        };
    }

    //-Doracle.jdbc.autoCommitSpecCompliant=false

    /*grant execute on dbms_xa to BPSREP_USER ;
    grant select on pending_trans$ to BPSREP_USER;
    grant select on dba_2pc_pending to BPSREP_USER;
    grant select on dba_pending_transactions to BPSREP_USER;

    grant execute on dbms_xa to BPSOPAQ_OWNER ;
    grant select on pending_trans$ to BPSOPAQ_OWNER;
    grant select on dba_2pc_pending to BPSOPAQ_OWNER;
    grant select on dba_pending_transactions to BPSOPAQ_OWNER;*/
}
