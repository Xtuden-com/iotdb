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

package org.apache.iotdb.cluster.log.logtypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import org.apache.iotdb.cluster.common.TestUtils;
import org.apache.iotdb.cluster.exception.UnknownLogTypeException;
import org.apache.iotdb.cluster.log.Log;
import org.apache.iotdb.cluster.log.LogParser;
import org.apache.iotdb.cluster.rpc.thrift.Node;
import org.apache.iotdb.db.exception.query.QueryProcessException;
import org.apache.iotdb.db.qp.physical.crud.InsertPlan;
import org.apache.iotdb.db.qp.physical.sys.CreateTimeSeriesPlan;
import org.apache.iotdb.db.qp.physical.sys.SetStorageGroupPlan;
import org.apache.iotdb.tsfile.file.metadata.enums.CompressionType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.apache.iotdb.tsfile.read.common.Path;
import org.apache.iotdb.tsfile.write.schema.MeasurementSchema;
import org.junit.Test;

public class SerializeLogTest {

  @Test
  public void testPhysicalPlanLog() throws UnknownLogTypeException, QueryProcessException {
    PhysicalPlanLog log = new PhysicalPlanLog();
    log.setPreviousLogIndex(1);
    log.setPreviousLogTerm(1);
    log.setCurrLogIndex(2);
    log.setCurrLogTerm(2);
    InsertPlan plan = new InsertPlan();
    plan.setDeviceId("root.d1");
    plan.setMeasurements(new String[]{"s1", "s2", "s3"});
    plan.setInferType(true);
    plan.setTypes(new TSDataType[plan.getMeasurements().length]);
    plan.setValues(new Object[]{"0.1", "1", "\"dd\""});
    MeasurementSchema[] schemas = {TestUtils.getTestMeasurementSchema(1),
        TestUtils.getTestMeasurementSchema(2), TestUtils.getTestMeasurementSchema(3)};
    schemas[0].setType(TSDataType.DOUBLE);
    schemas[1].setType(TSDataType.INT32);
    schemas[2].setType(TSDataType.TEXT);
    plan.setSchemasAndTransferType(schemas);
    plan.setTime(1);
    log.setPlan(plan);

    ByteBuffer byteBuffer = log.serialize();
    Log logPrime = LogParser.getINSTANCE().parse(byteBuffer);
    assertEquals(log, logPrime);

    log = new PhysicalPlanLog(new SetStorageGroupPlan(new Path("root.sg1")));
    byteBuffer = log.serialize();
    logPrime = LogParser.getINSTANCE().parse(byteBuffer);
    assertEquals(log, logPrime);

    log = new PhysicalPlanLog(new CreateTimeSeriesPlan(new Path("root.applyMeta"
        + ".s1"), TSDataType.DOUBLE, TSEncoding.RLE, CompressionType.SNAPPY,
        new HashMap<String, String>() {{
          put("MAX_POINT_NUMBER", "100");
        }}, Collections.emptyMap(), Collections.emptyMap(), null));
    byteBuffer = log.serialize();
    logPrime = LogParser.getINSTANCE().parse(byteBuffer);
    assertEquals(log, logPrime);
  }

  @Test
  public void testAddNodeLog() throws UnknownLogTypeException {
    AddNodeLog log = new AddNodeLog();
    log.setPreviousLogIndex(1);
    log.setPreviousLogTerm(1);
    log.setCurrLogIndex(2);
    log.setCurrLogTerm(2);
    log.setNewNode(new Node("apache.iotdb.com", 1234, 1, 4321));
    ByteBuffer byteBuffer = log.serialize();
    Log logPrime = LogParser.getINSTANCE().parse(byteBuffer);
    assertEquals(log, logPrime);
  }

  @Test
  public void testCloseFileLog() throws UnknownLogTypeException {
    CloseFileLog log = new CloseFileLog("root.sg1", 0, true);
    log.setPreviousLogIndex(1);
    log.setPreviousLogTerm(1);
    log.setCurrLogIndex(2);
    log.setCurrLogTerm(2);
    ByteBuffer byteBuffer = log.serialize();
    CloseFileLog logPrime = (CloseFileLog) LogParser.getINSTANCE().parse(byteBuffer);
    assertTrue(logPrime.isSeq());
    assertEquals("root.sg1", logPrime.getStorageGroupName());
    assertEquals(log, logPrime);
  }

  @Test
  public void testRemoveNodeLog() throws UnknownLogTypeException {
    RemoveNodeLog log = new RemoveNodeLog();
    log.setPreviousLogIndex(1);
    log.setPreviousLogTerm(1);
    log.setCurrLogIndex(2);
    log.setCurrLogTerm(2);
    log.setRemovedNode(TestUtils.getNode(0));
    ByteBuffer byteBuffer = log.serialize();
    RemoveNodeLog logPrime = (RemoveNodeLog) LogParser.getINSTANCE().parse(byteBuffer);
    assertEquals(log, logPrime);
  }

  @Test
  public void testEmptyContentLog() throws UnknownLogTypeException {
    EmptyContentLog log = new EmptyContentLog();
    log.setPreviousLogIndex(1);
    log.setPreviousLogTerm(1);
    log.setCurrLogIndex(2);
    log.setCurrLogTerm(2);
    ByteBuffer byteBuffer = log.serialize();
    EmptyContentLog logPrime = (EmptyContentLog) LogParser.getINSTANCE().parse(byteBuffer);
    assertEquals(log, logPrime);
  }
}