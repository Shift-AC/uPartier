#!/bin/bash

dir=`pwd`
rt=${dir%/upartier/*}/upartier

cd $rt

i=0
while [ $i -lt 33 ]
do
	make runClient 2> test/log/$i.log
	let i=$i+1
done 
