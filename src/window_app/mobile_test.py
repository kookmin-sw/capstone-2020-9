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
clientSock.connect(('15.164.116.157', port))
#clientSock.connect(('127.0.0.1', port))

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

-------------------------------------------
핸드폰 어플 시나리오
모바일에서 번호 입력 후 전송
-> 1. 맞지 않는 번호 : 'Invalid Password' 수신
-> 2. 맞는 번호 : 'Connected' 수신
맞는 번호로 진입
-> 송신 : 계속?(모션 인식할때마다 좌표값 전송)
-> 수신 : 대기하다가 'disconnected with other device' 메시지 오면 재접속 필요 

'''
