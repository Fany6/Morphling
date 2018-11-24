# Morphling
Morphling is a model-free ultra fast genome structural variants detection tool. It also provides a machine learning based method to classify SVs based on their mutational signature sequential features as well as providing scoring method for complex SVs.

# Install and run

Morphling requires Java JDK (>=v1.6) and Python (2.7) to run. 

#### Dependency
* htsjdk(https://github.com/samtools/htsjdk): A Java API for high-throughput sequencing data (HTS) formats.
* Numpy: used in Python script for BAM parameter estimation.

#### Usage
```sh
$ git clone https://github.com/jiadong324/Morphling.git
```
To run the Morphling, first run bamConfig.py to get BAM configuration file. To run it, you have to specify how much standard deviation (-X) away are considered as discordant insert size and number of read-pairs (-N) you would like to use for the estimation. You will get bam.cfg file contains the read length, estimated library average insert size and standard deviation.
```sh
$ samtools view your.bam | python bamConfig.py -X 3 -N 30000
```
To get help info
```
$ java /your/path/to/Morphling/dist/MorphReleaseV1.jar
```

Run mode one example: run with BAM file, and your can either output all SuperItems to file or keep it in the memory. It is suggested to keep them in file, since you won't need to go through the BAM file agian for next run with different parameters.
```
$ java -jar /path/to/Morphling/dist/MorphRealeaseV1.jar bamFile=file.bam faFile=file.fa bamCfg=bam.cfg
```
Run mode two example: run on SuperItem files directly without BAM file

'''
$ java -jar /path/to/Morphling/dist/MorphRealeaseV1.jar faFile=file.fa itemOut=item.txt
'''

#### Output format

The SV output file contains predicted SV position on the genome. Additional information includes SupType, Pattern, Region (genome region spanned by pattern), weights, ratio (allele fraction of each Super-Item), orientation (orientation of reads in Super-Item). A single SV can be supported by more than one evidence, more evidence indicates more confident calls.
* SupType=ARP_Span: indicates SV is combined by two patterns that is able to link together through read-pair. Each pattern of the SV might be a breakpoint. Number of read pairs support such relation is provided.
* SupType=Self: a pattern is self-linked through read pairs. Then we estimate potential breakpoint based on abnormal read pairs. Number, quality and weight of these supporting read pairs is provided.
* SupType=Split: indicates SV is discovered based on split alignment. We provide additional information, such as number of split read support, split read mapping quality.
* SupType=Cross: indicates SV is discovered based on local sequence cross links. Additional information includes number of reads support the cross, the maximum cross matched sequence length.
* SupType=Realign: for region with multiple clipped Super-Items, we usually do realignment, this helps discover INDELS and small SVs. Information includes minus and plus strand support read is provided.
* SupType=OEM: one-end-unmapped reads formed cluster may indicate potential insertion breakpoint near OEM Super-Item. This is not a very confident evidence, but we report such abnormal.

# Classify and score SVs

Todo...

# Contact
If you have questions or encouter problems, please feel free to contact: jiadong324@gmail.com, ccxtbut@gmail.com.

License
----

MIT


**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)


   [dill]: <https://github.com/joemccann/dillinger>
   [git-repo-url]: <https://github.com/joemccann/dillinger.git>
   [john gruber]: <http://daringfireball.net>
   [df1]: <http://daringfireball.net/projects/markdown/>
   [markdown-it]: <https://github.com/markdown-it/markdown-it>
   [Ace Editor]: <http://ace.ajax.org>
   [node.js]: <http://nodejs.org>
   [Twitter Bootstrap]: <http://twitter.github.com/bootstrap/>
   [jQuery]: <http://jquery.com>
   [@tjholowaychuk]: <http://twitter.com/tjholowaychuk>
   [express]: <http://expressjs.com>
   [AngularJS]: <http://angularjs.org>
   [Gulp]: <http://gulpjs.com>

   [PlDb]: <https://github.com/joemccann/dillinger/tree/master/plugins/dropbox/README.md>
   [PlGh]: <https://github.com/joemccann/dillinger/tree/master/plugins/github/README.md>
   [PlGd]: <https://github.com/joemccann/dillinger/tree/master/plugins/googledrive/README.md>
   [PlOd]: <https://github.com/joemccann/dillinger/tree/master/plugins/onedrive/README.md>
   [PlMe]: <https://github.com/joemccann/dillinger/tree/master/plugins/medium/README.md>
   [PlGa]: <https://github.com/RahulHP/dillinger/blob/master/plugins/googleanalytics/README.md>
