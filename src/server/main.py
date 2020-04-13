from socket import *
import threading
import time
import random

random.seed(time.time())
lock = threading.Lock()

connected_com = dict()

def receive(source_sock, target_sock):
    while True:
        recvData = source_sock.recv(1024)
        target_sock.send(recvData)
        

def dist(sock, addr):
    while True:
        recvData = sock.recv(1024).decode('utf-8')
        if( recvData == 'com' ): # from com 

            pw = f'0000'
            while(pw == '0000' or connected_com.get(pw,0) != 0 ):
                pw = f'{random.randrange(1, 10**4):04}'

            lock.acquire()
            connected_com[pw] = sock
            lock.release()

            sandData = pw.encode('utf-8')
            sock.send(sandData)
            break

        else : # from mobile, data : password 
            if(connected_com.get(recvData,0) != 0):

                sendData = 'Connected'
                sock.send(sendData.encode('utf-8'))


                receiver = threading.Thread(target=receive, args=(sock, connected_com[recvData]))
                receiver.start()
                break 
            else: #
                sendData = 'Invalid Password'
                sock.send(sendData.encode('utf-8'))
                


port = 8081

serverSock = socket(AF_INET, SOCK_STREAM)
serverSock.bind(('', port))
serverSock.listen(1)

while True:
    
    print('%d번 포트로 접속 대기중...'%port)

    connectionSock, addr = serverSock.accept()

    print(str(addr), '에서 접속되었습니다.')

    disting = threading.Thread(target=dist, args=(connectionSock, addr))

    disting.start()

    time.sleep(1)
    pass