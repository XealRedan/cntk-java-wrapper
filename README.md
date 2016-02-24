CNTK evaluation Java wrapper
==============

This project is a Java wrapper for the Microsoft CNTK evaluation library ("EvalDll.dll").
Its purpose is to give the ability to use trained neural network in a Java context.

How to use it
--------------
The minimal code to use the library is the following:

    // Initialize the paths
    IEvaluateModelManaged.initializeCNTK(path to CNTK binaries and libraries);
    IEvaluateModelManaged.addLibraryDirectory(path to CUDA binaries and libraries);
    
    IEvaluateModelManaged.setWorkingDirectory(directory containing your data);
    
    // Load the configuration
    evaluateModelManaged.init(
            evaluateModelManaged.readConfiguration(
                    new File(your .cntk configuration file)));
    
    // Build your inputs/outputs maps
    final Map<WString, List<Float>> inputs = new HashMap<>();
    final Map<WString, List<Float>> outputs = new HashMap<>();

    final List<Float> inputData = ...;

    inputs.put(new WString("features"), inputData);

    final List<Float> outputData = new ArrayList<>();
    for(int i = 0; i < 10; i++) {
        outputData.add(0.0f);
    }
    
    outputs.put(new WString("ol.z"), outputData);
    
    // Start the evaluation
    evaluateModelManaged.evaluate(inputs, outputs);

CNTK version
--------------

The present version of the Java wrapper has been tested using the CNTK CPU version published the 2016-02-08 on a Windows
7 x64 computer.

There is no garanty it will work with another version of the CNTK.

Current limitations
--------------

The current software relies on a native wrapper written in C.
The only version included here is compiled with MSVC 2013 for Windows x64 architecture, thus it cannot be used on other
systems.

Moreover, the wrapper is still in development and lots of bugs are expected.