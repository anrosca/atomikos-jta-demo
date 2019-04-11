package com.endava.jta.jtademo.service;

import java.time.LocalDate;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.endava.jta.jtademo.domain.Report;
import com.endava.jta.jtademo.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FooService {

    private final ReportRepository reportRepository;

    private final CamelContext camelContext;

    @Transactional
    public void foo() {
        final ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        producerTemplate.sendBody("oracleAqComponent:queue:BPSOPAQ_OWNER.Q_REPORTS?jmsMessageType=Text", "Welcome!!!JTA!!!");


        reportRepository.save(Report.builder()
                .channel("")
                .id(6669L)
                .code("BPS001")
                .destination("CCE")
                .participantId("0001")
                .participantName("qwqw")
                .dailyMonthly("D")
                .reportName("dsdsdsd")
                .reportDate(LocalDate.now())
                .schedule("TRM")
                .target("BULK")
                .channel("PORTAL")
                .build());
    }
}
