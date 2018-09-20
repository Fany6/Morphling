/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package morphreleasev1;

import contiguousfspm.ContiguousFSPM;
import dataStructures.SequenceDatabase;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import options.terminalParamLoader;
import superitemGenerator.SignalReader;
import superitemGenerator.fileReader;


/**
 *
 * @author jiadonglin
 */
public class MorphReleaseV1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        // TODO code application logic here
        
        terminalParamLoader paramLoader = new terminalParamLoader();
        paramLoader.loadParam(args);
        if (paramLoader.hasParamInput){
            if (!paramLoader.siFileMode){
                System.out.println("fragMean: " + paramLoader.fragMean + " fragStd: " + paramLoader.fragStd 
                + " readLen: " + paramLoader.readLen + " clustD: " + paramLoader.clusteringDist);
                System.out.println("Working directory: " + paramLoader.workDir);
                
                SignalReader myReader = new SignalReader(paramLoader.fragMean, paramLoader.fragStd, paramLoader.cutStd, 
                        paramLoader.readLen, paramLoader.clusteringDist, paramLoader.minMapQ);
                myReader.doWork(paramLoader.bamFile, paramLoader.fastaIndexFile, paramLoader.chr, paramLoader.givenRegionS, paramLoader.givenRegionE, paramLoader.superitemOut, paramLoader.abnormalSignalOut); 

                
                SequenceDatabase sequenceDatabase = new SequenceDatabase(); 
                int minSup = 50;

                BufferedWriter svRegionWriter = new BufferedWriter(new FileWriter(paramLoader.svOut));


                sequenceDatabase.loadSequencesFromFile(paramLoader.superitemOut, svRegionWriter);

                ContiguousFSPM algoContiguousFSPM = new ContiguousFSPM(minSup, paramLoader.fragMean);
                algoContiguousFSPM.setParams(myReader.chromNameMap, true, paramLoader.regionMaskFile);
                algoContiguousFSPM.runAlgorithm(sequenceDatabase, paramLoader.frequentPatternOut, paramLoader.mergedPatternOut, paramLoader.susRegionWriter, svRegionWriter, paramLoader.fastaFile);
                algoContiguousFSPM.printAlgoStatistics();
            }
            else{
                SequenceDatabase sequenceDatabase = new SequenceDatabase(); 
                int minSup = 50;

                BufferedWriter svRegionWriter = new BufferedWriter(new FileWriter(paramLoader.svOut));


                sequenceDatabase.loadSequencesFromFile(paramLoader.superitemOut, svRegionWriter);

                ContiguousFSPM algoContiguousFSPM = new ContiguousFSPM(minSup, paramLoader.fragMean);
                fileReader reader = new fileReader();                
                algoContiguousFSPM.setParams(reader.getIdxToChrName(paramLoader.fastaIndexFile), true, paramLoader.regionMaskFile);
                algoContiguousFSPM.runAlgorithm(sequenceDatabase, paramLoader.frequentPatternOut, paramLoader.mergedPatternOut, paramLoader.susRegionWriter, svRegionWriter, paramLoader.fastaFile);
                algoContiguousFSPM.printAlgoStatistics();
            }                                    
        }
    }
}
