import os
import sys
import time

filenamelist = os.listdir("TestFiles")
os.chdir("src")

if len(sys.argv) < 2:
	print("You have to specify n or r mode!")
	exit()

for filename in filenamelist:
	newfilename = filename[0:-3] + "lzw"
	newexpandedname = filename[0:-4] + "(expanded)" + filename[-4:]
	start = time.time()
	os.system("java LZWmod - " + sys.argv[1] + " < ..\\TestFiles\\" + filename + " > ..\\TestFiles\\" + newfilename)
	end = time.time()
	print("Compressing " + filename + " took %.5f seconds." % (end - start))
	start = time.time()
	os.system("java LZWmod + < ..\\TestFiles\\" + newfilename + " > ..\\TestFiles\\" + newexpandedname)
	end = time.time()
	print("Decompressing " + filename + " took %.5f seconds." % (end - start))
	print("Oh, and that was with -" + sys.argv[1])
	print("\n")
	