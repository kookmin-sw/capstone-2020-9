from socket import *
import threading
import time
import pyautogui 
from ast import literal_eval
import sys
from PyQt5 import QtWidgets
from PyQt5 import QtGui
from PyQt5 import uic
from PyQt5 import QtCore
from PyQt5.QtCore import pyqtSlot
 

def point_on_screen(recvData):
    try:
        x_ratio, y_ratio = literal_eval(recvData)
        point_x = WIDTH * x_ratio[0] / sum(x_ratio)
        point_y = HEIGHT * y_ratio[0] / sum(y_ratio)

        pyautogui.click(x=point_x, y=point_y)
    except: 
        pass


def make_connection():
    port = 8081

    clientSock = socket(AF_INET, SOCK_STREAM)
    clientSock.connect(('127.0.0.1', port))

    print('접속 완료')

    sendData = 'com'
    clientSock.send(sendData.encode('utf-8'))

    password = clientSock.recv(1024).decode('utf-8')

    #화면에 비밀번호 보여주기 
    print("pw : " + password)
    return password, clientSock


def pointing_start(sock):
    # 좌표값 
    recvData = ''
    while True:
        recvData = sock.recv(1024).decode('utf-8')
        if( recvData == 'test' ) :
            continue
        elif( recvData == 'disconnected with other device'):
            sock.close()
            break # 재접속 여부 확인 
        elif( recvData == 'closed' ):
            sock.close()
            break

        print('좌표', recvData)  # '(12.3, 3.5), (5.4, 2.8)'
        
        p = threading.Thread(target = point_on_screen, args=(recvData,), daemon=True )
        p.start()
    
    return recvData

def connectionStart(sock):
    #연결 성공 
    while True:
        recvData = sock.recv(1024).decode('utf-8')
        if( recvData == 'Connected' ):
            print(recvData)
            break

    res = pointing_start(sock)

    # 종료 / 재접속 gui
    # print( res )
    ''' 
    if( 재접속 ):
        continue
    elif( 종료 ):
        break
    '''
    


class Form(QtWidgets.QDialog):
    def __init__(self, parent=None):
        QtWidgets.QDialog.__init__(self, parent)
        self.ui = uic.loadUi("tos.ui", self)
        self.ui.setWindowTitle('Touch On Screen')
        self.ui.login_widget.hide()
        self.ui.how_to_widget.hide()
        self.ui.show()
        

    # make connection & generate number
    # 입력 들어오면 gui
    @pyqtSlot()
    def generate_num(self): # btn 
        #self.ui.pw_show.setText("1234")

        pw, sock = make_connection()
        #화면에 pw 보여주기 gui
        self.ui.pw_label_2.setText(pw)

        waiting = threading.Thread(target=connectionStart, args=(sock,))
        waiting.start()

            
        


#fixed size = 365 305        
if __name__ == '__main__':
    WIDTH, HEIGHT = pyautogui.size()  
    print('width={0}, height={1}'.format(WIDTH, HEIGHT))

    app = QtWidgets.QApplication(sys.argv)
    w = Form()
    sys.exit(app.exec())
    os._exit
