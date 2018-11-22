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

import java.util.*;

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
        
//        terminalParamLoader paramLoader = new terminalParamLoader();
//        paramLoader.loadParam(args);
//        if (paramLoader.hasParamInput){
//            if (!paramLoader.siFileMode){
//                System.out.println("fragMean: " + paramLoader.fragMean + " fragStd: " + paramLoader.fragStd 
//                + " readLen: " + paramLoader.readLen + " clustD: " + paramLoader.clusteringDist);
//                System.out.println("Working directory: " + paramLoader.workDir);
//                
//                SignalReader myReader = new SignalReader(paramLoader.fragMean, paramLoader.fragStd, paramLoader.cutStd, 
//                        paramLoader.readLen, paramLoader.clusteringDist, paramLoader.minMapQ);
//                
//                myReader.doWork(paramLoader.bamFile, paramLoader.fastaIndexFile, paramLoader.chr, paramLoader.givenRegionS, paramLoader.givenRegionE, paramLoader.superitemOut, paramLoader.abnormalSignalOut); 
//
//                
//                SequenceDatabase sequenceDatabase = new SequenceDatabase(); 
//                int minSup = 50;
//                
//                System.out.println("Super-Item generation completed!!\n\nSV path: " + paramLoader.svOut);
//                
//                BufferedWriter svRegionWriter = new BufferedWriter(new FileWriter(paramLoader.svOut));
//
//
//                sequenceDatabase.loadSequencesFromFile(paramLoader.superitemOut, svRegionWriter);
//
//                ContiguousFSPM algoContiguousFSPM = new ContiguousFSPM(minSup, paramLoader.patternMaxSpan);
//                algoContiguousFSPM.setParams(myReader.chromNameMap, paramLoader.regionMaskFile);
//                algoContiguousFSPM.runAlgorithm(sequenceDatabase, paramLoader.frequentPatternOut, paramLoader.mergedPatternOut, paramLoader.susRegionWriter, svRegionWriter, paramLoader.fastaFile);
//                algoContiguousFSPM.printAlgoStatistics();
//            }
//            else{
//                SequenceDatabase sequenceDatabase = new SequenceDatabase(); 
//                int minSup = 50;
//
//                BufferedWriter svRegionWriter = new BufferedWriter(new FileWriter(paramLoader.svOut));
//
//
//                sequenceDatabase.loadSequencesFromFile(paramLoader.superitemOut, svRegionWriter);
//
//                ContiguousFSPM algoContiguousFSPM = new ContiguousFSPM(minSup, paramLoader.fragMean);
//                fileReader reader = new fileReader();                
//                algoContiguousFSPM.setParams(reader.getIdxToChrName(paramLoader.fastaIndexFile), paramLoader.regionMaskFile);
//                algoContiguousFSPM.runAlgorithm(sequenceDatabase, paramLoader.frequentPatternOut, paramLoader.mergedPatternOut, paramLoader.susRegionWriter, svRegionWriter, paramLoader.fastaFile);
//                algoContiguousFSPM.printAlgoStatistics();
//            } 
//        }

//            String workDir = "/Users/jiadonglin/SV_data/NA19240/";
//            String bamFile = workDir + "30X/HG00514.30X.bam";
            
            String workDir = "/Users/jiadonglin/SV_data/synthetics/svsimGenome/wgs/hetSim/het50X_new/allele0.2/";
            String bamFile = workDir + "simDEL.30X.AF20%.sorted.rg.bam";
//            
//            String superItemOut = workDir + "morphlingv3/optimizeCode/wgs.all.superitems.txt";
//            String svOutPath = workDir + "morphlingv3/optimizeCode/wgs.predict.SVs.2std.vcf";
//            String susComplexRegion = workDir + "morphlingv3/optimizeCode/CSVsRegion/susCSVs.region.vcf";
            
            String superItemOut = workDir + "wgs.all.superitems.txt";
            String svOutPath = workDir + "morph.vcf";           
            
            
//            String fastaIndexFile = "/Users/jiadonglin/SV_data/ref_genome/GRCh38_full_analysis_set_plus_decoy_hla.fa.fai";
//            String fastaFile = "/Users/jiadonglin/SV_data/ref_genome/GRCh38_full_analysis_set_plus_decoy_hla.fa";
            
            
            String fastaIndexFile = "/Users/jiadonglin/SV_data/ref_genome/human_g1k_v37.fasta.fai";
            String fastaFile = "/Users/jiadonglin/SV_data/ref_genome/human_g1k_v37.fasta";
//            
//            String excludableRegion = "/Users/jiadonglin/SV_data/ref_genome/grch38.exclude.bed";
            
            int fragMean = 500;
            int fragStd = 50;
            int readLen = 150;
            int minMapQ = 10;
            int cutStd = 3;
            int ArpClusterDist = fragMean;     
            int patternGrowthLimit = fragMean;
                       
            
//            SignalReader myReader = new SignalReader(fragMean, fragStd, cutStd, readLen, ArpClusterDist, minMapQ);
//            myReader.doWork(bamFile, fastaIndexFile, null, 0, 0, superItemOut, null);                        
//            
            SequenceDatabase sequenceDatabase = new SequenceDatabase(); 
            
            int minSup = 50;

            BufferedWriter svRegionWriter = new BufferedWriter(new FileWriter(svOutPath));
//            BufferedWriter susRegionWriter = new BufferedWriter(new FileWriter(susComplexRegion));

            sequenceDatabase.loadSequencesFromFile(superItemOut, svRegionWriter);

            ContiguousFSPM algoContiguousFSPM = new ContiguousFSPM(minSup, patternGrowthLimit);
            fileReader reader = new fileReader();                
            algoContiguousFSPM.setParams(reader.getIdxToChrName(fastaIndexFile), null);
            algoContiguousFSPM.runAlgorithm(sequenceDatabase, null, null, null, svRegionWriter, fastaFile);
            algoContiguousFSPM.printAlgoStatistics(); 
        }
}

  

