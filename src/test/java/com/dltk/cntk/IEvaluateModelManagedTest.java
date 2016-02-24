package com.dltk.cntk;

/*
 * #%L
 * EvaluationJavaWrapper
 * %%
 * Copyright (C) 2016 DLTK
 * %%
 * Java wrapper for evaluation library of Microsoft CNTK
 * Copyright (C) 2016 Alexandre Lombard
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * #L%
 */


import com.sun.jna.*;
import org.junit.Test;

import java.io.File;
import java.util.*;

/**
 * Created by alombard on 15/02/2016.
 */
public class IEvaluateModelManagedTest {
    @Test
    public void testIEvaluateModelManagedFReadMap() {
        final WString[] keys = new WString[1];
        keys[0] = new WString("features");
        final int keysLength = keys.length;
        final int[] valuesLength = new int[1];
        valuesLength[0] = 10;

        final Pointer valuesLengthPtr = new Memory(valuesLength.length * Native.getNativeSize(Integer.TYPE));
        for(int i = 0; i < valuesLength.length; i++)
            valuesLengthPtr.setInt(i * Native.getNativeSize(Integer.TYPE), valuesLength[i]);

        final Pointer p  =
                IEvaluateModelManaged.EvaluationCWrapper.INSTANCE.CreateEmptyMapF(
                        keys, keysLength, valuesLengthPtr);

        IEvaluateModelManaged.EvaluationCWrapper.INSTANCE.ReadMapF(
                p,
                (key, values, vl) -> System.out.println(key + ": " + values.getInt(0)));
    }

    @Test
    public void testIEvaluateModelManagedF() {
        IEvaluateModelManaged.initializeCNTK("C:\\Users\\alombard.PC-SET-155\\Source\\Repos\\CNTK\\x64\\Debug");
        IEvaluateModelManaged.addLibraryDirectory("C:\\Program Files\\NVIDIA GPU Computing Toolkit\\CUDA\\v7.0\\bin");

        try(final IEvaluateModelManaged<Float> evaluateModelManaged = new IEvaluateModelManagedF()) {
//            evaluateModelManaged.init(getClass().getResource("Convolution.config").getPath());
//            evaluateModelManaged.loadModel(new WString(getClass().getResource("Convolution").getPath()));
//            evaluateModelManaged.init("D:\\CNTK\\CNTK-20160126-Windows-64bit-ACML5.3.1-CUDA7.0\\Examples\\Image\\MNIST\\Config\\02_Convolution.config");
            IEvaluateModelManaged.setWorkingDirectory("D:\\CNTK\\CNTK-2016-02-08-Windows-64bit-CPU-Only\\Examples\\Image\\MNIST\\Data");
            evaluateModelManaged.init(
                    evaluateModelManaged.readConfiguration(
                            new File("D:\\CNTK\\CNTK-2016-02-08-Windows-64bit-CPU-Only\\Examples\\Image\\MNIST\\Config\\01_OneHidden.cntk")));

            final Map<WString, List<Float>> inputs = new HashMap<>();
            final Map<WString, List<Float>> outputs = new HashMap<>();

            final String test = "0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	84	185	159	151	60	36	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	222	254	254	254	254	241	198	198	198	198	198	198	198	198	170	52	0	0	0	0	0	0	0	0	0	0	0	0	67	114	72	114	163	227	254	225	254	254	254	250	229	254	254	140	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	17	66	14	67	67	67	59	21	236	254	106	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	83	253	209	18	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	22	233	255	83	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	129	254	238	44	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	59	249	254	62	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	133	254	187	5	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	9	205	248	58	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	126	254	182	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	75	251	240	57	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	19	221	254	166	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	3	203	254	219	35	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	38	254	254	77	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	31	224	254	115	1	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	133	254	254	52	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	61	242	254	254	52	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	121	254	254	219	40	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	121	254	207	18	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0";
            final String[] testArr = test.split("\t");
            final List<Float> testList = new ArrayList<>();

            for(String s : testArr) {
                testList.add(Float.parseFloat(s));
            }

            inputs.put(new WString("features"), testList);

            final List<Float> outList = new ArrayList<>();
            for(int i = 0; i < 10; i++) {
                outList.add(0.0f);
            }
            outputs.put(new WString("ol.z"), outList);

            evaluateModelManaged.evaluate(inputs, outputs);

            final List<Float> res = outputs.get("test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
    public void testIEvaluateModelManagedD() {
        IEvaluateModelManaged.initializeCNTK("C:\\Users\\alombard.PC-SET-155\\Source\\Repos\\CNTK\\x64\\Debug");

        try(final IEvaluateModelManaged<Double> evaluateModelManaged = new IEvaluateModelManagedD()) {
            evaluateModelManaged.init(getClass().getResource("Convolution.config").getPath());
            evaluateModelManaged.loadModel(new WString(getClass().getResource("Convolution").getPath()));

            final Map<WString, List<Double>> inputs = new HashMap<>();
            final Map<WString, List<Double>> outputs = new HashMap<>();

            evaluateModelManaged.evaluate(inputs, outputs);
        } catch (Exception e) {
            //
        }
    }
}
