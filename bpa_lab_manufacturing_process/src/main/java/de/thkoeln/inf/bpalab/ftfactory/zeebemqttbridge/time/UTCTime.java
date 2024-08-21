package de.thkoeln.inf.bpalab.ftfactory.zeebemqttbridge.time;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UTCTime {
/*
 *  Helper class for FactoryMain timestamp format
 *  "YYYY-MM-DDThh:mm:ss.fffZ"
 *  
 */
	
	public static String getUTCTimeNowPlus1Day() {
		ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.ofOffset("UTC", ZoneOffset.UTC));
		String ts = DateTimeFormatter.ISO_INSTANT.format(zonedDateTime.plusDays(1));
		int len = ts.indexOf('.') + 4;
		return ts.substring(0, len) + 'Z';
	}

}
