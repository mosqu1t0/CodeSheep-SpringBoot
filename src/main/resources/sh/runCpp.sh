#!/bin/bash

echo $1 | ./"Cpp$2" &> ./"Cpp$2.out" & # 异步 哈哈

if [ `ps -ef | grep Cpp$2 | grep -v grep | cut -d ' ' -f 2` ]
then
    sleep 1
    if [ `ps -ef | grep Cpp$2 | grep -v grep | cut -d ' ' -f 2` ]
    then
        kill -9 $(ps -ef | grep Cpp$2 | grep -v grep | cut -d ' ' -f 2)
        echo -e "\n\nOver Time" >> "./Cpp$2.info"
    fi
fi


