// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.resources.fluentcore.dag;

import org.junit.jupiter.api.Test;

/**
 * Sync variant of {@link ExecutableWithCreatableTests}
 */
public class SyncExecutableWithCreatableTests {
    @Test
    public void testExecutableWithExecutableDependency() {
        BreadSliceImpl breadFetcher1 = new BreadSliceImplWrapper("BreadSlice1");
        BreadSliceImpl breadFetcher2 = new BreadSliceImplWrapper("BreadSlice2");
        breadFetcher1.withAnotherSliceFromStore(breadFetcher2).execute();
    }

    @Test
    public void testExecutableWithCreatableDependency() {
        BreadSliceImpl breadFetcher = new BreadSliceImplWrapper("BreadSlice");
        OrderImpl order = new OrderImpl("OrderForSlice", new OrderInner());
        breadFetcher.withNewOrder(order).execute();
    }

    @Test
    public void testCreatableWithExecutableDependency() {
        SandwichImpl sandwich = new SandwichImplWrapper("Sandwich", new SandwichInner());
        BreadSliceImpl breadFetcher = new BreadSliceImplWrapper("SliceForSandwich");
        OrderImpl order = new OrderImpl("OrderForSlice", new OrderInner());
        breadFetcher.withNewOrder(order);
        sandwich.withBreadSliceFromStore(breadFetcher).create();
    }

    private static class BreadSliceImplWrapper extends BreadSliceImpl {
        public BreadSliceImplWrapper(String name) {
            super(name);
        }

        @Override
        public IBreadSlice execute() {
            return (IBreadSlice) taskGroup().invoke();
        }
    }

    private static class SandwichImplWrapper extends SandwichImpl {
        protected SandwichImplWrapper(String name, SandwichInner innerObject) {
            super(name, innerObject);
        }

        @Override
        public ISandwich create() {
            return (ISandwich) taskGroup().invoke();
        }
    }
}
