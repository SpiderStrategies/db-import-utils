#!/bin/sh -l

cd /github/
curl https://github.com/SpiderStrategies/encryption-utils/archive/master.zip -o /github/master.zip
unzip master.zip
mv master encryption-utils
cd /github/encryption-utils
gradle fatJar

cd /github/workspace
gradle test
