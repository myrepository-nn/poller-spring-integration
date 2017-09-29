# poller-spring-integration
CronTrigger is executed every 10 sec if message is found or received else for next executions PeriodicTrigger is executed every 2seconds
till the message is found.Again when message is found CronTrigger starts executing and the cycle goes on.

if random number is greater then 5 then message is received else null is send as message.
if(n>5) {
					msf= MessageBuilder.withPayload(n+"......"+new Timestamp(new Date().getTime())+"......").build();
				}
