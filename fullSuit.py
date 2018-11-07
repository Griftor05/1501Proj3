import os

os.chdir("src")
os.system("javac *.java")
os.chdir("..")
os.system("python cleaner.py")
os.system("python tester.py")