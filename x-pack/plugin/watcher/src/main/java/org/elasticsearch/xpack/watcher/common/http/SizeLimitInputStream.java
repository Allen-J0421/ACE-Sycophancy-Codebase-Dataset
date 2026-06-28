/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */
package org.elasticsearch.xpack.watcher.common.http;

import org.elasticsearch.common.unit.ByteSizeValue;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * An inputstream throwing an exception when a preconfigured number of bytes is reached
 * This inputstream exists to prevent reading streaming or very big requests
 *
 * This implementation does not support mark/reset to prevent complex byte counting recalculations
 */
final class SizeLimitInputStream extends FilterInputStream {

    private final long maxByteSize;
    private long bytesRead;

    /**
     * Creates a new input stream, that throws an exception when a certain number of bytes is read
     * @param maxByteSize The maximum data to read, before throwing an exception
     * @param in          The underlying inputstream containing the data
     */
    SizeLimitInputStream(ByteSizeValue maxByteSize, InputStream in) {
        super(Objects.requireNonNull(in, "in"));
        this.maxByteSize = Objects.requireNonNull(maxByteSize, "maxByteSize").getBytes();
    }

    @Override
    public int read() throws IOException {
        int nextByte = super.read();
        if (nextByte != -1) {
            recordBytesRead(1);
        }
        return nextByte;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = super.read(b, off, len);
        if (read > 0) {
            recordBytesRead(read);
        }
        return read;
    }

    @Override
    public synchronized void mark(int readlimit) {
        throw new UnsupportedOperationException("mark not supported");
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("reset not supported");
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    private void recordBytesRead(int read) throws IOException {
        bytesRead += read;
        if (bytesRead > maxByteSize) {
            throw new IOException("Maximum limit of [" + maxByteSize + "] bytes reached");
        }
    }
}
