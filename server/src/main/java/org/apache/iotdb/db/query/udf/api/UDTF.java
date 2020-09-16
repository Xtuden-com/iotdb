/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.db.query.udf.api;

import org.apache.iotdb.db.query.udf.api.access.Point;
import org.apache.iotdb.db.query.udf.api.access.PointIterator;
import org.apache.iotdb.db.query.udf.api.access.Row;
import org.apache.iotdb.db.query.udf.api.access.RowIterator;
import org.apache.iotdb.db.query.udf.api.collector.PointCollector;
import org.apache.iotdb.db.query.udf.api.customizer.config.UDTFConfigurations;
import org.apache.iotdb.db.query.udf.api.customizer.parameter.UDFParameters;

public abstract class UDTF implements UDF {

  protected PointCollector collector;

  public abstract void initializeUDF(UDFParameters parameters, UDTFConfigurations configurations);

  public void transformPoint(Point point) throws Exception {
  }

  public void transformPoints(PointIterator points) throws Exception {
  }

  public void transformRow(Row row) throws Exception {
  }

  public void transformRows(RowIterator rows) throws Exception {
  }

  public final void setCollector(PointCollector collector) {
    this.collector = collector;
  }

  public final PointCollector getCollector() {
    return collector;
  }
}