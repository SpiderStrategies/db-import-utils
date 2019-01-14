#!/bin/sh -l

cd /github/
git clone git@github.com:SpiderStrategies/encryption-utils.git
cd /github/encryption-utils
gradle fatJar
cd /github/workspace
gradle test
