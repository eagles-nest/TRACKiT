#!/usr/bin/env python2
import serial
import RPi.GPIO as GPIO
import time,sys
GPIO.setmode(GPIO.Board)
from curses import ascii

#get the serial object
gsm=serial.Serial("/dev/ttyS0",baudrate=9600,timeout=3)

time.sleep(1)

# get latlong from positions.txt
def getlatlong():
	f=open("position.txt","r")
	contents=f.readline()
	data=contents.split(",")
	return data[0],data[1]

def getIMEI():
	#initialize the AT
	gsm.write("AT\r")
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	time.sleep(2)
	gsm.write("AT+CGSN\r")
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	return reply


def send_sms(text):
	#initialize the AT
	gsm.write("AT\r")
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	print(reply)
	#set the mode to text mode
	gsm.write("AT+CMGF=1\r")
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	print(reply)
	#delete all SMS in memory
	gsm.write('AT+CMGDA="DEL ALL"\r')
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	print(reply)
	#remove anything from the input buffer
	gsm.flushInput()
	reply=gsm.read(gsm.inWaiting())
	print("listening for any incoming sms...")
	print text
	while True:
		if reply != "":
			#read the new message
			gsm.write('AT+CMGR=1\r')
			time.sleep(3)
			reply=gsm.read(gsm.inWaiting())
			print("the received content is ->")
			print(reply)
			#reply=+CMGR: "REC UNREAD","+489300222",,"07/02/18.00:05:10+32"
			x=reply.split(,);
			number=x[1];
			print("the number is->")
			print(number)
			if "LOCATE" in reply:
				#prepare to send latlong to sender
				gsm.write("AT+CSMP=17,167,0,16\r")
				time.sleep(3)
				#set mode to gsm
				gsm.write('AT+CSCS="GSM"\r')
				time.sleep(3)
				#send latlong as msg
				gsm.write('AT+CMGS="+254718145956"\r')
				time.sleep(2)
				gsm.write(text)
				gsm.write(ascii.ctrl('z'))
				time.sleep(5)
				reply=gsm.read(gsm.inWaiting())
				print(reply)
				#message sent

def runme():
	x=getlatlong()
	lat=x[0]#latitude
	lon=x[1]#longitude
	text="https://maps.google.com/maps?q="+lat+","+ lon
	send_sms(text)
	#send_cords()

def send_cords(lat,lon):
	#sends cordinates to mysql database
	#ensure gsm already connected to network
	lat=lat
	lon=lon
	gsmIMEI=getIMEI()
	#gprs attach
	gsm.write('AT+CGATT=1\r')
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	print(reply)
	#set connection type to GPRS
	gsm.write('AT+SAPBR=3,1,"CONTYPE","GPRS"\r')
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	print(reply)
	#set APN type of the network provider
	gsm.write('AT+SAPBR=3,1,"APN","internet"\r')
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	print(reply)
	#connect to GPRS network
	gsm.write('AT+SAPBR=1,1\r')
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	print(reply)
	#initialize HTTP service
	gsm.write('AT+HTTPINIT\r')
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	print(reply)
	#set up the parameters
	url="https://2db65f43.ngrok.io/latlong.php?lat="+lat+"&lon="+lon+"&gsmIMEI="+gsmIMEI
	print(url)
	#set the URL
	gsm.write('AT+HTTPPARA="URL",url\r')
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	print(reply)
	#set context id 
	gsm.write('AT+HTTPPARA=CID,1\r')
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	print(reply)
	#set the method
	gsm.write('AT+HTTPACTON=0\r')
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	print(reply)
	#terminate the session
	gsm.write('AT+HTTPTERM\r')
	time.sleep(2)
	reply=gsm.read(gsm.inWaiting())
	print(reply)
