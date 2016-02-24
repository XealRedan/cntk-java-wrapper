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
import com.sun.jna.ptr.PointerByReference;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for EvaluateModel
 */
public abstract class IEvaluateModelManaged<T> implements AutoCloseable {

    public interface EvaluationCWrapper extends Library {
        interface MapEntryReadF extends Callback {
            void invoke(WString key, Pointer values, int valuesLength);
        }

        interface MapEntryReadD extends Callback {
            void invoke(WString key, Pointer values, int valuesLength);
        }

        EvaluationCWrapper INSTANCE = (EvaluationCWrapper) Native.loadLibrary("EvaluationCWrapper-1.0.dll", EvaluationCWrapper.class);

        void InitializeCNTK(WString cntkPath);
        void AddLibraryDirectory(WString path);
        void SetWorkingDirectory(String path);

        void GetEvalF(PointerByReference out);
        void GetEvalD(PointerByReference out);

        void IEvaluateModelF_Destroy(Pointer p);
        void IEvaluateModelD_Destroy(Pointer p);
        void IEvaluateModelF_Init(Pointer p, String config);
        void IEvaluateModelD_Init(Pointer p, String config);
        void IEvaluateModelF_LoadModel(Pointer p, WString modelFileName);
        void IEvaluateModelD_LoadModel(Pointer p, WString modelFileName);
        void IEvaluateModelF_StartEvaluateMinibatchLoop(Pointer p, WString outputNodeName);
        void IEvaluateModelD_StartEvaluateMinibatchLoop(Pointer p, WString outputNodeName);
        void IEvaluateModelF_Evaluate(Pointer p, Pointer inputs, Pointer outputs);
        void IEvaluateModelD_Evaluate(Pointer p, Pointer inputs, Pointer outputs);
        void IEvaluateModelF_ResetState(Pointer p);
        void IEvaluateModelD_ResetState(Pointer p);

        // Utility functions
        Pointer CreateEmptyMapF(WString[] keys, int keysLength, Pointer valuesLength);
        Pointer CreateMapF(WString[] keys, int keysLength, Pointer values, Pointer valuesLength);
        Pointer CreateEmptyMapD(WString[] keys, int keysLength, Pointer valuesLength);
        Pointer CreateMapD(WString[] keys, int keysLength, Pointer values, Pointer valuesLength);
        void ReadMapF(Pointer map, MapEntryReadF mapEntryReadF);
        void ReadMapD(Pointer map, MapEntryReadD mapEntryReadD);
        void DisposeMapF(Pointer map);
        void DisposeMapD(Pointer map);
    }

    protected Pointer eval = Pointer.NULL;

    public static void initializeCNTK(String cntkPath) {
        EvaluationCWrapper.INSTANCE.InitializeCNTK(new WString(cntkPath));
    }

    public static void initializeCNTK(File cntkDirectory) {
        initializeCNTK(cntkDirectory.getAbsolutePath());
    }

    public static void addLibraryDirectory(String path) {
        EvaluationCWrapper.INSTANCE.AddLibraryDirectory(new WString(path));
    }

    public static void addLibraryDirectory(File directory) {
        addLibraryDirectory(directory.getAbsolutePath());
    }

    public static void setWorkingDirectory(String path) {
        EvaluationCWrapper.INSTANCE.SetWorkingDirectory(path);
    }

    public static void setWorkingDirectory(File directory) {
        setWorkingDirectory(directory.getAbsolutePath());
    }

    public static String readConfiguration(File configFile) throws IOException {
        final StringBuilder builder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line;
            while((line = br.readLine()) != null) {
                builder.append(line + "\n");
            }
        }

        return builder.toString();
    }

    /**
     * Initializes a new instance of the IEvaluateModelManaged class.
     * @param funcName Factory function name for retrieving the native model from the dll.
     */
    public IEvaluateModelManaged(@Nonnull String funcName) {
        final PointerByReference pbr = new PointerByReference();
        switch(funcName) {
            case "GetEvalF":
                EvaluationCWrapper.INSTANCE.GetEvalF(pbr);
                break;
            case "GetEvalD":
                EvaluationCWrapper.INSTANCE.GetEvalD(pbr);
                break;
            default:
                throw new IllegalArgumentException();
        }
        this.eval = pbr.getValue();
    }

    /**
     * Initializes the model evaluation library with a CNTK configuration
     * @param config Model configuration entries
     */
    public abstract void init(@Nonnull String config);

    /**
     * Initializes the model evaluation library with a CNTK configuration
     * @param configurationFile The configuration file
     * @throws IOException Thrown if the file is not found or cannot be read
     */
    public void init(@Nonnull File configurationFile) throws IOException {
        init(IEvaluateModelManaged.readConfiguration(configurationFile));
    }

    /**
     * Loads a model file
     * @param modelFileName The model file name to load
     */
    public void loadModel(@Nonnull String modelFileName) {
        loadModel(new WString(modelFileName));
    }

    /**
     * Loads a model file
     * @param modelFileName The model file name to load
     */
    public abstract void loadModel(@Nonnull WString modelFileName);

    /**
     * Evaluates the model against input data and retrieves the output layer data
     * @param inputs
     * @param outputs
     */
    public abstract void evaluate(@Nonnull Map<WString, List<T>> inputs, @Nonnull Map<WString, List<T>> outputs);

    /**
     * Evaluates the model against input data and retrieves the output layer data
     * @param inputs
     * @param outputKey
     * @param outputSize
     * @return Results for specified layer
     */
    public abstract List<T> evaluate(@Nonnull Map<WString, List<T>> inputs, @Nonnull WString outputKey, int outputSize);

}
