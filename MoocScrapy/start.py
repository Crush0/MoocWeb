import sys

from scrapy import cmdline

args = 'scrapy crawl'
for arg in range(0,len(sys.argv)):
    if arg == 0:
        continue
    if arg == 1:
        args += ' ' + sys.argv[arg]
    else:
        args += ' -a ' + sys.argv[arg]
# args += ' --nolog'
cmdline.execute(args.split())