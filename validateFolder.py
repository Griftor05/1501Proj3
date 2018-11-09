import os

os.chdir("TestFiles")
testNames = os.listdir(".");

for filename in testNames:
	name1 = filename
	nameStripped = filename[:-4]
	nameEnd = filename[-4:]
	name2 = nameStripped + "(expanded)" + nameEnd
	os.system("fc /B " + name1 + " " + name2)