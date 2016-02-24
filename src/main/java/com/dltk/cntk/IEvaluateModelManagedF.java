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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;

/**
 * Implementation of IEvaluateModel using Double
 */
public class IEvaluateModelManagedF extends IEvaluateModelManaged<Float> {

    public IEvaluateModelManagedF() {
        super("GetEvalF");
    }

    @Override
    public void init(@Nonnull String config) {
        EvaluationCWrapper.INSTANCE.IEvaluateModelF_Init(this.eval, config);
    }

    @Override
    public void loadModel(@Nonnull WString modelFileName) {
        EvaluationCWrapper.INSTANCE.IEvaluateModelF_LoadModel(this.eval, modelFileName);
    }

    @Override
    public void evaluate(Map<WString, List<Float>> inputs, Map<WString, List<Float>> outputs) {
        final WString[] inputKeys = new WString[inputs.keySet().size()];
        final List<Float> inputValues = new ArrayList<>();
        final int[] inputValuesLength = new int[inputKeys.length];

        final WString[] outputKeys = new WString[outputs.keySet().size()];
        final List<Float> outputValues = new ArrayList<>();
        final int[] outputValuesLength = new int[outputKeys.length];

        // Fill arrays with maps content in order to create native maps
        int idx = 0;
        for(WString k : inputs.keySet()) {
            final List<Float> v = inputs.get(k);

            inputKeys[idx] = k;
            inputValuesLength[idx] = v.size();
            inputValues.addAll(inputs.get(k));

            idx++;
        }

        idx = 0;
        for(WString k : outputs.keySet()) {
            final List<Float> v = outputs.get(k);

            outputKeys[idx] = k;
            outputValuesLength[idx] = v.size();
            outputValues.addAll(outputs.get(k));
        }

        // Allocate arrays
        final Pointer inputValuesPtr = new Memory(inputValues.size() * Native.getNativeSize(Float.TYPE));
        for(int i = 0; i < inputValues.size(); i++)
            inputValuesPtr.setFloat(i * Native.getNativeSize(Float.TYPE), inputValues.get(i));

        final Pointer inputValuesLengthPtr = new Memory(inputValuesLength.length * Native.getNativeSize(Integer.TYPE));
        for(int i = 0; i < inputValuesLength.length; i++)
            inputValuesLengthPtr.setInt(i * Native.getNativeSize(Integer.TYPE), inputValuesLength[i]);

        final Pointer outputValuesPtr = new Memory(outputValues.size() * Native.getNativeSize(Float.TYPE));
        for(int i = 0; i < outputValues.size(); i++)
            outputValuesPtr.setFloat(i * Native.getNativeSize(Float.TYPE), outputValues.get(i));

        final Pointer outputValuesLengthPtr = new Memory(outputValuesLength.length * Native.getNativeSize(Integer.TYPE));
        for(int i = 0; i < outputValuesLength.length; i++)
            outputValuesLengthPtr.setInt(i * Native.getNativeSize(Integer.TYPE), outputValuesLength[i]);

        // Allocate the native maps
        final Pointer inputMap =
                EvaluationCWrapper.INSTANCE.CreateMapF(inputKeys, inputKeys.length, inputValuesPtr, inputValuesLengthPtr);

        final Pointer outputMap =
                EvaluationCWrapper.INSTANCE.CreateMapF(outputKeys, outputKeys.length, outputValuesPtr, outputValuesLengthPtr);

        // Evaluate
        EvaluationCWrapper.INSTANCE.IEvaluateModelF_Evaluate(this.eval, inputMap, outputMap);

        // Read the result and store it in the output map
        EvaluationCWrapper.INSTANCE.ReadMapF(outputMap, (key, values, valuesLength) -> {
            final List<Float> l = new ArrayList<>();
            for(int i = 0; i < valuesLength; i++) {
                l.add(values.getFloat(i * Native.getNativeSize(Float.class)));
            }
            outputs.put(key, l);
        });

        // Free the native maps
        EvaluationCWrapper.INSTANCE.DisposeMapF(inputMap);
        EvaluationCWrapper.INSTANCE.DisposeMapF(outputMap);
    }

    @Override
    public List<Float> evaluate(@Nonnull Map<WString, List<Float>> inputs, @Nonnull WString outputKey, int outputSize) {
        final Map<WString, List<Float>> outputMap = new HashMap<>();
        final List<Float> outputs = new ArrayList<>();

        for(int i = 0; i < outputSize; i++) {
            outputs.add(0.0f);
        }

        outputMap.put(outputKey, outputs);

        this.evaluate(inputs, outputMap);

        return outputMap.get(outputKey);
    }

    @Override
    public void close() throws IOException {
        EvaluationCWrapper.INSTANCE.IEvaluateModelF_Destroy(this.eval);
    }
}
