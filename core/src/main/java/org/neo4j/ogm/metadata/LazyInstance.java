/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with
 * separate copyright notices and license terms. Your use of the source
 * code for these subcomponents is subject to the terms and
 *  conditions of the subcomponent's license, as noted in the LICENSE file.
 */

package org.neo4j.ogm.metadata;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.function.Supplier;

/**
 * @author Frantisek Hartman
 */
class LazyInstance<T> {

    private Supplier<T> supplier;
    private AtomicMarkableReference<T> ref;

    public LazyInstance(Supplier<T> supplier) {
        this.supplier = supplier;
        // mark value is used to indicate if the instance is initialized or not.
        this.ref = new AtomicMarkableReference<>(null, false);
    }

    public T get() {
        if(!this.ref.isMarked()) {
            this.ref.compareAndSet(null, this.supplier.get(), false, true);
        }
        return this.ref.getReference();
    }

    public boolean exists() {
        // need to actually execute get() here because this might be a first call to this LazyInstance
        return get() != null;
    }

}
