from socket import *
import threading
import time
import pyautogui 
from ast import literal_eval

def point_on_screen(recvData):
    try:
        x_ratio, y_ratio = literal_eval(recvData)
        point_x = WIDTH * x_ratio[0] / sum(x_ratio)
        point_y = HEIGHT * y_ratio[0] / sum(y_ratio)

        pyautogui.click(x=point_x, y=point_y)
    except: 
        pass


WIDTH, HEIGHT = pyautogui.size()  
print('width={0}, height={1}'.format(WIDTH, HEIGHT))

port = 8081

clientSock = socket(AF_INET, SOCK_STREAM)
clientSock.connect(('127.0.0.1', port))

print('접속 완료')

sendData = 'com'
clientSock.send(sendData.encode('utf-8'))

recvData = clientSock.recv(1024).decode('utf-8')

#화면에 비밀번호 보여주기 
print("pw : " + recvData)

#연결 성공 
while True:
    recvData = clientSock.recv(1024).decode('utf-8')
    if( recvData == 'Connected' ):
        print(recvData)
        break

# 좌표값 
while True:
    recvData = clientSock.recv(1024).decode('utf-8')
    if( recvData == 'test' ) :
        continue
    elif( recvData == 'disconnected with other device'):
        break 

    print('좌표', recvData)  # '(12.3, 3.5), (5.4, 2.8)'
    
    p = threading.Thread(target = point_on_screen, args=(recvData,), daemon=True )
    p.start()
    
