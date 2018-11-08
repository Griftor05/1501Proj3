import os

filenamelist = os.listdir("TestFiles")
os.chdir("TestFiles")

for filename in filenamelist:
    if "(expanded)" in filename or ".lzw" in filename or filename == "Thumbs.db":
        os.remove(filename);