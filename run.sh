#!/bin/bash

ps -ef | grep java | grep -v "grep" | awk '{print $2}' | xargs kill -2
rm log.txt 2> /dev/null
make runServerTest 2>> log.txt >>log.txt &