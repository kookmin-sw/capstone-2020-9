from socket import *
import threading
import time


def send(sock):
    while True:
        sendData = input('>>>')
        sock.send(sendData.encode('utf-8'))


def receive(sock):
    while True:
        recvData = sock.recv(1024)
        if not recvData:
            print("end")
            sock.close()
            break
        print('ans :', recvData.decode('utf-8'))

port = 8081

clientSock = socket(AF_INET, SOCK_STREAM)
clientSock.connect(('127.0.0.1', port))

print('접속 완료')

sender = threading.Thread(target=send, args=(clientSock,))
receiver = threading.Thread(target=receive, args=(clientSock,))

sender.start()
receiver.start()



'''
모바일에서 해야할 일
1. 접속시 비밀번호 전송 

접속 ok일때 
2. 'disconnected with other device'도착 : socket close 
-> socket 재 연결 후  재접속 비밀번호 입력
'''