from socket import *
import threading
import time
import random

random.seed(time.time())
lock = threading.Lock()

connected_com = dict()
connected_mob = dict()

def getLog():
    while True:
        now = time.localtime()
        print("%02d:%02d:%02d" % ((now.tm_hour+9)%24, now.tm_min, now.tm_sec) + str(connected_com) + str(connected_mob))
        time.sleep(300)

def run():
    while True: 
        try:
            s = input()
            exec(s)
        except error as m:
            print(m)


def receive(connection_id):
    source_sock = connected_mob[connection_id]
    target_sock = connected_com[connection_id]
    while True:
        try:
            recvData = source_sock.recv(1024)#check source alive
            if not recvData:    
                source_sock.close()
                break
            target_sock.send(recvData)
        except:
            target_sock.send('disconnected with other device'.encode('utf-8'))
            break
   

def check(connection_id):
    source_sock = connected_mob[connection_id]
    target_sock = connected_com[connection_id]
    receiver = threading.Thread(target=receive, args=(connection_id,), daemon=True)
    receiver.start()

    try:
        while True:
            recvData = target_sock.recv(1024) # check target alive
            if not recvData:
                lock.acquire()
                del connected_com[connection_id]
                del connected_mob[connection_id]
                lock.release()
                target_sock.close()
                break

    except OSError :
        lock.acquire()
        del connected_com[connection_id]
        del connected_mob[connection_id]
        lock.release()
        target_sock.close()
        source_sock.send('disconnected with other device'.encode('utf-8'))
        time.sleep(0.5)
        source_sock.close() ##reconnect? exti?


def dist(sock):
    while True:
        recvData = sock.recv(1024).decode('utf-8')
        if( recvData == 'com' ): # from com 

            pw = f'0000'
            while(pw == '0000' or connected_com.get(pw,0) != 0 ):
                pw = f'{random.randrange(1, 10**4):04}'

            lock.acquire()
            connected_com[pw] = sock
            connected_mob[pw] = 0
            lock.release()

            sandData = pw.encode('utf-8')
            sock.send(sandData)
            break

        else : # from mobile, data : password 
            try:    
                if(connected_mob[recvData] == 0):
                    sendData = 'Connected'.encode('utf-8')
                    sock.send(sendData)
                    lock.acquire()
                    connected_mob[recvData] = 1
                    lock.release()
                    time.sleep(10)
                    break

                else:
                    sendData = 'Connected'.encode('utf-8')
                    connected_com[recvData].send(sendData)
                    sock.send(sendData)

                    lock.acquire()
                    connected_mob[recvData] = sock
                    lock.release()

                    checking = threading.Thread(target=check, args=(recvData,))
                    checking.start()
                    break 

            except KeyError:
                sendData = 'Invalid Password'
                sock.send(sendData.encode('utf-8'))

            except OSError: 
                sendData = 'Invalid Password'
                sock.send(sendData.encode('utf-8'))
                lock.acquire()
                del connected_mob[recvData]
                del connected_com[recvData]
                lock.release()


port = 8081

serverSock = socket(AF_INET, SOCK_STREAM)
serverSock.bind(('', port))
serverSock.listen(1)

exe = threading.Thread(target= run)
exe.start()

logging = threading.Thread(target=getLog)
logging.start()

if __name__ == '__main__' :
    while True:
        
        print( connected_com)

        connectionSock, addr = serverSock.accept()

        print(str(addr), 'connected.')

        disting = threading.Thread(target=dist, args=(connectionSock, ))
        disting.start()

        #time.sleep(1)
        pass