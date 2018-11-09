import os

os.chdir("src")
os.system("javac *.java")
os.chdir("..")
os.system("python cleaner.py")
os.system("python tester.py r")
os.system("python validateFolder.py")

os.system("python cleaner.py")
os.system("python tester.py n")
os.system("python validateFolder.py")