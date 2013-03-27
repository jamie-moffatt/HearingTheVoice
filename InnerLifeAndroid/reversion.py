import subprocess

p = subprocess.Popen("git log -1 --pretty=format:'%h'", shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
sha = p.stdout.readlines()[0]
print sha
p = subprocess.Popen("git log --oneline | wc -l", shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
rev = p.stdout.readlines()[0]
print rev.strip()

#!/bin/env python
import os
from xml.dom.minidom import parse
dom = parse("AndroidManifest.xml")
dom.documentElement.setAttribute("android:versionName","1.0." + rev.strip() + "-" + sha)
f = os.open("AndroidManifest.xml", os.O_RDWR)
os.write( f, dom.toxml() )