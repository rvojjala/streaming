package kafka;

import java.util.Collections;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

public class ErrorAnalyzer {

	public static void main(String[] args) throws Exception {
		final KafkaConsumer<String, String> consumer = Common.createStringConsumer("G2");
		consumer.subscribe(Collections.singletonList("trade_gen_service_errors"));
		final Thread threadMain = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("shutting down consumer...");
				consumer.wakeup();
				try {
					threadMain.join();
				} catch (Exception e) {e.printStackTrace();}
			}
		});
		
		try {
			System.out.println("waiting for messages...");
			long cnt = 0;
			while (true) {
				ConsumerRecords<String, String> msgs = consumer.poll(100);
				for (ConsumerRecord<String, String> msg : msgs) {
					cnt++;
					System.out.println(msg.topic() + "(" + cnt + "): " + msg.value().substring(msg.value().indexOf("B")));
				}
			}
		} catch (WakeupException w) {	
	    }  finally {
			consumer.close();
			System.out.println("consumer shutdown complete");
		}
	}
}
