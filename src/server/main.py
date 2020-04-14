from socket import *
import threading
import time
import random

random.seed(time.time())
lock = threading.Lock()

connected_com = dict()

def run():
    while True:
        s = input()
        exec(s)


def receive(source_sock, target_id):
    target_sock = connected_com[target_id]
    while True:
        try:
            recvData = source_sock.recv(1024)#source 살아있는지 확인
            if not recvData:    
                source_sock.close()
                break
            target_sock.send(recvData)
        except:
            target_sock.send('disconnected with other device'.encode('utf-8'))
            break
   

def check(source_sock, target_id):
    target_sock = connected_com[target_id]
    receiver = threading.Thread(target=receive, args=(source_sock, target_id), daemon=True)
    receiver.start()

    try:
        while True:
            recvData = target_sock.recv() # target 살아있는지 확인
            if not recvData:
                lock.acquire()
                del connected_com[target_id]
                lock.release()
                target_sock.close()

    except OSError :
        lock.acquire()
        del connected_com[target_id]
        lock.release()
        target_sock.close()
        source_sock.send('disconnected with other device'.encode('utf-8'))
        time.sleep(0.5)
        source_sock.close() ## 종료시켜? 아니면 재접속? 
     

def dist(sock):
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
            try:
                sendData = 'Connected'.encode('utf-8')
                connected_com[recvData].send(sendData)
                sock.send(sendData)

                checking = threading.Thread(target=check, args=(sock, recvData))
                checking.start()
                break 

            except KeyError:
                sendData = 'Invalid Password'
                sock.send(sendData.encode('utf-8'))

            except OSError: 
                sendData = 'Invalid Password'
                sock.send(sendData.encode('utf-8'))
                lock.acquire()
                del connected_com[recvData]
                lock.release()


port = 8081

serverSock = socket(AF_INET, SOCK_STREAM)
serverSock.bind(('', port))
serverSock.listen(1)

exe = threading.Thread(target= run)
exe.start()


while True:
    
    print('%d번 포트로 접속 대기중...'%port)
    print( connected_com)

    connectionSock, addr = serverSock.accept()

    print(str(addr), '에서 접속되었습니다.')

    disting = threading.Thread(target=dist, args=(connectionSock, ))

    disting.start()

    time.sleep(1)
    pass