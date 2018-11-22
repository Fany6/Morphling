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
import java.util.List;
import java.util.ArrayList;

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
    public int patternMaxSpan;
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
    
    public List<String> excludableRegion;
    
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
        
        
        sb.append("cutStd=    given the cutoff in unit of the standard deviation (default=3)\n");
        sb.append("maxD=    given the maximum distance to cluster abnormal read pairs (default=readLen)\n");
        sb.append("minQ=    given the minimum mapping quality of read (default=10)\n");
        sb.append("pMax=    a parameter control the max distance pattern can growth (default=2)\n");        
        sb.append("chrom=   given a specific region, process whole chromosome if coordinates are not given. optional (e.g chr1:1000-2000)\n");         
        
          
        sb.append("freOut=  output path of frequent patterns (optional, default=False)\n");
        sb.append("sigOut=  output path of abnormal alignments (optional, default=Flase)\n");
        sb.append("patternOut=  output path of merged patterns (optional, default=False)\n");  
        sb.append("indelOut=    output path of INDELS based on reads pileup (optional, default=False)\n");
        sb.append("regionMask=   regions to exclude during SV discovery (optional)\n");
        
        sb.append("\n**** Mode Two: only run with Super-Item file (only support WG now) ****\n");
//        sb.append("itemOut= super-item out file path\n"); 
        sb.append("faFile=   given the path of your reference file\n");  
        sb.append("svOut=   output path of discovered SVs\n");
        sb.append("bamCfg=  given the bam configuration file\n");   
        sb.append("pMax=    a parameter control the max distance pattern can growth (default=3)\n");
        
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
            
            if (argTokens[0].equals("pMax")){
                patternMaxSpan = fragMean + Integer.parseInt(argTokens[1]) * fragStd;
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
                abnormalSignalOut = "wgs.abnormal.signals.txt";
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
        loadExcluableRegion();
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
                superitemOut = workDir + "/wgs.all.superitems.txt";
                svOut = workDir + "/morph.vcf";
            }
        }
    }   
    
    private void loadExcluableRegion() throws IOException{
        String cenTelRegionFile = "/Users/jiadonglin/SV_data/ref_genome/centromeres.bed";
        String gapRegionFile = "/Users/jiadonglin/SV_data/ref_genome/hg38.gap.bed";
        String lowMapQRegionFile = "/Users/jiadonglin/SV_data/ref_genome/hg38.RegionsExcludable.bed";
        
//        String cenTelRegionFile = workDir + "centromeres.bed";
//        String gapRegionFile = workDir + "hg38.gap.bed";
//        String lowMapQRegionFile = workDir + "hg38.RegionsExcludable.bed";
        
        excludableRegion = new ArrayList<>(3);
        excludableRegion.add(cenTelRegionFile);
        excludableRegion.add(gapRegionFile);
        excludableRegion.add(lowMapQRegionFile);
    }
}
