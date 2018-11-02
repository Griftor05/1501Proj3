import os

filenamelist = os.listdir("TestFiles")
os.chdir("src")

for filename in filenamelist:
	newfilename = filename[0:-3] + "lzw"
	newexpandedname = filename[0:-4] + "(expanded)" + filename[-4:]
	os.system("java LZWmod - n < ..\\TestFiles\\" + filename + " > ..\\TestFiles\\" + newfilename)
	os.system("java LZWmod + < ..\\TestFiles\\" + newfilename + " > ..\\TestFiles\\" + newexpandedname)
	print("Did " + filename)