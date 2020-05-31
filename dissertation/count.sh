#!/bin/bash
# numprint package required
/usr/bin/perl /Library/TeX/texbin/texcount -inc -brief dissertation.tex | /usr/bin/tail -n1 | /usr/bin/sed 's/\([0-9]*\).*/\\numprint{\1}/'