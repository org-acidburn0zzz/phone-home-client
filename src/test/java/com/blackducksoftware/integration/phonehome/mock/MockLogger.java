/*
 * Copyright (C) 2018 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.phonehome.mock;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.Marker;

public class MockLogger implements Logger {
    private final List<String> logLines;

    public MockLogger() {
        logLines = new ArrayList<>();
    }

    public List<String> getLogLines() {
        return logLines;
    }

    public boolean doLogsContainText(final String text) {
        for (final String logLine : logLines) {
            if (logLine.contains(text)) {
                return true;
            }
        }
        return false;
    }

    private void log(final String msg) {
        System.out.println(msg);
        logLines.add(msg);
    }

    @Override
    public String getName() {
        // Not needed
        return null;
    }

    @Override
    public boolean isTraceEnabled() {
        // Not needed
        return false;
    }

    @Override
    public void trace(final String msg) {
        log(msg);
    }

    @Override
    public void trace(final String format, final Object arg) {
        // Not needed
    }

    @Override
    public void trace(final String format, final Object arg1, final Object arg2) {
        // Not needed
    }

    @Override
    public void trace(final String format, final Object... arguments) {
        // Not needed
    }

    @Override
    public void trace(final String msg, final Throwable t) {
        // Not needed
    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        // Not needed
        return false;
    }

    @Override
    public void trace(final Marker marker, final String msg) {
        // Not needed
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg) {
        // Not needed
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
        // Not needed
    }

    @Override
    public void trace(final Marker marker, final String format, final Object... argArray) {
        // Not needed
    }

    @Override
    public void trace(final Marker marker, final String msg, final Throwable t) {
        // Not needed
    }

    @Override
    public boolean isDebugEnabled() {
        // Not needed
        return false;
    }

    @Override
    public void debug(final String msg) {
        log(msg);
    }

    @Override
    public void debug(final String format, final Object arg) {
        // Not needed
    }

    @Override
    public void debug(final String format, final Object arg1, final Object arg2) {
        // Not needed
    }

    @Override
    public void debug(final String format, final Object... arguments) {
        // Not needed
    }

    @Override
    public void debug(final String msg, final Throwable t) {
        // Not needed
    }

    @Override
    public boolean isDebugEnabled(final Marker marker) {
        // Not needed
        return false;
    }

    @Override
    public void debug(final Marker marker, final String msg) {
        // Not needed
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg) {
        // Not needed
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
        // Not needed
    }

    @Override
    public void debug(final Marker marker, final String format, final Object... arguments) {
        // Not needed
    }

    @Override
    public void debug(final Marker marker, final String msg, final Throwable t) {
        // Not needed
    }

    @Override
    public boolean isInfoEnabled() {
        // Not needed
        return false;
    }

    @Override
    public void info(final String msg) {
        log(msg);
    }

    @Override
    public void info(final String format, final Object arg) {
        // Not needed
    }

    @Override
    public void info(final String format, final Object arg1, final Object arg2) {
        // Not needed
    }

    @Override
    public void info(final String format, final Object... arguments) {
        // Not needed
    }

    @Override
    public void info(final String msg, final Throwable t) {
        // Not needed
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        // Not needed
        return false;
    }

    @Override
    public void info(final Marker marker, final String msg) {
        // Not needed
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg) {
        // Not needed
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
        // Not needed
    }

    @Override
    public void info(final Marker marker, final String format, final Object... arguments) {
        // Not needed
    }

    @Override
    public void info(final Marker marker, final String msg, final Throwable t) {
        // Not needed
    }

    @Override
    public boolean isWarnEnabled() {
        // Not needed
        return false;
    }

    @Override
    public void warn(final String msg) {
        log(msg);
    }

    @Override
    public void warn(final String format, final Object arg) {
        // Not needed
    }

    @Override
    public void warn(final String format, final Object... arguments) {
        // Not needed
    }

    @Override
    public void warn(final String format, final Object arg1, final Object arg2) {
        // Not needed
    }

    @Override
    public void warn(final String msg, final Throwable t) {
        // Not needed
    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        // Not needed
        return false;
    }

    @Override
    public void warn(final Marker marker, final String msg) {
        // Not needed
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg) {
        // Not needed
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
        // Not needed
    }

    @Override
    public void warn(final Marker marker, final String format, final Object... arguments) {
        // Not needed
    }

    @Override
    public void warn(final Marker marker, final String msg, final Throwable t) {
        // Not needed
    }

    @Override
    public boolean isErrorEnabled() {
        // Not needed
        return false;
    }

    @Override
    public void error(final String msg) {
        log(msg);
    }

    @Override
    public void error(final String format, final Object arg) {
        // Not needed
    }

    @Override
    public void error(final String format, final Object arg1, final Object arg2) {
        // Not needed
    }

    @Override
    public void error(final String format, final Object... arguments) {
        // Not needed
    }

    @Override
    public void error(final String msg, final Throwable t) {
        // Not needed
    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        // Not needed
        return false;
    }

    @Override
    public void error(final Marker marker, final String msg) {
        // Not needed
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg) {
        // Not needed
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
        // Not needed
    }

    @Override
    public void error(final Marker marker, final String format, final Object... arguments) {
        // Not needed
    }

    @Override
    public void error(final Marker marker, final String msg, final Throwable t) {
        // Not needed
    }

}
