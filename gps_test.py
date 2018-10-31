#!/usr/bin/env python2
#author : Evans Mwenda
#Date   : 23/9/18
#gets the gps data from receiver and stores it in position.txt
import serial
import time,os

gps=serial.Serial("/dev/ttyUSB0",baudrate=9600)
while True:
	line=gps.readline()
	data=line.split(",")
	if(data[0]=='$GPRMC'):
		if data[2] == "A":
			#gps has a lock on satellites
			lat=str(data[3])
			lon=str(data[5])
			if(data[4]=="S"):
				lat="-"+lat#negate lat for accurate location
				#convert the values to proper ones
				#print(lat,lon)->(-01160.2900,03653.4880)
				lat1=lat[0:3]#->-01
				lon1=lon[0:3]#->036
				lat2=lat[3:]#->16.2800
				lon2=lon[3:]#->53.4880
				lat2=float(lat2)/60
				lon2=float(lon2)/60
				lat=float(lat1)-lat2#->-1.2713
				lon=float(lon1)+lon2#->36.8914
				print(lat,lon)
