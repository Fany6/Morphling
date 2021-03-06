/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superitemGenerator;

import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author jiadonglin
 */
public class fileReader {

    
    public fileReader(){            
    }
    
    public SamReader openBamFile(String bamFilePath, ValidationStringency stringency, boolean includeFileSource) throws IOException{
        SamReaderFactory samReaderFactory = SamReaderFactory.makeDefault().validationStringency(stringency).enable(SamReaderFactory.Option.DONT_MEMORY_MAP_INDEX);
        if(includeFileSource){
            samReaderFactory.enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS);            
        }
//        samReaderFactory.samRecordFactory(DefaultSAMRecordFactory.getInstance());
        final SamReader samReader = samReaderFactory.open(new File(bamFilePath));
        return samReader;
    }
    
    public void readFaIdxFile(String faIdxFile, String[] chromNameMap, int[] chromLengthMap) throws IOException{
        FileInputStream fin = new FileInputStream(new File(faIdxFile));
        BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
        String thisLine;
        int chrIdx = 0;
        while ((thisLine = myInput.readLine()) != null){
            String[] tokens = thisLine.split("\t");
            String refSeq = tokens[0];
            // escape "M", "MT", "chrM"
            if (refSeq.contains("M") || refSeq.contains("_")){
                continue;
            }
                                
            chromLengthMap[chrIdx] = Integer.parseInt(tokens[1]);
            chromNameMap[chrIdx] = refSeq;            
            chrIdx += 1;
            
            // Read until Y chromosome, ignore other contigs.
            if (refSeq.equals("Y") || refSeq.equals("chrY")){
                break;
            }
        }
    }
    
    public String[] getIdxToChrName(String faIdxFile) throws IOException{
        String[] chromNameMap = new String[24];
        FileInputStream fin = new FileInputStream(new File(faIdxFile));
        BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
        String thisLine;
        int chrIdx = 0;
        while ((thisLine = myInput.readLine()) != null){
            String[] tokens = thisLine.split("\t");
            String refSeq = tokens[0];
            // escape "M", "MT", "chrM"
            if (refSeq.contains("M") || refSeq.contains("_")){
                continue;
            }
                                            
            chromNameMap[chrIdx] = refSeq;            
            chrIdx += 1;
            
            // Read until Y chromosome, ignore other contigs.
            if (refSeq.equals("Y") || refSeq.equals("chrY")){
                break;
            }
        }
        return chromNameMap;
    }
    
    public ReferenceSequenceFile readFastaFile(String faFile) throws IOException{                        
        File fa = new File(faFile);
        return ReferenceSequenceFileFactory.getReferenceSequenceFile(fa);        
    }
    
}
