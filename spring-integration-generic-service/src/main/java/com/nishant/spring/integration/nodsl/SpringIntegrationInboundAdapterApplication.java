package com.nishant.spring.integration.nodsl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.aop.CompoundTriggerAdvice;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.util.CompoundTrigger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import com.fasterxml.jackson.core.JsonProcessingException;
@SpringBootApplication
public class SpringIntegrationInboundAdapterApplication {

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(SpringIntegrationInboundAdapterApplication.class, args);
	}

	@Bean
	public CompoundTrigger compoundTrigger() {
		return  new CompoundTrigger(cronTrigger());
	}
	@Bean
	public CronTrigger cronTrigger() {
		return  new CronTrigger("0 * * * * ?");
	}
	@Bean
	public PeriodicTrigger periodicTrigger() {
		return  new PeriodicTrigger(4000);
	}
	@Bean
	public PollerMetadata  pollerMetadata () {
		PollerMetadata pm=  new PollerMetadata ();
		List<Advice> lst=new ArrayList<>();
		lst.add(compoundTriggerAdvice());
		pm.setAdviceChain(lst);
		pm.setTrigger(compoundTrigger());
		pm.setMaxMessagesPerPoll(1);
		return pm;
	}
	@Bean
	public CompoundTriggerAdvice compoundTriggerAdvice() {
		return  new CompoundTriggerAdvice(compoundTrigger(), periodicTrigger());
	}
	@Bean
	@InboundChannelAdapter(value = "outChannel", poller = @Poller("pollerMetadata"))
	public MessageSource<String> fileReadingMessageSource() {
		return new MessageSource<String>() {

			@Override
			public Message<String> receive() {
				System.out.println("/////"+new Timestamp(new Date().getTime()));
				Message<String> msf=null;
				if(new Date().getMinutes()==35) {
					msf= MessageBuilder.withPayload(new Timestamp(new Date().getTime())+"......").build();
				}
				return msf;
			}
		};
	}
	@Bean
	public MessageChannel outChannel() {
		return  new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel="outChannel")
	public MessageHandler displayChannelhandle() {
		return new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				System.out.println("outChannel="+message.getPayload()+"----whole message----"+message);

			}
		};

	}

}
