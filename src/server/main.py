from socket import *
import threading
import time
import random
import json
import pymysql

random.seed(time.time())
lock = threading.Lock()

connected_com = dict()
connected_dev = dict()
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
        print("flag: {}".format(recvData))

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

        elif( recvData =='conn' ):
            recvData = sock.recv(1024).decode('utf-8')
            conn_info = json.loads(recvData)

            connected_dev[conn_info["id"], conn_info["did"]] = pw
            sock.send(pw.encode('utf-8'))

            # conn_info["id" | "did"]
            break

        elif( recvData == 'login' ):
            recvData = sock.recv(1024).decode('utf-8')
            login_info = json.loads(recvData)
            print("login {}".format(recvData))
            # db에서 정보 확인

            if(login_info.get("did",0) == 0 ): #phone
                sql = 'select count(*) from user_info where id = "{}" and pw = "{}";'.format(login_info["id"], login_info["pw"])

                curs.execute(sql)
                rows = curs.fetchall()
                if(rows[0][0] == 1):
                    sql = 'select deviceName from conn_info where id = "{}";'.format(login_info["id"])
                    curs.execute(sql)
                    rows = curs.fetchall()
                    conn_list = ''
                    for r in rows: 
                        if( connected_dev.get([login_info["id"],r[0]], 0) != 0 ):
                            conn_list += r[0] + ','

                    sock.send(conn_list.encode('utf-8'))
                else : 
                    sock.send('fail'.encode('utf-8'))

            else: # pc
                
                sql = 'insert conn_info(id, macAddr, DeviceName) values ("{}", "{}", "{}");'.format(login_info["id"], login_info["mac"], login_info["did"])
                try:
                    curs.execute(sql)
                    rows = curs.fetchall()

                    pw = f'0000'
                    while(pw == '0000' or connected_com.get(pw,0) != 0 ):
                        pw = f'{random.randrange(1, 10**4):04}'

                    lock.acquire()
                    connected_com[pw] = sock
                    connected_mob[pw] = 0
                    connected_dev[login_info["id"], login_info["did"]] = pw
                    lock.release()
                    
                    sock.send('ok'.encode('utf-8'))

                except:
                    sock.send('fail'.encode('utf-8'))

            print("login end")
            break

        elif( recvData == 'signup'):
            recvData = sock.recv(1024).decode('utf-8')
            print("signup {}",format(recvData))
            signup_info = json.loads(recvData)  #id, pw, name, email
            print(signup_info)
            sql = 'insert user_info(id, pw, name, email) values ("{}", "{}", "{}", "{}");'.format(signup_info["id"], signup_info["pw"], signup_info["name"], signup_info["email"])
            try:
                curs.execute(sql)
                rows = curs.fetchall()
                sock.send('ok'.encode('utf-8'))
            except :
                sock.send('fail'.encode('utf-8'))

            print("signup end")
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


#mysql 5.7.22 

# MySQL Connection 연결
db_conn = pymysql.connect(host='database-1.clechpc6fvlz.us-east-1.rds.amazonaws.com', 
port = 3306, user='admin', password='puri142857', db='capstone', charset='utf8')
 
# # Connection 으로부터 Cursor 생성
curs = db_conn.cursor()
 
# # SQL문 실행
# sql = "select * from customer"
# curs.execute(sql)
 
# # 데이타 Fetch
# rows = curs.fetchall()
# print(rows)     # 전체 rows
# # print(rows[0])  # 첫번째 row: (1, '김정수', 1, '서울')
# # print(rows[1])  # 두번째 row: (2, '강수정', 2, '서울')
 
# Connection 닫기
# db_conn.close()


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

    db_conn.close()

db_conn.close()