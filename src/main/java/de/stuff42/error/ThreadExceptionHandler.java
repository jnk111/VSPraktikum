/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is NON free software; you are not permitted to redistribute it
 * and/or modify it.
 *
 * This code is distributed WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Copyright (c) 2015 by stuff42. ALL RIGHTS RESERVED.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA or
 * visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * This file is available under the conditions stated above.
 *
 * Written by Gerriet Hinrichs of stuff42.
 */
package de.stuff42.error;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Default uncaught exception handler for threads.
 *
 * @author Gerriet Hinrichs <gerriet.hinrichs@web.de>
 */
public class ThreadExceptionHandler implements UncaughtExceptionHandler {

    /**
     * Creates a new {@link ThreadExceptionHandler} instance and registers it as
     * {@link UncaughtExceptionHandler} for the current thread.
     */
    public static void register() {
	Thread.currentThread().setUncaughtExceptionHandler(new ThreadExceptionHandler());
    }

    /**
     * Creates a new {@link ThreadExceptionHandler} instance and registers it as
     * {@link UncaughtExceptionHandler} for the given thread.
     * 
     * @param thread
     *            Thread to register the handler for.
     */
    public static void register(final Thread thread) {
	thread.setUncaughtExceptionHandler(new ThreadExceptionHandler());
    }

    /**
     * Creates a new {@link ThreadExceptionHandler} instance and registers it as
     * default {@link UncaughtExceptionHandler} for this application.
     */
    public static void registerDefault() {
	Thread.setDefaultUncaughtExceptionHandler(new ThreadExceptionHandler());
    }

    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
	System.err.println("---");
	System.err.println("Thread: " + t.getName() + " (#" + t.getId() + ")");
	System.err.print(ExceptionUtils.getExceptionInfo(e, "FATAL"));
	System.exit(-1);
    }

}
