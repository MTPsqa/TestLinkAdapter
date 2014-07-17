package eu.uqasar.testlink.adapter;

import java.util.Arrays;
import java.util.List;


public enum TLMetric {
	
	TEST_P("TEST_P"),
	TEST_F("TEST_F"),
	TEST_B("TEST_B"),
	TEST_N("TEST_N"),
	TEST_TOTAL("TEST_TOTAL"),
	BUGS_PLAN("BUGS_PLAN"),

	;

	private final String labelKey;

	private TLMetric(final String labelKey) {
		this.labelKey = labelKey;
	}

	public static List<TLMetric> getAllTLMetrics(){
		return Arrays.asList(values());
	}


}
