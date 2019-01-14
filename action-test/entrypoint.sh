#!/bin/sh -l
id
echo $PATH
pwd
ls /home
ls -la
gradle --debug test
sudo gradle --debug test
chmod -R 777 *
gradle --debug test
