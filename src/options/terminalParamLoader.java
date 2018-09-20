/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package options;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author jiadonglin
 */
public class terminalParamLoader {
    
    public int readLen;
    public int fragMean;
    public int fragStd;
    public int cutStd = 3;
    public int clusteringDist;
    public int minMapQ = 10;
    public String chr = null;
    public int givenRegionS;
    public int givenRegionE;
    public String workDir;
    public String bamFile;
    public String fastaFile;
    public String fastaIndexFile;
    public String regionMaskFile;
    public String superitemOut;
    public String svOut;
    public String indelOut;
    public String abnormalSignalOut;
    public String mergedPatternOut;
    public String frequentPatternOut;
    public BufferedWriter susRegionWriter;
    public boolean hasParamInput = true;
    public boolean siFileMode = false;
    public terminalParamLoader(){        
        
    }
    
    public void loadParam(String[] args) throws IOException{
        
        if (args.length == 0){
            printOptionsHelp();
            hasParamInput = false;
        }else{
            loadTerminalInputParams(args);
        }
    }
    private void printOptionsHelp(){
        System.out.println("============ Morphling help info ===============");
        System.out.println("\nVersion: V1.0\nAuthor: Jiadong Lin (Xi'an JiaoTong University)\nContact: jiadong324@gmail.com");
        System.out.println("\n================================================\n");
        StringBuilder sb = new StringBuilder();
        sb.append("**** Mode One: run with BAM, this will create Super-Item file ****\n");
        sb.append("bamFile=   given the path of your BAM file\n");
        sb.append("faFile=   given the path of your reference file\n");     
        sb.append("bamCfg=  given the bam configuration file\n");
        
        
//        sb.append("itemOut= output path of super-item file\n"); 
//        sb.append("svOut=   output path of discovered SVs\n");
        
        sb.append("cutStd=    given the cutoff in unit of the standard deviation (default=3)\n");
        sb.append("maxD=    given the maximum distance to cluster abnormal read pairs (default=readLen)\n");
        sb.append("minQ=    given the minimum mapping quality of read (default=10)\n");
        
        sb.append("chrom=   given a specific region, process whole chromosome if coordinates are not given. optional (e.g chr1:1000-2000)\n");         
        sb.append("freOut=  output path of frequent patterns (optional, default=False (True in work dir)\n");
        sb.append("sigOut=  output path of abnormal alignments (optional, default=Flase (True in work dir)\n");
        sb.append("patternOut=  output path of merged patterns (optional, default=False (Ture in work dir)\n");  
        sb.append("indelOut=    output path of INDELS based on reads pileup (optional, default=False (True in work dir))\n");
        sb.append("regionMask=    exclude regions in file during SV discovery (optional, default=None)\n");
        
        sb.append("\n**** Mode Two: only run with Super-Item file (only support WG now) ****\n");
        sb.append("itemOut= super-item out file path\n"); 
        sb.append("faFile=   given the path of your reference file\n");  
        sb.append("svOut=   output path of discovered SVs\n");
        sb.append("bamCfg=  given the bam configuration file\n");   
        
        System.out.println(sb.toString());
    }
    
    private void loadTerminalInputParams(String[] args) throws IOException{

        for (int i = 0; i < args.length; i++){                     
            String[] argTokens = args[i].split("=");
            if (argTokens[0].equals("bamCfg")){
                readBamConfigFile(argTokens[1]);                
            }            
            if (argTokens[0].equals("cutStd")){
                cutStd = Integer.parseInt(argTokens[1]);
            }
            if (argTokens[0].equals("maxD")){
                clusteringDist = Integer.parseInt(argTokens[1]);
            }
            if (argTokens[0].equals("minQ")){
                minMapQ = Integer.parseInt(argTokens[1]);
            }
            if (argTokens[0].equals("chrom")){
                String givenRegion = argTokens[1];
                if (givenRegion.length() == 1){
                    chr = givenRegion;
                }else{
                    chr = givenRegion.split(":")[0];
                    givenRegionS = Integer.parseInt(givenRegion.split(":")[1].split("-")[0]);
                    givenRegionE = Integer.parseInt(givenRegion.split(":")[1].split("-")[1]);
                }
            }
            if (argTokens[0].equals("bamFile")){
                bamFile = argTokens[1];
            }
            if (argTokens[0].equals("faFile")){
                fastaFile = argTokens[1];
                fastaIndexFile = fastaFile + ".fai";
            }
            if (argTokens[0].equals("itemOut")){
                superitemOut = argTokens[1];
            }
            if (argTokens[0].equals("sigOut") && argTokens[0].equals("True")){
                abnormalSignalOut = workDir + "wgs.abnormal.signals.txt";
            }
            if (argTokens[0].equals("patternOut") && argTokens[0].equals("True")){
                mergedPatternOut = workDir + "wgs.merged.patterns.txt";
            }
            if (argTokens[0].equals("svOut")){
                svOut = argTokens[1];
            }
            if (argTokens[0].equals("freOut") && argTokens[0].equals("True")){
                frequentPatternOut = workDir + "wgs.frequent.patterns.txt";
            }
            if (argTokens[0].equals("regionMask")){
                regionMaskFile = argTokens[1];
            }
            if (argTokens[0].equals("indelOut") && argTokens[0].equals("True")){
                indelOut = workDir + "wgs.indels.txt";
            }
        }        
        if (bamFile == null){
            siFileMode = true;
        }
    }
    private void readBamConfigFile(String cfgFile) throws IOException{
        FileInputStream fin = new FileInputStream(new File(cfgFile));
        BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
        String thisLine;
        
        while ((thisLine = myInput.readLine()) != null){
            String[] tokens = thisLine.split(":");            
            if (tokens[0].equals("mean")){
                fragMean = Integer.parseInt(tokens[1]);
            }
            if (tokens[0].equals("stdev")){
                fragStd = Integer.parseInt(tokens[1]);
            }
            if (tokens[0].equals("readlen")){
                readLen = Integer.parseInt(tokens[1]);
                clusteringDist = readLen;
            }
            if (tokens[0].equals("workDir")){
                workDir = tokens[1];
                superitemOut = workDir + "wgs.superitems.txt";
                svOut = workDir + "wgs.predict.SVs.vcf";
            }
        }
    }    
}
