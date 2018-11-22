#!/usr/bin/env python
# Reference: Ryan M. Layer (Hall Laboratory Quinlan Laboratory)
# We adopt the idea of BAM default parameter estimation from Ryan M. Layer used in LUMPY.
#
#
# This script is used to get get read length, library insert size mean and standard deviation.
#
# Author: Jiadong Lin (Xi'an JiaoTong University)
# Contact: jiadong324@gmail.com

import sys
import re
import os
import numpy as np
from operator import itemgetter
from optparse import OptionParser

FLAG = 1
CIGAR = 5
REFNAME = 2
MATE_REFNAME = 6
ISIZE = 8

cigarPattern = '([0-9]+[MIDNSHP])'
cigarSearch = re.compile(cigarPattern)
atomicCigarPattern = '([0-9]+)([MIDNSHP])'
atomicCigarSearch = re.compile(atomicCigarPattern)

parser = OptionParser()

parser.add_option("-X",
    dest="X",
    type="int",
    help="Number of stdevs from mean to extend")

parser.add_option("-N",
    dest="N",
    type="int",
    help="Number to sample")


parser.add_option("-m",
    dest="mads",
    type="int",
    default=30,
    help="Outlier cutoff in # of median absolute deviations (unscaled, upper only)")

parser.add_option("-w",
      dest="work_dir",
      help="Working directory")

def unscaled_upper_mad(xs):
    """Return a tuple consisting of the median of xs followed by the
    unscaled median absolute deviation of the values in xs that lie
    above the median.
    """
    med = np.median(xs)
    return med, np.median(xs[xs > med] - med)

def getReadLenFromCigar(cigar):
    readLen = 0
    if (cigar != '*'):
        cigarOpStrings = cigarSearch.findall(cigar)

        for opString in cigarOpStrings:
            cigarOpList = atomicCigarSearch.findall(opString)[0]
            readLen += int(cigarOpList[0])
    return readLen

(options, args) = parser.parse_args()



required = 97
restricted = 3484
flag_mask = required | restricted

L = []
c = 0

read_len_is_set = False
read_len = 0

for l in sys.stdin:

    valid_read_len = False
    if c >= options.N:
        break
    A = l.rstrip().split('\t')
    cigar = A[CIGAR]
    if not read_len_is_set:
        read_len = getReadLenFromCigar(cigar)
        read_len_is_set = True
    flag = int(A[FLAG])
    refname = A[REFNAME]
    mate_refname = A[MATE_REFNAME]
    isize = int(A[ISIZE])

    valid = mate_refname == '=' and flag & flag_mask == required and isize >= 0

    if valid:
        c += 1
        L.append(isize)


L = np.array(L)
L.sort()
med, umad = unscaled_upper_mad(L)
upper_cutoff = med + options.mads * umad
L = L[L < upper_cutoff]
mean = int(np.mean(L))
stdev = int(np.std(L))

bamCfgFile = options.work_dir + '/bam.cfg'
writer = open(bamCfgFile,'w')



outStr = 'mean:' + str(mean) + '\nstdev:' + str(stdev) + '\nreadlen:' + str(read_len) + '\nworkDir:' + options.work_dir

writer.write(outStr)
