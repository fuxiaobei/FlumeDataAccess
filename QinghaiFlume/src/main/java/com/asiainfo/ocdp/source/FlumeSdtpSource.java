package com.asiainfo.ocdp.source;

import com.asiainfo.ocdp.common.Constants;
import com.asiainfo.ocdp.socket.SocketServer;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Flume自定义source类.
 */
public class FlumeSdtpSource extends AbstractSource implements Configurable, PollableSource {
	private final static Logger logger = Logger.getLogger(FlumeSdtpSource.class);
	public static LinkedBlockingQueue<byte[]> msgQueue = new LinkedBlockingQueue<byte[]>(Integer.MAX_VALUE);
	private int analysisNum = 100;
	private String socketPort;
	private static int flag = 0;

	public void configure(Context context) {
		logger.debug("context.toString(): " + context.toString());
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(Constants.PROPERTIE_FILENAME);
		Properties props = new Properties();
		try {
			props.load(inputStream);
			analysisNum = context.getInteger("maxworks",
					Integer.parseInt(props.getProperty(Constants.ANALYSIS_THREAD_NUM)));

			socketPort = context.getString("port", props.getProperty(Constants.SOCKETSERVER_PORT));
			logger.info("socketPort =" + socketPort);
			logger.info("load config [" + Constants.PROPERTIE_FILENAME + "]");

		} catch (IOException e) {
			logger.error("could not load config", e);
		}
	}

	@Override
	public void start() {
		String[] ports = socketPort.split(" ");

		for (String port : ports) {
			logger.info("port is " + port);
			new Thread(new SocketServer(port)).start();
		}
		logger.info("server socket,analysis task start...");
	}

	@Override
	public void stop() {
		super.stop();
	}

	public Status process() {
		try {
			flag++;
			byte[] msg = msgQueue.take();
			long startTime = System.currentTimeMillis();
			String message = new String(msg);
			String[] messages = message.split(Constants.MESSAGE_SEPARATOR);
			logger.debug("Original message context:" + Arrays.asList(messages));

			for (String signalling : messages) {
				Map<String, String> headers = new HashMap();
				// 将imsi设置为header的key
				String imsi = signalling.trim().split(Constants.HEADERS_KEY_SEPARATOR)[9];
				try {
					headers.put(Constants.HEADERS_KEY, imsi);

				} catch (StringIndexOutOfBoundsException e) {
					logger.error("The message format is invalid. <" + signalling + ">");
					continue;
				}
				// 去除imsi为0的无效信令
				if (!imsi.equals("000000000000000")) {
					Event e = EventBuilder.withBody(signalling.getBytes(), headers);
					getChannelProcessor().processEvent(e);
				}
			}

			long endTime = System.currentTimeMillis(); // 获取结束时间

			return Status.READY;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return Status.BACKOFF;
		}
	}
}