package de.felixroske.jfxsupport.util;

import net.jodah.concurrentunit.Waiter;

/**
 * Created by Krystian Kałużny on 06.07.2017.
 */
public class BaseJavaFxSupportTest {
	protected static long TIMEOUT = 500000;
	protected static Waiter waiter; //it have to be static because classes with @FxSpringBootApplication have to be static either
	protected static JavaFxApplicationContainer javaFxApplicationContainer;

	public void setUp() throws Exception {
		waiter = new Waiter();
		javaFxApplicationContainer = new JavaFxApplicationContainer();
	}

	public void tearDown() throws Exception {
		waiter = null;
		javaFxApplicationContainer = null;
	}
}
