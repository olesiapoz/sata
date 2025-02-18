"""Replay requests from an HTTP access log file.

- Takes time between requests into account, with option to speed up the replay.
- Allows one to send all requests to a selected server (proxy).
"""

import sys
import csv
import time

from datetime import datetime
from optparse import OptionParser

# Constants that specify access log format (indices
# specify position after splitting on spaces)
HOST_INDEX = 0
TIME_INDEX = 3
PATH_INDEX = 1

def main(filename,  speedup=1):
    """Setup and start replaying."""
    requests = _parse_logfile(filename)
    _replay(requests, speedup)

def new(date, asd):
    a = 0
    for each in asd:
        if date == each[0]:
            a=a+1
    return date.strftime("%H:%M:%S") + "," + str(a)



def _replay(requests, speedup):
    """Replay the requests passed as argument"""
    #total_delta = requests[-1][0] - requests[0][0]

    #last_time = requests[0][0]
    print("dates")
    dates = list(set([each[0] for each in requests]))
    outfile = open("reduced_olympics.csv", "w")
    for date in dates:
        line = new(date,requests)
        outfile.write(line)
        outfile.write('\n')
    outfile.close()


def _setup_http_client(proxy):
    """Configure proxy server and install HTTP opener"""
    proxy_config = {'http': proxy} if proxy else {}
    proxy_handler = urllib2.ProxyHandler(proxy_config)
    opener = urllib2.build_opener(proxy_handler)
    urllib2.install_opener(opener)

def _parse_logfile(filename):
    """Parse the logfile and return a list with tuples of the form
    (<request time>, <requested host>, <requested url>)
    """
    logfile = open(filename, "r")
    requests = []
    for line in logfile:
        parts = line.split(" ")
        time_text = parts[TIME_INDEX][1:]

        request_time = datetime.strptime(time_text, "%d/%b/%Y:%H:%M:%S")
       # print(request_time)
        host = parts[HOST_INDEX]
        path = parts[PATH_INDEX]
        requests.append((request_time, host))

    if not requests:
        print("Seems like I don't know how to parse this file!")
    return requests

if __name__ == "__main__":
    """Parse command line options."""
    usage = "usage: %prog [options] logfile"
    parser = OptionParser(usage)
    parser.add_option('-s', '--speedup',
        help='make time run faster by factor SPEEDUP',
        dest='speedup',
        type='int',
        default=1)
    (options, args) = parser.parse_args()
    if len(args) == 1:
        main(args[0])
    else:
        parser.error("incorrect number of arguments")
