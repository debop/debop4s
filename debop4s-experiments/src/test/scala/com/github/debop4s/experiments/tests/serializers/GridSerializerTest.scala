//package com.github.debop4s.experiments.tests.serializers
//
//import com.github.debop4s.experiments.tests.AbstractExperimentTest
//import org.gridgain.grid.marshaller.optimized.GridOptimizedMarshaller
//
///**
// * GridSerializerTest
// * Created by debop on 2014. 3. 23.
// */
//class GridSerializerTest extends AbstractExperimentTest {
//
//    test("GridOptimizedMarshaller test") {
//        val marshaller = new GridOptimizedMarshaller()
//
//        val data = GridSampleClass("debop", 47, 176.6f)
//        val bytes = marshaller.marshal(data)
//        val converted = marshaller.unmarshal(bytes, classOf[GridSampleClass].getClassLoader).asInstanceOf[GridSampleClass]
//
//        assert(converted != null)
//        assert(converted == data)
//    }
//
//}
//
//case class GridSampleClass(name: String, age: Int, score: Float) {
//    def this() {
//        this(null, 0, 0f)
//    }
//}