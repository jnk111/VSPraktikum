/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is NON free software; you are not permitted to redistribute it
 * and/or modify it.
 *
 * This code is distributed WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Copyright (c) 2014-2015 by stuff42. ALL RIGHTS RESERVED.
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

/**
 * Utility class to work with exceptions.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public final class ExceptionUtils {

    /**
     * Contains filters for stack trace simplification.
     */
    private static String[] filters = {};

    /**
     * <p>
     * Generates an error message for the given exception.
     * </p>
     * <p>
     * Uses {@link ExceptionUtils#getSimplifiedTrace(Throwable)} to print the
     * stack trace.
     * </p>
     *
     * @param ex
     *            Exception to generate the error message from.
     * @param lvl
     *            String to flag the exception message with.
     * @return The generated exception message.
     * @see ExceptionUtils#getSimplifiedTrace(Throwable)
     */
    public static String getExceptionInfo(final Throwable ex, final String lvl) {

        final String mask = "[%s] %s - %s%n%n%s";
        String msg = ex.getMessage();
        if (msg == null || msg.isEmpty()) {
            msg = "<no message>";
        }
        String level = lvl;
        if (lvl == null || lvl.isEmpty()) {
            level = "UNKNOWN";
        }
        return String.format(mask, level, ex.getClass().getCanonicalName(), msg,
                ExceptionUtils.getSimplifiedTrace(ex));
    }

    /**
     * <p>
     * Generates the stack trace string for the given exception.
     * </p>
     * <p>
     * This trace skips all entries from library functions.
     * </p>
     *
     * @param ex
     *            The exception to generate the stack trace string from.
     * @return The generated stack trace string.
     */
    @SuppressWarnings({ "boxing", "null" })
    public static String getSimplifiedTrace(final Throwable ex) {
        // load stack trace
        final StringBuilder trace = new StringBuilder();
        final StackTraceElement[] stackTrace = ex.getStackTrace();
        int skipped = 0;
        StackTraceElement prev = null;
        for (int i = 0; i < stackTrace.length; i++) {
            final String traceElement = "#%d: %s => %s (%s Line %d)%n";
            final StackTraceElement elem = stackTrace[i];
            boolean skip = true;
            final String className = elem.getClassName();
            // check if we should show the current element
            for (final String filter : ExceptionUtils.filters) {
                if (className.startsWith(filter)) {
                    skip = false;
                    break;
                }
            }
            if (skip) {
                skipped++;
            } else {
                if (skipped > 0) {
                    if (skipped > 1) {
                        trace.append(String.format("[...] (%d)%n", skipped - 1));
                    }
                    skipped = 0;
                    // print previous element
                    trace.append(String.format(traceElement, i - 1, prev.getClassName(),
                            prev.getMethodName(), prev.getFileName(), prev.getLineNumber()));
                }
                // print current element
                trace.append(String.format(traceElement, i, elem.getClassName(),
                        elem.getMethodName(), elem.getFileName(), elem.getLineNumber()));
            }
            prev = elem;
        }
        return trace.toString();
    }

    /**
     * Sets package name filters for stack trace simplification.
     *
     * @param filters
     *            Package names used for filtering. All sub-packages of the
     *            given packages will be included as well.
     */
    public static void setFilters(final String[] filters) {
        // add ending dot (if not present) to only filter on exact packages
        for (int i = 0; i < filters.length; i++) {
            if (!filters[i].endsWith(".")) {
                filters[i] += ".";
            }
        }
        // set filters
        ExceptionUtils.filters = filters;
    }

    /**
     * Hide the default constructor since this class only contains static
     * methods.
     */
    private ExceptionUtils() {
        // hide the default constructor
    }
}
