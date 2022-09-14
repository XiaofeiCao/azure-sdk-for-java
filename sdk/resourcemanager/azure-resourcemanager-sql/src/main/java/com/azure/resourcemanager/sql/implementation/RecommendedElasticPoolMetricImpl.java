// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.sql.implementation;

import com.azure.resourcemanager.resources.fluentcore.model.implementation.WrapperImpl;
import com.azure.resourcemanager.sql.fluent.models.ElasticPoolInner;
import com.azure.resourcemanager.sql.models.RecommendedElasticPoolMetric;
import com.azure.resourcemanager.sql.fluent.models.RecommendedElasticPoolMetricInner;
import java.time.OffsetDateTime;

/** Implementation for RecommendedElasticPoolMetric interface. */
class RecommendedElasticPoolMetricImpl extends WrapperImpl<ElasticPoolInner>
    implements RecommendedElasticPoolMetric {

    protected RecommendedElasticPoolMetricImpl(ElasticPoolInner innerObject) {
        super(innerObject);
    }

    @Override
    public OffsetDateTime dateTime() {
        return this.innerModel().dateTime();
    }

    @Override
    public double dtu() {
        return this.innerModel().dtu();
    }

    @Override
    public double sizeGB() {
        return this.innerModel().sizeGB();
    }
}
