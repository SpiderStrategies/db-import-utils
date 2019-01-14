#!/bin/sh -l

ssh-keyscan -H github.com >> ~/.ssh/known_hosts
cd /github/
git clone git@github.com:SpiderStrategies/encryption-utils.git
cd /github/encryption-utils
gradle fatJar
cd /github/workspace
gradle test
