#!/bin/sh -l

#handle encryption-utils dependency
cd ../
wget https://github.com/SpiderStrategies/encryption-utils/archive/master.zip
unzip master.zip
mv encryption-utils-master encryption-utils
cd encryption-utils
gradle dependencies
gradle jar

#switch back to db-import-utils
cd ../workspace
gradle dependencies
gradle test
