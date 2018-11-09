import os
import sys
import time

os.system("python cleaner.py")
filenamelist = os.listdir("TestFiles")
os.chdir("original_code")
os.system("javac *.java")

for filename in filenamelist:
	newfilename = filename[0:-3] + "lzw"
	newexpandedname = filename[0:-4] + "(expanded)" + filename[-4:]
	start = time.time()
	os.system("java LZW -  < ..\\TestFiles\\" + filename + " > ..\\TestFiles\\" + newfilename)
	end = time.time()
	print("Compressing " + filename + " took %.5f seconds." % (end - start))
	start = time.time()
	os.system("java LZW + < ..\\TestFiles\\" + newfilename + " > ..\\TestFiles\\" + newexpandedname)
	end = time.time()
	print("Decompressing " + filename + " took %.5f seconds." % (end - start))
	ratio = os.stat("..\\TestFiles\\" + newfilename).st_size / os.stat("..\\TestFiles\\" + filename).st_size
	print("Achieved a compression ratio of %.5f" % ratio)
	
	print()
	